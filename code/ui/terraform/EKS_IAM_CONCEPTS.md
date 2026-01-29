# EKS Cluster Setup & IAM Roles - Essential Concepts

This guide explains the key concepts you need to understand when setting up EKS clusters, especially around IAM roles and service roles.

## Table of Contents

1. [EKS Architecture Overview](#eks-architecture-overview)
2. [IAM Roles in EKS](#iam-roles-in-eks)
3. [Service Roles Explained](#service-roles-explained)
4. [Node Group IAM Roles](#node-group-iam-roles)
5. [How Terraform Module Handles This](#how-terraform-module-handles-this)
6. [Common Scenarios & Troubleshooting](#common-scenarios--troubleshooting)
7. [Best Practices](#best-practices)

---

## EKS Architecture Overview

### What is EKS?

**Amazon Elastic Kubernetes Service (EKS)** is a managed Kubernetes service. Key points:

- **Control Plane**: Managed by AWS (you don't manage master nodes)
- **Data Plane**: You manage worker nodes (EC2 instances or Fargate)
- **Networking**: Uses AWS VPC for networking
- **Authentication**: Uses IAM for cluster access, Kubernetes RBAC for in-cluster access

### EKS Components

```
┌─────────────────────────────────────────┐
│         EKS Control Plane               │
│    (Managed by AWS - You don't see)     │
│  - API Server                           │
│  - etcd                                 │
│  - Scheduler                            │
│  - Controller Manager                  │
└─────────────────────────────────────────┘
                    │
                    │ API Calls
                    │
┌─────────────────────────────────────────┐
│         Your Worker Nodes               │
│    (EC2 Instances - You manage)        │
│  - kubelet                              │
│  - kube-proxy                           │
│  - Container Runtime                    │
└─────────────────────────────────────────┘
```

### EKS Components — Interview-Style Summary (Under 1 Min)

Use this as a concise, points-based answer when explaining EKS components in an interview:

1. **Two main parts:** The **control plane** (managed by AWS) and the **data plane** (your worker nodes).

2. **Control plane (AWS-managed):**
   - **API server** — Single entry point for all cluster operations.
   - **etcd** — Stores cluster state.
   - **Scheduler** — Decides which node runs which pod.
   - **Controller manager** — Keeps desired state in sync.

3. **Data plane (you manage — worker nodes, e.g. EC2):**
   - **kubelet** — Talks to the API server, runs pods, keeps the node registered and healthy.
   - **kube-proxy** — Implements Service networking and forwards traffic to the right pods.
   - **Container runtime** (e.g. containerd) — Runs the containers.

4. **Summary:** AWS runs the control plane; you run the nodes that execute workloads.

5. **IAM in brief:**
   - **EKS service role** — Used by the control plane to create/manage AWS resources.
   - **Node IAM role** — Used by nodes to pull images (ECR), write logs (CloudWatch), attach volumes, etc.

---

## IAM Roles in EKS

EKS requires **two main types of IAM roles**:

### 1. Cluster Service Role (EKS Service Role)

**Purpose**: Allows the EKS service to create and manage AWS resources on your behalf.

**What it does**:
- Creates CloudWatch log groups
- Creates ENIs (Elastic Network Interfaces) for the control plane
- Manages load balancers
- Interacts with other AWS services

**Key Permissions**:
- `eks:CreateCluster`
- `eks:DescribeCluster`
- `ec2:CreateNetworkInterface`
- `ec2:DescribeNetworkInterfaces`
- `logs:CreateLogGroup`
- `logs:PutLogEvents`

**Important**: This role is **assumed by the EKS service**, not by your user or Terraform.

### 2. Node Group IAM Role (Worker Node Role)

**Purpose**: Allows worker nodes to interact with AWS services and join the cluster.

**What it does**:
- Allows nodes to pull container images from ECR
- Allows nodes to write logs to CloudWatch
- Allows nodes to attach/detach EBS volumes
- Allows nodes to communicate with the EKS API server

**Key Permissions**:
- `ec2:DescribeInstances`
- `ec2:DescribeRegions`
- `ecr:GetAuthorizationToken`
- `ecr:BatchCheckLayerAvailability`
- `ecr:GetDownloadUrlForLayer`
- `ecr:BatchGetImage`
- `logs:CreateLogGroup`
- `logs:CreateLogStream`
- `logs:PutLogEvents`

**Important**: This role is **attached to EC2 instances** (worker nodes) via instance profile.

---

## Service Roles Explained

### What is a Service Role?

A **service role** is an IAM role that a **service** (not a user) assumes to perform actions on your behalf.

### EKS Service Role vs Node Role

```
┌─────────────────────────────────────────────────┐
│           EKS Service Role                      │
│  (Assumed by: AWS EKS Service)                 │
│                                                 │
│  Purpose: Allow EKS to manage AWS resources   │
│  - Create ENIs                                 │
│  - Create CloudWatch logs                      │
│  - Manage load balancers                       │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│           Node Group IAM Role                   │
│  (Attached to: EC2 Instances)                  │
│                                                 │
│  Purpose: Allow nodes to interact with AWS     │
│  - Pull images from ECR                        │
│  - Write logs to CloudWatch                   │
│  - Attach EBS volumes                          │
└─────────────────────────────────────────────────┘
```

### Trust Relationships

**EKS Service Role Trust Policy**:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "eks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

**Node Group Role Trust Policy**:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

---

## Node Group IAM Roles

### Instance Profile vs IAM Role

When you create a node group, you need to:

1. **Create an IAM Role** with necessary permissions
2. **Create an Instance Profile** (container for the role)
3. **Attach the Instance Profile** to EC2 instances

```
EC2 Instance → Instance Profile → IAM Role → Permissions
```

### Required Policies for Node Groups

The Terraform module automatically attaches these AWS managed policies:

1. **AmazonEKSWorkerNodePolicy**: Basic permissions for nodes
2. **AmazonEKS_CNI_Policy**: For VPC CNI plugin (networking)
3. **AmazonEC2ContainerRegistryReadOnly**: To pull images from ECR
4. **AmazonSSMManagedInstanceCore**: For Systems Manager access (optional)

### Custom Policies

You can add custom policies for:
- Accessing S3 buckets
- Accessing Secrets Manager
- Accessing other AWS services

---

## How Terraform Module Handles This

### What the Module Does Automatically

The `terraform-aws-modules/eks/aws` module handles:

#### 1. Cluster Service Role

```hcl
# In main.tf, the module creates:
resource "aws_iam_role" "cluster" {
  name = "${var.cluster_name}-cluster-role"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "eks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

# Attaches required policies
resource "aws_iam_role_policy_attachment" "cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.cluster.name
}
```

#### 2. Node Group IAM Role

```hcl
# The module creates:
resource "aws_iam_role" "node_group" {
  name = "${var.cluster_name}-node-group-role"
  
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

# Creates instance profile
resource "aws_iam_instance_profile" "node_group" {
  name = "${var.cluster_name}-node-group-profile"
  role = aws_iam_role.node_group.name
}

# Attaches required policies
resource "aws_iam_role_policy_attachment" "node_group_policies" {
  for_each = toset([
    "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy",
    "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy",
    "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  ])
  
  policy_arn = each.value
  role       = aws_iam_role.node_group.name
}
```

### What You Need to Configure

In your `main.tf`, you only need to specify:

```hcl
module "eks" {
  source = "terraform-aws-modules/eks/aws"
  
  # Cluster name
  name = var.cluster_name
  
  # VPC configuration
  vpc_id     = var.vpc_id
  subnet_ids = var.subnet_ids
  
  # The module handles IAM roles automatically
  # But you can override if needed:
  create_iam_role = true  # Default: true
  iam_role_name   = null  # Auto-generated if null
  
  # Node group configuration
  eks_managed_node_groups = {
    main = {
      # IAM role is created automatically
      # Instance profile is created automatically
      # Policies are attached automatically
    }
  }
}
```

---

## Common Scenarios & Troubleshooting

### Scenario 1: Cluster Creation Fails

**Error**: `InvalidParameterException: Role ARN is not valid`

**Cause**: The service role doesn't exist or has wrong trust policy.

**Solution**:
```hcl
# Ensure create_iam_role is true (default)
module "eks" {
  create_iam_role = true
  # ...
}
```

### Scenario 2: Nodes Can't Join Cluster

**Error**: Nodes show `NotReady` status

**Cause**: Node group IAM role missing permissions or wrong trust policy.

**Solution**:
```hcl
# Check that node group has required policies
# The module attaches these automatically, but verify:
eks_managed_node_groups = {
  main = {
    # Ensure IAM role is created
    create_iam_role = true
  }
}
```

### Scenario 3: Nodes Can't Pull Images from ECR

**Error**: `ImagePullBackOff` in pods

**Cause**: Node group role missing ECR permissions.

**Solution**: The module automatically attaches `AmazonEC2ContainerRegistryReadOnly`. If using private ECR, add:

```hcl
eks_managed_node_groups = {
  main = {
    iam_role_additional_policies = {
      ECRFullAccess = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess"
    }
  }
}
```

### Scenario 4: Need Custom Permissions

**Requirement**: Nodes need to access S3 bucket

**Solution**:
```hcl
# Create custom policy
resource "aws_iam_policy" "s3_access" {
  name = "${var.cluster_name}-s3-access"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "s3:GetObject",
        "s3:ListBucket"
      ]
      Resource = [
        "arn:aws:s3:::my-bucket/*",
        "arn:aws:s3:::my-bucket"
      ]
    }]
  })
}

# Attach to node group
module "eks" {
  eks_managed_node_groups = {
    main = {
      iam_role_additional_policies = {
        S3Access = aws_iam_policy.s3_access.arn
      }
    }
  }
}
```

---

## Best Practices

### 1. Use Least Privilege

Don't attach overly broad policies:

```hcl
# ❌ Bad
iam_role_additional_policies = {
  FullAccess = "arn:aws:iam::aws:policy/AdministratorAccess"
}

# ✅ Good
iam_role_additional_policies = {
  S3ReadOnly = aws_iam_policy.s3_readonly.arn
}
```

### 2. Separate Roles Per Environment

```hcl
# Different roles for dev/prod
module "eks_dev" {
  name = "my-cluster-dev"
  # ...
}

module "eks_prod" {
  name = "my-cluster-prod"
  # ...
}
```

### 3. Use IAM Roles for Service Accounts (IRSA)

For pods to access AWS services, use IRSA instead of node role:

```hcl
# Enable IRSA
module "eks" {
  enable_irsa = true
  # ...
}

# Create service account with IAM role
resource "kubernetes_service_account" "app" {
  metadata {
    name      = "app-sa"
    namespace = "default"
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.app_pod.arn
    }
  }
}
```

### 4. Monitor IAM Role Usage

Use CloudTrail to monitor:
- Who assumes roles
- What actions are performed
- When roles are used

### 5. Use Tags for Organization

```hcl
module "eks" {
  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
    Project     = "MyProject"
  }
  
  # Tags propagate to IAM roles
  iam_role_tags = {
    Purpose = "EKS Cluster Service Role"
  }
}
```

---

## Key Takeaways

1. **EKS Service Role**: Assumed by AWS EKS service to manage cluster resources
2. **Node Group Role**: Attached to EC2 instances to allow nodes to interact with AWS
3. **Trust Policies**: Define which service can assume the role
4. **Instance Profile**: Required to attach IAM role to EC2 instances
5. **Module Handles It**: The Terraform module creates and configures roles automatically
6. **Customize When Needed**: Add additional policies for specific requirements

---

## Additional Resources

- [AWS EKS IAM Roles Documentation](https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html)
- [EKS Cluster IAM Role](https://docs.aws.amazon.com/eks/latest/userguide/service_IAM_role.html)
- [Node Group IAM Role](https://docs.aws.amazon.com/eks/latest/userguide/create-node-role.html)
- [Terraform AWS EKS Module](https://github.com/terraform-aws-modules/terraform-aws-eks)

---

## Quick Reference: What Gets Created

```
terraform apply
│
├── IAM Role: eks-cluster-role
│   ├── Trust: eks.amazonaws.com
│   └── Policies: AmazonEKSClusterPolicy
│
├── IAM Role: eks-node-group-role
│   ├── Trust: ec2.amazonaws.com
│   ├── Instance Profile: eks-node-group-profile
│   └── Policies:
│       ├── AmazonEKSWorkerNodePolicy
│       ├── AmazonEKS_CNI_Policy
│       └── AmazonEC2ContainerRegistryReadOnly
│
└── EKS Cluster
    └── Uses: eks-cluster-role
    └── Node Groups
        └── Use: eks-node-group-profile
```
