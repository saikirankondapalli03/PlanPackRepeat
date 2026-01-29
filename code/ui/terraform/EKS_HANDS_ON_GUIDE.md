# EKS IAM Roles - Hands-On Guide

This guide walks you through understanding and working with EKS IAM roles with practical examples.

## Understanding the Flow

### Step 1: Cluster Creation

When you run `terraform apply`, here's what happens:

```bash
# 1. Terraform creates the Cluster Service Role
aws iam create-role \
  --role-name my-cluster-cluster-role \
  --assume-role-policy '{
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Principal": {"Service": "eks.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }]
  }'

# 2. Attaches required policy
aws iam attach-role-policy \
  --role-name my-cluster-cluster-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonEKSClusterPolicy

# 3. Creates the EKS cluster using this role
aws eks create-cluster \
  --name my-cluster \
  --role-arn arn:aws:iam::123456789012:role/my-cluster-cluster-role \
  --resources-vpc-config subnetIds=subnet-xxx,subnet-yyy
```

### Step 2: Node Group Creation

```bash
# 1. Creates Node Group IAM Role
aws iam create-role \
  --role-name my-cluster-node-group-role \
  --assume-role-policy '{
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Principal": {"Service": "ec2.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }]
  }'

# 2. Creates Instance Profile
aws iam create-instance-profile \
  --instance-profile-name my-cluster-node-group-profile

aws iam add-role-to-instance-profile \
  --instance-profile-name my-cluster-node-group-profile \
  --role-name my-cluster-node-group-role

# 3. Attaches required policies
aws iam attach-role-policy \
  --role-name my-cluster-node-group-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy

aws iam attach-role-policy \
  --role-name my-cluster-node-group-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy

aws iam attach-role-policy \
  --role-name my-cluster-node-group-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly

# 4. Creates Node Group using the instance profile
aws eks create-nodegroup \
  --cluster-name my-cluster \
  --nodegroup-name main \
  --instance-types t3.medium \
  --node-role arn:aws:iam::123456789012:role/my-cluster-node-group-role
```

---

## Inspecting What Was Created

### Check Cluster Service Role

```bash
# Get the role ARN from Terraform output
terraform output cluster_iam_role_arn

# Or query directly
aws iam get-role --role-name <cluster-name>-cluster-role

# Check attached policies
aws iam list-attached-role-policies \
  --role-name <cluster-name>-cluster-role

# View trust policy
aws iam get-role --role-name <cluster-name>-cluster-role \
  --query 'Role.AssumeRolePolicyDocument'
```

### Check Node Group Role

```bash
# Get node group role ARN
terraform output node_iam_role_arn

# Check the role
aws iam get-role --role-name <cluster-name>-node-group-role

# Check attached policies
aws iam list-attached-role-policies \
  --role-name <cluster-name>-node-group-role

# Check instance profile
aws iam get-instance-profile \
  --instance-profile-name <cluster-name>-node-group-profile

# Verify it's attached to EC2 instances
aws ec2 describe-instances \
  --filters "Name=tag:eks:nodegroup-name,Values=main" \
  --query 'Reservations[*].Instances[*].[InstanceId,IamInstanceProfile.Arn]'
```

---

## Common Customizations

### Example 1: Add S3 Access to Nodes

```hcl
# Create custom policy
resource "aws_iam_policy" "node_s3_access" {
  name        = "${var.cluster_name}-node-s3-access"
  description = "Allow nodes to read from S3"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::my-app-bucket/*",
          "arn:aws:s3:::my-app-bucket"
        ]
      }
    ]
  })
}

# Attach to node group
module "eks" {
  eks_managed_node_groups = {
    main = {
      iam_role_additional_policies = {
        S3Access = aws_iam_policy.node_s3_access.arn
      }
    }
  }
}
```

### Example 2: Add Secrets Manager Access

```hcl
resource "aws_iam_policy" "node_secrets_access" {
  name = "${var.cluster_name}-node-secrets-access"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ]
        Resource = "arn:aws:secretsmanager:*:*:secret:my-app/*"
      }
    ]
  })
}

module "eks" {
  eks_managed_node_groups = {
    main = {
      iam_role_additional_policies = {
        SecretsAccess = aws_iam_policy.node_secrets_access.arn
      }
    }
  }
}
```

### Example 3: Use Existing IAM Role

If you have an existing role:

```hcl
module "eks" {
  # Use existing cluster role
  create_iam_role = false
  iam_role_arn    = "arn:aws:iam::123456789012:role/my-existing-cluster-role"

  eks_managed_node_groups = {
    main = {
      # Use existing node group role
      create_iam_role = false
      iam_role_arn    = "arn:aws:iam::123456789012:role/my-existing-node-role"
    }
  }
}
```

---

## Troubleshooting Commands

### Check if Role Can Be Assumed

```bash
# Test cluster service role assumption (as AWS service)
aws sts assume-role \
  --role-arn arn:aws:iam::123456789012:role/my-cluster-cluster-role \
  --role-session-name test-session

# This will fail because only eks.amazonaws.com can assume it
# That's expected!
```

### Check Node Group Permissions

```bash
# SSH into a node (if possible)
ssh ec2-user@<node-ip>

# Check instance metadata for role
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/

# Get temporary credentials
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/<role-name>/

# Test permissions
aws s3 ls  # If S3 access is configured
aws ecr get-login-password --region us-east-1  # ECR access
```

### Verify Node Can Pull Images

```bash
# Check if node can authenticate to ECR
kubectl run test-pod --image=123456789012.dkr.ecr.us-east-1.amazonaws.com/my-app:latest \
  --restart=Never \
  --dry-run=client -o yaml

# If pod fails with ImagePullBackOff, check:
kubectl describe pod <pod-name>
kubectl logs <pod-name>

# Check node group role has ECR permissions
aws iam list-attached-role-policies \
  --role-name <cluster-name>-node-group-role | grep ECR
```

---

## Understanding the Module Outputs

After `terraform apply`, check outputs:

```bash
terraform output
```

Key outputs related to IAM:

```hcl
# Cluster IAM Role
cluster_iam_role_arn = "arn:aws:iam::123456789012:role/my-cluster-cluster-role"
cluster_iam_role_name = "my-cluster-cluster-role"

# Node Group IAM Role
node_iam_role_arn = "arn:aws:iam::123456789012:role/my-cluster-node-group-role"
node_iam_role_name = "my-cluster-node-group-role"
```

---

## Real-World Scenarios

### Scenario 1: Multi-Environment Setup

```hcl
# environments/dev.tfvars
cluster_name = "my-app-dev"
tags = {
  Environment = "dev"
}

# environments/prod.tfvars
cluster_name = "my-app-prod"
tags = {
  Environment = "prod"
}

# Each gets separate IAM roles:
# - my-app-dev-cluster-role
# - my-app-dev-node-group-role
# - my-app-prod-cluster-role
# - my-app-prod-node-group-role
```

### Scenario 2: Different Node Groups with Different Permissions

```hcl
module "eks" {
  eks_managed_node_groups = {
    # General purpose nodes
    general = {
      iam_role_additional_policies = {
        S3ReadOnly = aws_iam_policy.s3_readonly.arn
      }
    }
    
    # GPU nodes for ML workloads
    gpu = {
      instance_types = ["p3.2xlarge"]
      iam_role_additional_policies = {
        S3FullAccess = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
        SageMakerAccess = aws_iam_policy.sagemaker_access.arn
      }
    }
  }
}
```

### Scenario 3: Using IRSA (IAM Roles for Service Accounts) Instead of Node Role

For pod-level permissions (better security):

```hcl
# Enable IRSA
module "eks" {
  enable_irsa = true
  # ...
}

# Create IAM role for pods
resource "aws_iam_role" "app_pod_role" {
  name = "${var.cluster_name}-app-pod-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Federated = module.eks.oidc_provider_arn
      }
      Action = "sts:AssumeRoleWithWebIdentity"
      Condition = {
        StringEquals = {
          "${module.eks.oidc_provider}:sub" = "system:serviceaccount:default:app-sa"
        }
      }
    }]
  })
}

# Attach policy
resource "aws_iam_role_policy_attachment" "app_pod_s3" {
  role       = aws_iam_role.app_pod_role.name
  policy_arn = aws_iam_policy.app_s3_access.arn
}

# Create service account
resource "kubernetes_service_account" "app" {
  metadata {
    name      = "app-sa"
    namespace = "default"
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.app_pod_role.arn
    }
  }
}

# Use in deployment
resource "kubernetes_deployment" "app" {
  spec {
    template {
      spec {
        service_account_name = kubernetes_service_account.app.metadata[0].name
        # Pods now have S3 access via IRSA
      }
    }
  }
}
```

---

## Key Differences: Node Role vs IRSA

**IRSA** = **I**AM **R**oles for **S**ervice **A**ccounts (pod-level IAM via Kubernetes service accounts).

| Aspect | Node Role | IRSA (Pod Role) |
|--------|-----------|-----------------|
| **Scope** | All pods on node | Specific pods |
| **Security** | Less granular | More granular |
| **Use Case** | Node-level needs (ECR, CloudWatch) | Pod-specific needs (S3, DynamoDB) |
| **Best Practice** | For infrastructure needs | For application needs |

---

## Quick Checklist

Before deploying, ensure:

- [ ] Cluster service role has correct trust policy (`eks.amazonaws.com`)
- [ ] Node group role has correct trust policy (`ec2.amazonaws.com`)
- [ ] Instance profile exists and is attached to role
- [ ] Required AWS managed policies are attached
- [ ] Custom policies (if any) are properly scoped
- [ ] Tags are applied for cost tracking
- [ ] Roles follow naming conventions
- [ ] Least privilege principle is followed

---

## Debugging Tips

1. **Check CloudTrail**: See who/what is assuming roles
2. **Check IAM Policy Simulator**: Test permissions before applying
3. **Check Node Logs**: `/var/log/messages` on nodes
4. **Check Pod Logs**: `kubectl logs` for application errors
5. **Use AWS Console**: Visual inspection of roles and policies

---

## Next Steps

1. Review the created roles in AWS Console
2. Test permissions with AWS CLI
3. Monitor CloudTrail for role usage
4. Implement IRSA for pod-level permissions
5. Set up monitoring and alerting for IAM events
