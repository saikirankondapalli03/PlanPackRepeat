# Data sources
data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}

# EKS Cluster Module
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name               = var.cluster_name
  kubernetes_version = var.kubernetes_version

  # Cluster endpoint configuration
  endpoint_public_access  = var.endpoint_public_access
  endpoint_private_access  = var.endpoint_private_access
  endpoint_public_access_cidrs = var.endpoint_public_access ? ["0.0.0.0/0"] : []

  # Enable cluster creator admin permissions
  enable_cluster_creator_admin_permissions = var.enable_cluster_creator_admin_permissions

  # VPC configuration
  vpc_id     = var.vpc_id
  subnet_ids = var.subnet_ids

  control_plane_subnet_ids = length(var.control_plane_subnet_ids) > 0 ? var.control_plane_subnet_ids : var.subnet_ids

  # Cluster addons
  addons = {
    coredns = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
    vpc-cni = {
      most_recent = true
    }
    eks-pod-identity-agent = {
      most_recent = true
    }
  }

  # EKS Managed Node Groups
  eks_managed_node_groups = {
    main = {
      name = "${var.cluster_name}-node-group"

      instance_types = var.node_group_instance_types

      min_size     = var.node_group_min_size[var.environment]
      max_size     = var.node_group_max_size[var.environment]
      desired_size = var.node_group_desired_size[var.environment]

      # Use AL2023 AMI
      ami_type = "AL2023_x86_64_STANDARD"

      # Disk configuration
      disk_size = 50

      # Labels
      labels = {
        Environment = var.environment
        NodeGroup   = "main"
      }

      # Taints (optional)
      # taints = {}

      # Update configuration
      update_config = {
        max_unavailable_percentage = 50
      }
    }
  }

  # CloudWatch logging
  enabled_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]

  # Tags
  tags = merge(
    var.tags,
    {
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  )
}

# Kubernetes Provider Configuration
provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate  = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args = [
      "eks",
      "get-token",
      "--cluster-name",
      module.eks.cluster_name
    ]
  }
}

provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate  = base64decode(module.eks.cluster_certificate_authority_data)
    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args = [
        "eks",
        "get-token",
        "--cluster-name",
        module.eks.cluster_name
      ]
    }
  }
}

# Wait for cluster to be ready
resource "time_sleep" "wait_for_cluster" {
  depends_on = [module.eks]

  create_duration = "30s"
}

# Ingress Controller (NGINX)
resource "helm_release" "ingress_nginx" {
  count = var.ingress_enabled ? 1 : 0

  name       = "ingress-nginx"
  repository = "https://kubernetes.github.io/ingress-nginx"
  chart      = "ingress-nginx"
  version    = "4.8.3"
  namespace  = "ingress-nginx"
  create_namespace = true

  set {
    name  = "controller.service.type"
    value = "LoadBalancer"
  }

  set {
    name  = "controller.service.annotations.service\\.beta\\.kubernetes\\.io/aws-load-balancer-type"
    value = "nlb"
  }

  depends_on = [
    time_sleep.wait_for_cluster
  ]
}

# Namespace for application
resource "kubernetes_namespace" "app_namespace" {
  metadata {
    name = var.app_name
    labels = {
      Environment = var.environment
    }
  }

  depends_on = [time_sleep.wait_for_cluster]
}

# ConfigMap for Spring Boot application configuration
resource "kubernetes_config_map" "app_config" {
  metadata {
    name      = "${var.app_name}-config"
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
  }

  data = merge(
    {
      SPRING_PROFILES_ACTIVE = var.spring_profiles_active
      SERVER_PORT            = tostring(var.app_port)
    },
    var.spring_config_additional
  )

  depends_on = [kubernetes_namespace.app_namespace]
}

# Deployment for Spring Boot application
resource "kubernetes_deployment" "app" {
  metadata {
    name      = var.app_name
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
    labels = {
      app         = var.app_name
      environment = var.environment
    }
  }

  spec {
    replicas = var.app_replicas[var.environment]

    selector {
      match_labels = {
        app = var.app_name
      }
    }

    template {
      metadata {
        labels = {
          app         = var.app_name
          environment = var.environment
        }
      }

      spec {
        container {
          name  = var.app_name
          image = "${var.app_image}:${var.app_image_tag}"

          port {
            container_port = var.app_port
            name           = "http"
          }

          resources {
            requests = {
              cpu    = var.app_cpu_request
              memory = var.app_memory_request
            }
            limits = {
              cpu    = var.app_cpu_limit
              memory = var.app_memory_limit
            }
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.app_config.metadata[0].name
            }
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 60
            period_seconds        = 30
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = var.app_port
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }
        }
      }
    }
  }

  depends_on = [kubernetes_config_map.app_config]
}

# Service for Spring Boot application
resource "kubernetes_service" "app" {
  metadata {
    name      = var.app_name
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
    labels = {
      app         = var.app_name
      environment = var.environment
    }
  }

  spec {
    selector = {
      app = var.app_name
    }

    port {
      port        = 80
      target_port = var.app_port
      protocol    = "TCP"
      name        = "http"
    }

    type = "ClusterIP"
  }

  depends_on = [kubernetes_deployment.app]
}

# Ingress for Spring Boot application
resource "kubernetes_ingress_v1" "app" {
  count = var.ingress_enabled ? 1 : 0

  metadata {
    name      = "${var.app_name}-ingress"
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
    annotations = merge(
      {
        "kubernetes.io/ingress.class" = var.ingress_class
      },
      var.ingress_tls_enabled && var.ingress_tls_secret_name != "" ? {
        "cert-manager.io/cluster-issuer" = "letsencrypt-prod"
      } : {}
    )
  }

  spec {
    ingress_class_name = var.ingress_class

    dynamic "tls" {
      for_each = var.ingress_tls_enabled ? [1] : []
      content {
        hosts       = [var.ingress_host]
        secret_name = var.ingress_tls_secret_name != "" ? var.ingress_tls_secret_name : "${var.app_name}-tls"
      }
    }

    rule {
      host = var.ingress_host
      http {
        path {
          path      = "/"
          path_type = "Prefix"
          backend {
            service {
              name = kubernetes_service.app.metadata[0].name
              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_service.app,
    helm_release.ingress_nginx
  ]
}
