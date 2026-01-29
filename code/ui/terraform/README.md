# Terraform EKS Deployment for Spring Boot Application

This Terraform configuration deploys a Spring Boot application to AWS EKS clusters across multiple environments (dev, uat, prod).

## Prerequisites

1. **AWS CLI** installed and configured
2. **Terraform** >= 1.5.7
3. **kubectl** installed
4. **helm** installed (for ingress controller)
5. AWS credentials configured with appropriate permissions
6. Existing VPC and subnets in AWS

## Required AWS Permissions

The AWS credentials used should have permissions to:
- Create and manage EKS clusters
- Create and manage EC2 instances (for node groups)
- Create and manage IAM roles and policies
- Create and manage security groups
- Create and manage load balancers
- Create and manage CloudWatch log groups

## Directory Structure

```
terraform/
├── main.tf                 # Main Terraform configuration
├── variables.tf             # Variable definitions
├── outputs.tf              # Output definitions
├── versions.tf             # Terraform and provider versions
├── providers.tf            # Provider configurations
├── terraform.tfvars.example # Example variables file
├── environments/
│   ├── dev.tfvars          # Development environment variables
│   ├── uat.tfvars          # UAT environment variables
│   └── prod.tfvars         # Production environment variables
└── README.md               # This file
```

## Quick Start

### 1. Configure Variables

Copy the example variables file and update with your values:

```bash
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your VPC ID, subnet IDs, etc.
```

Or use environment-specific files:

```bash
# For development
terraform apply -var-file=environments/dev.tfvars

# For UAT
terraform apply -var-file=environments/uat.tfvars

# For production
terraform apply -var-file=environments/prod.tfvars
```

### 2. Initialize Terraform

```bash
terraform init
```

### 3. Plan Deployment

```bash
# For development
terraform plan -var-file=environments/dev.tfvars

# For UAT
terraform plan -var-file=environments/uat.tfvars

# For production
terraform plan -var-file=environments/prod.tfvars
```

### 4. Apply Configuration

```bash
# For development
terraform apply -var-file=environments/dev.tfvars

# For UAT
terraform apply -var-file=environments/uat.tfvars

# For production
terraform apply -var-file=environments/prod.tfvars
```

### 5. Configure kubectl

After deployment, configure kubectl to connect to your cluster:

```bash
aws eks update-kubeconfig --name <cluster-name> --region <region>
```

Or use the output command:

```bash
terraform output -raw kubectl_config_command | bash
```

## Configuration Details

### EKS Cluster

- Uses the official `terraform-aws-modules/eks/aws` module
- Supports Kubernetes version 1.33
- Includes essential addons: CoreDNS, kube-proxy, VPC-CNI, EKS Pod Identity Agent
- CloudWatch logging enabled for API, audit, authenticator, controller manager, and scheduler

### Node Groups

- Managed node groups using AL2023 AMI
- Configurable instance types and scaling (min/max/desired)
- Default disk size: 50GB
- Rolling update with 50% max unavailable

### Application Deployment

- Spring Boot application deployed as Kubernetes Deployment
- ConfigMap for Spring Boot configuration
- Service for internal cluster communication
- Ingress for external access (NGINX Ingress Controller)
- Health checks using Spring Boot Actuator endpoints

### Ingress Controller

- NGINX Ingress Controller installed via Helm
- AWS Network Load Balancer (NLB) for ingress
- TLS support (can be configured per environment)

## Environment-Specific Configurations

### Development (dev)
- 1 replica
- Smaller instance types (t3.small)
- TLS usually disabled
- Debug logging enabled

### UAT (uat)
- 2 replicas
- Medium instance types (t3.medium)
- TLS enabled
- Info logging

### Production (prod)
- 3 replicas
- Larger instance types (t3.large)
- TLS enabled
- Warn/Error logging

## Variables to Update

Before deploying, update these variables in your `.tfvars` file:

1. **VPC Configuration**
   - `vpc_id`: Your VPC ID
   - `subnet_ids`: List of subnet IDs (at least 2 in different AZs)

2. **Application Configuration**
   - `app_image`: Docker image repository URL
   - `app_image_tag`: Docker image tag
   - `spring_profiles_active`: Spring profile (dev/uat/prod)

3. **Ingress Configuration**
   - `ingress_host`: Domain name for your application
   - `ingress_tls_secret_name`: TLS certificate secret name (if using cert-manager)

4. **Spring Boot Configuration**
   - `spring_config_additional`: Additional environment variables as key-value pairs

## Outputs

After deployment, you can view outputs:

```bash
terraform output
```

Key outputs:
- `cluster_name`: EKS cluster name
- `cluster_endpoint`: Kubernetes API endpoint
- `kubectl_config_command`: Command to configure kubectl
- `app_ingress_host`: Application ingress hostname
- `ingress_load_balancer_hostname`: Ingress LoadBalancer hostname

## Updating the Application

To update the application image:

1. Update `app_image_tag` in your `.tfvars` file
2. Run `terraform apply`

The deployment will perform a rolling update.

## Scaling

To scale the application:

1. Update `app_replicas` in your `.tfvars` file
2. Run `terraform apply`

To scale node groups:

1. Update `node_group_min_size`, `node_group_max_size`, or `node_group_desired_size`
2. Run `terraform apply`

## Troubleshooting

### Check Cluster Status

```bash
aws eks describe-cluster --name <cluster-name> --region <region>
```

### Check Node Status

```bash
kubectl get nodes
```

### Check Pod Status

```bash
kubectl get pods -n <app-name>
```

### View Application Logs

```bash
kubectl logs -n <app-name> -l app=<app-name>
```

### Check Ingress

```bash
kubectl get ingress -n <app-name>
kubectl describe ingress -n <app-name>
```

## Cleanup

To destroy all resources:

```bash
terraform destroy -var-file=environments/<environment>.tfvars
```

**Warning**: This will delete the entire EKS cluster and all resources!

## Additional Notes

- The configuration assumes Spring Boot Actuator is enabled with `/actuator/health` endpoint
- For production, consider:
  - Setting up AWS WAF for ingress
  - Configuring autoscaling policies
  - Setting up monitoring and alerting
  - Configuring backup strategies
  - Using AWS Secrets Manager for sensitive configuration
- For TLS certificates, you may want to install cert-manager:

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

## Support

For issues or questions, please refer to:
- [Terraform AWS EKS Module Documentation](https://github.com/terraform-aws-modules/terraform-aws-eks)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
