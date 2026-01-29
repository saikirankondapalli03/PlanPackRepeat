# EKS IAM Roles - Quick Reference Cheat Sheet

## 🎯 The Two Main Roles

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLUSTER SERVICE ROLE                                     │
│    Assumed by: AWS EKS Service (eks.amazonaws.com)         │
│    Purpose: Allow EKS to manage AWS resources              │
│    Created: Automatically by Terraform module              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ 2. NODE GROUP IAM ROLE                                      │
│    Attached to: EC2 Instances (via Instance Profile)       │
│    Purpose: Allow nodes to interact with AWS services      │
│    Created: Automatically by Terraform module              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 What Each Role Needs

### Cluster Service Role

**Trust Policy**:
```json
{
  "Service": "eks.amazonaws.com"
}
```

**Required Policies**:
- ✅ `AmazonEKSClusterPolicy`

**What It Does**:
- Creates ENIs for control plane
- Creates CloudWatch log groups
- Manages load balancers
- Manages cluster resources

---

### Node Group IAM Role

**Trust Policy**:
```json
{
  "Service": "ec2.amazonaws.com"
}
```

**Required Policies**:
- ✅ `AmazonEKSWorkerNodePolicy`
- ✅ `AmazonEKS_CNI_Policy`
- ✅ `AmazonEC2ContainerRegistryReadOnly`

**What It Does**:
- Pulls container images from ECR
- Writes logs to CloudWatch
- Attaches/detaches EBS volumes
- Communicates with EKS API

---

## 🔧 Terraform Configuration

### Minimal Configuration (Module Handles Everything)

```hcl
module "eks" {
  source = "terraform-aws-modules/eks/aws"
  
  name = "my-cluster"
  vpc_id = "vpc-xxx"
  subnet_ids = ["subnet-xxx", "subnet-yyy"]
  
  # IAM roles created automatically ✅
  # No need to specify anything!
}
```

### Custom IAM Role Configuration

```hcl
module "eks" {
  # Cluster role customization
  create_iam_role = true  # Default: true
  iam_role_name   = null  # Auto-generated if null
  
  # Node group role customization
  eks_managed_node_groups = {
    main = {
      create_iam_role = true  # Default: true
      iam_role_name   = null  # Auto-generated if null
      
      # Add custom policies
      iam_role_additional_policies = {
        S3Access = aws_iam_policy.s3_access.arn
      }
    }
  }
}
```

---

## 🔍 Inspection Commands

### Check Cluster Role

```bash
# Get role ARN
terraform output cluster_iam_role_arn

# View role details
aws iam get-role --role-name <cluster-name>-cluster-role

# Check policies
aws iam list-attached-role-policies \
  --role-name <cluster-name>-cluster-role
```

### Check Node Group Role

```bash
# Get role ARN
terraform output node_iam_role_arn

# View role details
aws iam get-role --role-name <cluster-name>-node-group-role

# Check instance profile
aws iam get-instance-profile \
  --instance-profile-name <cluster-name>-node-group-profile

# Verify on EC2 instances
aws ec2 describe-instances \
  --filters "Name=tag:eks:nodegroup-name,Values=main" \
  --query 'Reservations[*].Instances[*].IamInstanceProfile.Arn'
```

---

## ⚠️ Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Cluster creation fails | Wrong trust policy | Module handles automatically ✅ |
| Nodes can't join | Missing node role | Module handles automatically ✅ |
| ImagePullBackOff | No ECR permissions | Module attaches `AmazonEC2ContainerRegistryReadOnly` ✅ |
| Can't access S3 | No S3 permissions | Add custom policy via `iam_role_additional_policies` |
| Can't access Secrets | No Secrets Manager permissions | Add custom policy via `iam_role_additional_policies` |

---

## 🎨 Custom Policy Examples

### S3 Read-Only Access

```hcl
resource "aws_iam_policy" "s3_readonly" {
  name = "${var.cluster_name}-s3-readonly"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = ["s3:GetObject", "s3:ListBucket"]
      Resource = [
        "arn:aws:s3:::my-bucket/*",
        "arn:aws:s3:::my-bucket"
      ]
    }]
  })
}

module "eks" {
  eks_managed_node_groups = {
    main = {
      iam_role_additional_policies = {
        S3ReadOnly = aws_iam_policy.s3_readonly.arn
      }
    }
  }
}
```

### Secrets Manager Access

```hcl
resource "aws_iam_policy" "secrets_access" {
  name = "${var.cluster_name}-secrets-access"
  
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ]
      Resource = "arn:aws:secretsmanager:*:*:secret:my-app/*"
    }]
  })
}
```

---

## 📊 Role Hierarchy

```
EKS Cluster
│
├── Cluster Service Role (assumed by eks.amazonaws.com)
│   └── AmazonEKSClusterPolicy
│
└── Node Groups
    └── Node Group IAM Role (attached to EC2 via Instance Profile)
        ├── AmazonEKSWorkerNodePolicy
        ├── AmazonEKS_CNI_Policy
        ├── AmazonEC2ContainerRegistryReadOnly
        └── [Custom Policies]
```

---

## 🚀 Quick Start Checklist

- [ ] Run `terraform init`
- [ ] Update `environments/*.tfvars` with VPC/subnet IDs
- [ ] Run `terraform plan -var-file=environments/dev.tfvars`
- [ ] Review IAM roles that will be created
- [ ] Run `terraform apply -var-file=environments/dev.tfvars`
- [ ] Verify roles in AWS Console
- [ ] Check nodes can join cluster
- [ ] Test pod deployment

---

## 💡 Pro Tips

1. **Let the module handle it**: Don't create roles manually unless you have specific requirements
2. **Use IRSA for pods**: Better than node-level permissions for application needs
3. **Least privilege**: Only add permissions you actually need
4. **Tag everything**: Helps with cost tracking and organization
5. **Separate roles per environment**: Better security isolation
6. **Monitor CloudTrail**: Track role usage and changes

---

## 📚 Key Concepts Summary

| Concept | Description |
|---------|-------------|
| **Service Role** | Role assumed by AWS service (not users) |
| **Trust Policy** | Defines who can assume the role |
| **Instance Profile** | Container for IAM role attached to EC2 |
| **Managed Policies** | AWS-provided policies (e.g., `AmazonEKSWorkerNodePolicy`) |
| **Custom Policies** | Your own policies for specific needs |
| **IRSA** | IAM Roles for Service Accounts (pod-level permissions) |

---

## 🔗 Related Resources

- [EKS IAM Concepts Guide](./EKS_IAM_CONCEPTS.md) - Detailed explanation
- [EKS Hands-On Guide](./EKS_HANDS_ON_GUIDE.md) - Practical examples
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Terraform EKS Module](https://github.com/terraform-aws-modules/terraform-aws-eks)

---

## 🎓 Remember

1. **Cluster Role** = EKS service uses it to manage cluster
2. **Node Role** = EC2 instances use it to interact with AWS
3. **Module handles both** = You don't need to create them manually
4. **Customize when needed** = Add policies via `iam_role_additional_policies`
5. **Use IRSA for pods** = Better security than node-level permissions

---

## 🎤 2-Minute Interview Story

Use this as a spoken narrative when asked about EKS and IAM. Aim for under 2 minutes (~250 words).

**"When we run Kubernetes on EKS, we have to answer one question: who is allowed to talk to AWS on behalf of the cluster?"**

That's where EKS IAM comes in. There are **two main roles** I keep in mind.

**First, the Cluster Service Role.**  
The EKS *control plane* is an AWS service. It needs permission to create ENIs, CloudWatch log groups, load balancers, and other cluster resources. So we give it a role that only the EKS service can assume — trust policy says `eks.amazonaws.com`. We attach `AmazonEKSClusterPolicy`. That's the "cluster role": everything the control plane needs to run the cluster.

**Second, the Node Group IAM Role.**  
The *workers* are EC2 instances. They pull images from ECR, attach EBS volumes, talk to the EKS API, and send logs to CloudWatch. So we give the *nodes* a role, attached via an instance profile, trusted by `ec2.amazonaws.com`. They get the usual trio: WorkerNodePolicy, CNI policy, and ECR read-only. That's the "node role": everything a node needs to join the cluster and run pods.

**In practice**, we use the Terraform EKS module. It creates both roles and attaches the right policies by default — we don't usually hand-create them. When we need extra permissions — for example, pods reading from S3 or Secrets Manager — we add them through `iam_role_additional_policies` on the node group, or better still we use **IRSA** (IAM Roles for Service Accounts) so only specific pods get those permissions.

**If something goes wrong**, we think in terms of "who needs to do what." ImagePullBackOff → node needs ECR; nodes not joining → node role or trust; cluster failing → cluster role or trust. The module avoids most of that, but knowing these two roles helps us debug and extend the setup safely.

**One-liner to close:**  
*"EKS IAM is mainly two roles: one for the control plane, one for the nodes. The Terraform module creates both; we customize only when we need things like S3 or Secrets Manager, and we prefer IRSA for app-level permissions."*

### Story cheat sheet

| Beat | What to say |
|------|-------------|
| **Hook** | "Who can talk to AWS on behalf of the cluster?" |
| **Role 1** | Cluster role → `eks.amazonaws.com` → EKS manages cluster (ENIs, LB, logs) |
| **Role 2** | Node role → instance profile → `ec2.amazonaws.com` → ECR, EBS, API, CloudWatch |
| **Practice** | Terraform module creates both; we use `iam_role_additional_policies` or IRSA when we need more |
| **Troubleshoot** | Map symptom to role: ImagePullBackOff = node/ECR; nodes not joining = node role; cluster fail = cluster role |
| **Close** | "Two roles: control plane and nodes; module handles them; we add policies or IRSA when we need more." |

### If they ask a follow-up

- **"What's IRSA?"** — IAM Roles for Service Accounts: a role is bound to a Kubernetes ServiceAccount so only pods using that SA get those AWS permissions, instead of every pod on the node.
- **"How do you add S3 access?"** — Define an IAM policy for `s3:GetObject` / `s3:ListBucket`, then reference it in `iam_role_additional_policies` for the node group, or use IRSA for a single app.
- **"How do you verify the roles?"** — `terraform output cluster_iam_role_arn` and `node_iam_role_arn`; then `aws iam list-attached-role-policies` and `aws iam get-instance-profile` for the node role.
