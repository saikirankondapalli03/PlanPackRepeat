# UAT Environment Configuration

environment = "uat"

cluster_name = "spring-boot-eks-uat"

# VPC Configuration - Update with your VPC ID and subnet IDs
vpc_id = "vpc-xxxxxxxxxxxxxxxxx"
subnet_ids = [
  "subnet-xxxxxxxxxxxxxxxxx",
  "subnet-yyyyyyyyyyyyyyyyy",
  "subnet-zzzzzzzzzzzzzzzzz"
]

# Application Configuration
app_image = "your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app"
app_image_tag = "uat-latest"

# Spring Boot Configuration
spring_profiles_active = "uat"

# Additional Spring Boot configuration for UAT
spring_config_additional = {
  # "SPRING_DATASOURCE_URL" = "jdbc:postgresql://uat-db.example.com:5432/mydb"
  # "SPRING_DATASOURCE_USERNAME" = "uatuser"
  # "LOG_LEVEL" = "INFO"
}

# Ingress Configuration
ingress_host = "app-uat.example.com"
ingress_tls_enabled = true
ingress_tls_secret_name = "app-uat-tls"

# Node Group Configuration
node_group_instance_types = ["t3.medium"]

# Tags
tags = {
  Project     = "SpringBootApp"
  Environment = "uat"
  ManagedBy   = "Terraform"
}
