# EKS IAM Roles - Interview Narrative Guide

This document helps you narrate your EKS IAM knowledge in an interview setting, demonstrating hands-on experience and deep understanding.

---

## Opening Statement (30 seconds)

**"I've worked extensively with EKS IAM roles, understanding both the architecture and practical implementation. The key is recognizing that EKS requires two distinct IAM roles: one for the cluster itself and one for the worker nodes, each serving different purposes and following different trust relationships."**

---

## Core Concept Explanation (2-3 minutes)

### The Two-Role Architecture

**"When setting up an EKS cluster, you need to understand two critical IAM roles:**

**1. Cluster Service Role:**
- This role is assumed by the EKS service itself (`eks.amazonaws.com`)
- It allows EKS to create and manage AWS resources on your behalf
- Required policy: `AmazonEKSClusterPolicy`
- Used during cluster creation and management operations
- The trust policy must allow `eks.amazonaws.com` service to assume it

**2. Node Group Role:**
- This role is assumed by EC2 instances that run your Kubernetes worker nodes
- Trust relationship with `ec2.amazonaws.com` service
- Requires three AWS managed policies:
  - `AmazonEKSWorkerNodePolicy` - allows nodes to register with cluster
  - `AmazonEKS_CNI_Policy` - for VPC CNI plugin (networking)
  - `AmazonEC2ContainerRegistryReadOnly` - to pull container images from ECR
- Must be attached via an Instance Profile to EC2 instances

**The critical difference: Cluster role is for the control plane, node role is for the data plane."**

---

## Implementation Flow (2-3 minutes)

### Step-by-Step Process

**"When I implement this with Terraform, here's my approach:**

**Phase 1: Cluster Creation**
1. First, I create the cluster service role with the correct trust policy allowing `eks.amazonaws.com` to assume it
2. Attach the `AmazonEKSClusterPolicy` managed policy
3. Use this role ARN when creating the EKS cluster via `aws eks create-cluster`

**Phase 2: Node Group Setup**
1. Create the node group IAM role with trust policy for `ec2.amazonaws.com`
2. Create an Instance Profile and attach the role to it (this is crucial - EC2 instances need instance profiles, not direct role attachments)
3. Attach the three required managed policies
4. When creating the node group, reference this role ARN

**Phase 3: Verification**
- I verify roles using `aws iam get-role` and check trust policies
- Confirm instance profiles are correctly attached using `aws iam get-instance-profile`
- Test that nodes can pull images from ECR by checking node group role permissions"

---

## Advanced Scenarios (2-3 minutes)

### Custom Permissions

**"In real-world scenarios, nodes often need additional permissions. For example:**

**Scenario 1: S3 Access**
- I create a custom IAM policy allowing S3 read access
- Attach it to the node group role using `iam_role_additional_policies`
- This allows all pods on those nodes to access S3 (though IRSA is better for pod-specific access)

**Scenario 2: Multi-Environment Setup**
- Each environment (dev/staging/prod) gets separate roles
- Naming convention: `{cluster-name}-cluster-role` and `{cluster-name}-node-group-role`
- This provides isolation and allows different permission levels per environment

**Scenario 3: Different Node Groups, Different Permissions**
- General-purpose nodes might have S3 read-only access
- GPU nodes for ML workloads might need full S3 access and SageMaker permissions
- Each node group can have its own IAM role with specific policies"

---

## IRSA - The Modern Approach (2 minutes)

### Pod-Level Permissions

**"For better security, I implement IRSA (IAM Roles for Service Accounts) instead of giving broad permissions to node roles:**

**Why IRSA?**
- Node roles give permissions to ALL pods on that node
- IRSA provides pod-specific permissions via service accounts
- Follows least privilege principle

**How it works:**
1. Enable OIDC provider on the EKS cluster
2. Create an IAM role with trust policy allowing the OIDC provider to assume it
3. The trust policy includes a condition matching the Kubernetes service account
4. Annotate the Kubernetes service account with the IAM role ARN
5. Pods using that service account automatically get temporary credentials

**Example use case:** An application pod needs S3 access, but other pods on the same node don't. With IRSA, only that specific pod gets S3 permissions, not the entire node."

---

## Troubleshooting Experience (1-2 minutes)

### Common Issues and Solutions

**"When troubleshooting IAM issues in EKS, I follow this approach:**

**Issue 1: Nodes can't register with cluster**
- Check node group role has `AmazonEKSWorkerNodePolicy`
- Verify instance profile is attached to EC2 instances
- Check trust policy allows `ec2.amazonaws.com`

**Issue 2: Image pull failures**
- Verify `AmazonEC2ContainerRegistryReadOnly` policy is attached
- Check ECR repository permissions
- Test ECR authentication from node: `aws ecr get-login-password`

**Issue 3: Pods can't access AWS services**
- If using node role: Check node group role permissions
- If using IRSA: Verify OIDC provider is configured, service account annotation is correct, and trust policy condition matches

**Debugging commands I use:**
- `aws iam list-attached-role-policies` - Check what policies are attached
- `aws iam get-role` - View trust policy
- `kubectl describe pod` - Check service account and annotations
- Instance metadata endpoint: `curl http://169.254.169.254/latest/meta-data/iam/security-credentials/`"

---

## Best Practices (1 minute)

**"Key principles I follow:**

1. **Least Privilege:** Use IRSA for pod-specific permissions, not node roles
2. **Separation of Concerns:** Cluster role for control plane, node role for data plane
3. **Environment Isolation:** Separate roles per environment
4. **Naming Conventions:** Consistent naming helps with management and troubleshooting
5. **Monitoring:** Use CloudTrail to audit role assumptions and permissions usage
6. **Documentation:** Document custom policies and their purposes"

---

## Closing Statement (30 seconds)

**"My experience spans from basic cluster setup to advanced scenarios like IRSA implementation, multi-environment deployments, and troubleshooting complex permission issues. I understand not just how to configure these roles, but why each component exists and how they interact with the broader AWS and Kubernetes ecosystem."**

---

## Quick Reference: Key Points to Mention

### Must Mention:
- ✅ Two distinct roles: Cluster role vs Node group role
- ✅ Trust relationships: `eks.amazonaws.com` vs `ec2.amazonaws.com`
- ✅ Instance profiles for EC2 nodes
- ✅ Three required managed policies for nodes
- ✅ IRSA for pod-level permissions
- ✅ Troubleshooting approach

### Advanced Points (if asked):
- ✅ OIDC provider setup for IRSA
- ✅ Multi-environment role management
- ✅ Custom policy creation and attachment
- ✅ CloudTrail for auditing
- ✅ Difference between node role and pod role permissions

### Red Flags to Avoid:
- ❌ Don't say "one role for everything"
- ❌ Don't confuse cluster role with node role
- ❌ Don't forget to mention instance profiles
- ❌ Don't suggest giving nodes admin access

---

## Sample Interview Questions & Answers

### Q: "Walk me through creating an EKS cluster with proper IAM setup."

**A:** "I'd start by creating the cluster service role with a trust policy allowing `eks.amazonaws.com` to assume it, attach `AmazonEKSClusterPolicy`, then create the cluster using this role. For nodes, I'd create a separate role with `ec2.amazonaws.com` trust policy, create an instance profile, attach the three required policies (WorkerNodePolicy, CNI_Policy, ECRReadOnly), and reference this role when creating the node group."

### Q: "How would you give a specific pod S3 access without giving it to all pods?"

**A:** "I'd use IRSA. First, enable the OIDC provider on the cluster. Create an IAM role with a trust policy that allows the OIDC provider to assume it, with a condition matching the specific Kubernetes service account. Attach S3 permissions to this role. Then annotate the service account with the role ARN. Pods using that service account will automatically receive temporary credentials with S3 access, while other pods won't."

### Q: "A pod can't pull images from ECR. How do you troubleshoot?"

**A:** "I'd first check if the node group role has `AmazonEC2ContainerRegistryReadOnly` policy attached. Then verify the instance profile is correctly attached to the EC2 instances. I'd test ECR authentication from the node using `aws ecr get-login-password`. If using IRSA, I'd check the service account annotation and OIDC provider configuration. I'd also check pod events with `kubectl describe pod` to see the exact error message."

---

## Practice Tips

1. **Practice the flow:** Narrate the two-role creation process without looking at notes
2. **Use examples:** Reference specific scenarios (S3 access, multi-environment, etc.)
3. **Show depth:** Mention IRSA even if not asked - shows advanced knowledge
4. **Be specific:** Use actual policy names and service names (`eks.amazonaws.com`, not just "AWS")
5. **Connect concepts:** Link IAM roles to Kubernetes concepts (service accounts, pods, nodes)
