variable "environment" {
  description = "Environment name (dev, uat, prod)"
  type        = string
  validation {
    condition     = contains(["dev", "uat", "prod"], var.environment)
    error_message = "Environment must be one of: dev, uat, prod"
  }
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
}

variable "kubernetes_version" {
  description = "Kubernetes version"
  type        = string
  default     = "1.33"
}

variable "vpc_id" {
  description = "VPC ID where the cluster will be deployed"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for the EKS cluster"
  type        = list(string)
}

variable "control_plane_subnet_ids" {
  description = "List of subnet IDs for the EKS control plane (optional)"
  type        = list(string)
  default     = []
}

variable "app_name" {
  description = "Name of the Spring Boot application"
  type        = string
  default     = "spring-boot-app"
}

variable "app_image" {
  description = "Docker image for the Spring Boot application"
  type        = string
}

variable "app_image_tag" {
  description = "Docker image tag"
  type        = string
  default     = "latest"
}

variable "app_replicas" {
  description = "Number of application replicas"
  type        = map(number)
  default = {
    dev  = 1
    uat  = 2
    prod = 3
  }
}

variable "app_cpu_request" {
  description = "CPU request for the application"
  type        = string
  default     = "500m"
}

variable "app_memory_request" {
  description = "Memory request for the application"
  type        = string
  default     = "512Mi"
}

variable "app_cpu_limit" {
  description = "CPU limit for the application"
  type        = string
  default     = "1000m"
}

variable "app_memory_limit" {
  description = "Memory limit for the application"
  type        = string
  default     = "1Gi"
}

variable "app_port" {
  description = "Port on which the application runs"
  type        = number
  default     = 8080
}

variable "ingress_enabled" {
  description = "Enable ingress"
  type        = bool
  default     = true
}

variable "ingress_host" {
  description = "Ingress hostname"
  type        = string
}

variable "ingress_class" {
  description = "Ingress class name"
  type        = string
  default     = "nginx"
}

variable "ingress_tls_enabled" {
  description = "Enable TLS for ingress"
  type        = bool
  default     = true
}

variable "ingress_tls_secret_name" {
  description = "TLS secret name for ingress"
  type        = string
  default     = ""
}

variable "spring_profiles_active" {
  description = "Spring profiles active (dev, uat, prod)"
  type        = string
}

variable "spring_config_additional" {
  description = "Additional Spring Boot configuration as key-value pairs"
  type        = map(string)
  default     = {}
}

variable "node_group_instance_types" {
  description = "Instance types for EKS node groups"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "node_group_min_size" {
  description = "Minimum number of nodes"
  type        = map(number)
  default = {
    dev  = 1
    uat  = 2
    prod = 2
  }
}

variable "node_group_max_size" {
  description = "Maximum number of nodes"
  type        = map(number)
  default = {
    dev  = 3
    uat  = 4
    prod = 5
  }
}

variable "node_group_desired_size" {
  description = "Desired number of nodes"
  type        = map(number)
  default = {
    dev  = 1
    uat  = 2
    prod = 3
  }
}

variable "enable_cluster_creator_admin_permissions" {
  description = "Enable cluster creator admin permissions"
  type        = bool
  default     = true
}

variable "endpoint_public_access" {
  description = "Enable public access to EKS endpoint"
  type        = bool
  default     = true
}

variable "endpoint_private_access" {
  description = "Enable private access to EKS endpoint"
  type        = bool
  default     = true
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {}
}
