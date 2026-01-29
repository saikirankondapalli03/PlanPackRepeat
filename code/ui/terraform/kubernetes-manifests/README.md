# Kubernetes Manifests

This directory contains Kubernetes manifest templates for manual deployment if needed.

**Note**: The Terraform configuration in the parent directory automatically creates these resources. These templates are provided for reference or manual deployments.

## Files

- `deployment.yaml.template`: Template for Deployment, Service, and Ingress

## Usage

If you want to deploy manually (without Terraform), replace the variables in the template:

```bash
# Set environment variables
export ENVIRONMENT=dev
export REPLICAS=1
export IMAGE=your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app
export IMAGE_TAG=latest
export SPRING_PROFILE=dev
export INGRESS_HOST=app-dev.example.com

# Replace variables and apply
envsubst < deployment.yaml.template | kubectl apply -f -
```

Or manually edit the template and apply:

```bash
kubectl apply -f deployment.yaml
```
