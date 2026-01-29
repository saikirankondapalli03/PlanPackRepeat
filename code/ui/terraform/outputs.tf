output "cluster_id" {
  description = "EKS cluster ID"
  value       = module.eks.cluster_id
}

output "cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.eks.cluster_arn
}

output "cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "Security group ID attached to the EKS cluster"
  value       = module.eks.cluster_security_group_id
}

output "cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data required to communicate with the cluster"
  value       = module.eks.cluster_certificate_authority_data
  sensitive   = true
}

output "cluster_oidc_issuer_url" {
  description = "The URL on the EKS cluster for the OpenID Connect identity provider"
  value       = module.eks.cluster_oidc_issuer_url
}

output "node_security_group_id" {
  description = "Security group ID attached to the EKS nodes"
  value       = module.eks.node_security_group_id
}

output "node_groups" {
  description = "EKS node groups"
  value       = module.eks.eks_managed_node_groups
}

output "kubectl_config_command" {
  description = "Command to update kubeconfig"
  value       = "aws eks update-kubeconfig --name ${module.eks.cluster_name} --region ${var.aws_region}"
}

output "ingress_load_balancer_hostname" {
  description = "Ingress LoadBalancer hostname"
  value       = var.ingress_enabled ? try(helm_release.ingress_nginx[0].values, "") : ""
}

output "app_service_name" {
  description = "Kubernetes service name for the application"
  value       = kubernetes_service.app.metadata[0].name
}

output "app_namespace" {
  description = "Kubernetes namespace for the application"
  value       = kubernetes_namespace.app_namespace.metadata[0].name
}

output "app_ingress_host" {
  description = "Ingress hostname for the application"
  value       = var.ingress_enabled ? var.ingress_host : ""
}
