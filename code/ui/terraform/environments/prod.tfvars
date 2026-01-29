# Production Environment Configuration

environment = "prod"

cluster_name = "spring-boot-eks-prod"

# VPC Configuration - Update with your VPC ID and subnet IDs
vpc_id = "vpc-xxxxxxxxxxxxxxxxx"
subnet_ids = [
  "subnet-xxxxxxxxxxxxxxxxx",
  "subnet-yyyyyyyyyyyyyyyyy",
  "subnet-zzzzzzzzzzzzzzzzz"
]

# Application Configuration
app_image = "your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app"
app_image_tag = "prod-latest"

# Spring Boot Configuration
spring_profiles_active = "prod"

# Additional Spring Boot configuration for production
spring_config_additional = {
  # "SPRING_DATASOURCE_URL" = "jdbc:postgresql://prod-db.example.com:5432/mydb"
  # "SPRING_DATASOURCE_USERNAME" = "produser"
  # "LOG_LEVEL" = "WARN"
}

# Ingress Configuration
ingress_host = "app.example.com"
ingress_tls_enabled = true
ingress_tls_secret_name = "app-prod-tls"

# Node Group Configuration
node_group_instance_types = ["t3.large"]  # Larger instances for prod

# Tags
tags = {
  Project     = "SpringBootApp"
  Environment = "prod"
  ManagedBy   = "Terraform"
  CostCenter  = "Production"
}
