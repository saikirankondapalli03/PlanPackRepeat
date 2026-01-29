# Development Environment Configuration

environment = "dev"

cluster_name = "spring-boot-eks-dev"

# VPC Configuration - Update with your VPC ID and subnet IDs
vpc_id = "vpc-xxxxxxxxxxxxxxxxx"
subnet_ids = [
  "subnet-xxxxxxxxxxxxxxxxx",
  "subnet-yyyyyyyyyyyyyyyyy",
  "subnet-zzzzzzzzzzzzzzzzz"
]

# Application Configuration
app_image = "your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app"
app_image_tag = "dev-latest"

# Spring Boot Configuration
spring_profiles_active = "dev"

# Additional Spring Boot configuration for dev
spring_config_additional = {
  # "SPRING_DATASOURCE_URL" = "jdbc:postgresql://dev-db.example.com:5432/mydb"
  # "SPRING_DATASOURCE_USERNAME" = "devuser"
  # "LOG_LEVEL" = "DEBUG"
}

# Ingress Configuration
ingress_host = "app-dev.example.com"
ingress_tls_enabled = false  # Usually disabled in dev

# Node Group Configuration
node_group_instance_types = ["t3.small"]  # Smaller instances for dev

# Tags
tags = {
  Project     = "SpringBootApp"
  Environment = "dev"
  ManagedBy   = "Terraform"
}
