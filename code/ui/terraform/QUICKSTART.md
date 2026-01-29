# Quick Start Guide

## Prerequisites Checklist

- [ ] AWS CLI installed and configured (`aws --version`)
- [ ] Terraform installed (`terraform --version`)
- [ ] kubectl installed (`kubectl version --client`)
- [ ] helm installed (`helm version`)
- [ ] AWS credentials configured (`aws sts get-caller-identity`)
- [ ] VPC and subnets created in AWS
- [ ] Docker image pushed to ECR (or your container registry)

## Step-by-Step Deployment

### 1. Update Environment Variables

Edit the environment-specific `.tfvars` file (e.g., `environments/dev.tfvars`):

```hcl
# Required: Update these values
vpc_id = "vpc-xxxxxxxxxxxxxxxxx"
subnet_ids = ["subnet-xxx", "subnet-yyy", "subnet-zzz"]
app_image = "your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app"
ingress_host = "app-dev.example.com"
```

### 2. Initialize Terraform

```powershell
# PowerShell
terraform init

# Or use the helper script
.\deploy.ps1 -Environment dev -Action init
```

### 3. Review the Plan

```powershell
terraform plan -var-file=environments/dev.tfvars
```

### 4. Deploy

```powershell
# Using Terraform directly
terraform apply -var-file=environments/dev.tfvars

# Or using the helper script
.\deploy.ps1 -Environment dev -Action apply
```

### 5. Configure kubectl

After deployment completes:

```powershell
# Get the command from Terraform output
terraform output -raw kubectl_config_command

# Or run it directly
aws eks update-kubeconfig --name spring-boot-eks-dev --region us-east-1
```

### 6. Verify Deployment

```powershell
# Check cluster status
kubectl cluster-info

# Check nodes
kubectl get nodes

# Check pods
kubectl get pods -n spring-boot-app

# Check services
kubectl get svc -n spring-boot-app

# Check ingress
kubectl get ingress -n spring-boot-app

# Get ingress LoadBalancer address
kubectl get svc -n ingress-nginx ingress-nginx-controller
```

### 7. Access Your Application

```powershell
# Get the ingress LoadBalancer hostname
$ingressHost = (kubectl get svc -n ingress-nginx ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')

# Update your DNS to point to this hostname
# Or access directly via hostname if using the ingress_host domain
```

## Common Commands

### View Application Logs

```powershell
kubectl logs -n spring-boot-app -l app=spring-boot-app --tail=100 -f
```

### Scale Application

```powershell
# Update replicas in tfvars file, then:
terraform apply -var-file=environments/dev.tfvars

# Or scale directly:
kubectl scale deployment spring-boot-app -n spring-boot-app --replicas=3
```

### Update Application Image

```powershell
# Update app_image_tag in tfvars, then:
terraform apply -var-file=environments/dev.tfvars
```

### View Terraform Outputs

```powershell
terraform output -var-file=environments/dev.tfvars
```

### Destroy Resources

```powershell
terraform destroy -var-file=environments/dev.tfvars
```

## Troubleshooting

### Cluster Not Accessible

```powershell
# Verify AWS credentials
aws sts get-caller-identity

# Check cluster status
aws eks describe-cluster --name spring-boot-eks-dev --region us-east-1

# Reconfigure kubectl
aws eks update-kubeconfig --name spring-boot-eks-dev --region us-east-1
```

### Pods Not Starting

```powershell
# Check pod events
kubectl describe pod <pod-name> -n spring-boot-app

# Check pod logs
kubectl logs <pod-name> -n spring-boot-app

# Check node resources
kubectl top nodes
```

### Ingress Not Working

```powershell
# Check ingress controller
kubectl get pods -n ingress-nginx

# Check ingress status
kubectl describe ingress spring-boot-app-ingress -n spring-boot-app

# Check LoadBalancer service
kubectl get svc -n ingress-nginx ingress-nginx-controller
```

## Environment-Specific Notes

### Development
- Smaller instances (t3.small)
- Single replica
- TLS usually disabled
- Debug logging

### UAT
- Medium instances (t3.medium)
- 2 replicas
- TLS enabled
- Info logging

### Production
- Larger instances (t3.large)
- 3+ replicas
- TLS required
- Warn/Error logging
- Consider autoscaling policies

## Next Steps

1. Set up monitoring (CloudWatch, Prometheus)
2. Configure autoscaling (HPA, Cluster Autoscaler)
3. Set up CI/CD pipeline
4. Configure backup strategies
5. Set up alerting
6. Review security best practices
7. Configure AWS Secrets Manager for sensitive data
