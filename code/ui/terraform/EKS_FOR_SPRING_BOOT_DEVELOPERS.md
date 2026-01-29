# EKS for Spring Boot Developers - Configuration Guide

**Purpose:** This guide helps Spring Boot developers understand EKS concepts and how to configure the existing Terraform module for deploying applications.

---

## 🎯 What You Need to Know (Simplified)

### EKS in Simple Terms

**EKS (Elastic Kubernetes Service)** = AWS-managed Kubernetes cluster where your Spring Boot app runs.

Think of it like this:
- **EKS Cluster** = The control center (manages everything)
- **Node Group** = The worker machines (where your app actually runs)
- **Pods** = Your Spring Boot application instances
- **Service** = Internal load balancer for pods
- **Ingress** = External access point (like a reverse proxy)

---

## 📋 Key Concepts You'll Configure

### 1. **Cluster Configuration** (Already Set Up)

The cluster is the foundation. Your team has already configured:
- **Cluster Name**: Identifies your cluster (`spring-boot-eks-dev`)
- **Kubernetes Version**: Version of K8s running (e.g., `1.33`)
- **VPC & Subnets**: Network where cluster lives

**What you might edit:**
```hcl
# In your .tfvars file
cluster_name = "spring-boot-eks-dev"  # Change if needed
kubernetes_version = "1.33"            # Usually leave as-is
```

---

### 2. **Node Group Configuration** (Where Your App Runs)

**Node Group** = Group of EC2 instances that run your pods.

**Key settings you'll configure:**

```hcl
# In dev.tfvars
node_group_instance_types = ["t3.small"]  # Instance size
node_group_min_size = { dev = 1 }         # Minimum nodes
node_group_max_size = { dev = 3 }         # Maximum nodes  
node_group_desired_size = { dev = 1 }     # Desired nodes
```

**What this means:**
- **Instance Types**: Size of machines (`t3.small` = small, `t3.medium` = medium, `t3.large` = large)
- **Min/Max/Desired**: Auto-scaling limits
  - If traffic increases → more nodes added (up to max)
  - If traffic decreases → nodes removed (down to min)
  - Desired = starting point

**When to change:**
- **App needs more resources?** → Increase instance type (`t3.medium` → `t3.large`)
- **High traffic expected?** → Increase `max_size`
- **Cost optimization?** → Decrease `min_size` and use smaller instances

---

### 3. **Application Configuration** (Your Spring Boot App)

```hcl
# In dev.tfvars
app_name = "spring-boot-app"
app_image = "your-account.dkr.ecr.us-east-1.amazonaws.com/spring-boot-app"
app_image_tag = "dev-latest"
app_port = 8080
```

**What you'll edit:**
- **app_image**: Your Docker image location in ECR
- **app_image_tag**: Which version to deploy (`dev-latest`, `v1.2.3`, etc.)
- **app_port**: Port your Spring Boot app listens on (usually 8080)

---

### 4. **Replicas** (How Many App Instances)

```hcl
app_replicas = {
  dev  = 1   # 1 instance in dev
  uat  = 2   # 2 instances in UAT
  prod = 3   # 3 instances in prod
}
```

**What this means:**
- Number of pod instances running your app
- More replicas = better availability, can handle more traffic
- Kubernetes distributes pods across nodes

**When to change:**
- **Need high availability?** → Increase replicas (min 2-3)
- **Low traffic?** → Can reduce to 1 in dev

---

### 5. **Resource Limits** (CPU & Memory)

```hcl
app_cpu_request = "500m"      # Minimum CPU guaranteed
app_memory_request = "512Mi"  # Minimum memory guaranteed
app_cpu_limit = "1000m"       # Maximum CPU allowed
app_memory_limit = "1Gi"      # Maximum memory allowed
```

**What this means:**
- **Requests**: Guaranteed resources (Kubernetes reserves this)
- **Limits**: Maximum resources (app can't exceed this)
- **Units**: 
  - CPU: `500m` = 0.5 CPU cores, `1000m` = 1 CPU core
  - Memory: `512Mi` = 512 MB, `1Gi` = 1 GB

**When to change:**
- **App uses more memory?** → Increase `memory_limit`
- **App is slow?** → Increase `cpu_limit`
- **Cost optimization?** → Reduce requests/limits (but watch for OOM kills)

**💡 Tip:** Check your app's actual usage with:
```bash
kubectl top pods -n spring-boot-app
```

---

### 6. **Spring Boot Configuration**

```hcl
spring_profiles_active = "dev"

spring_config_additional = {
  "SPRING_DATASOURCE_URL" = "jdbc:postgresql://dev-db.example.com:5432/mydb"
  "SPRING_DATASOURCE_USERNAME" = "devuser"
  "LOG_LEVEL" = "DEBUG"
}
```

**What this means:**
- **spring_profiles_active**: Which Spring profile to use (`dev`, `uat`, `prod`)
- **spring_config_additional**: Environment variables passed to your app
- These become environment variables in your pods

**What you'll edit:**
- Add database URLs, API keys, feature flags, etc.
- Format: `"KEY" = "value"` (both quoted)

**Example:**
```hcl
spring_config_additional = {
  "SPRING_DATASOURCE_URL" = "jdbc:postgresql://db:5432/mydb"
  "AWS_REGION" = "us-east-1"
  "FEATURE_FLAG_X" = "true"
}
```

---

### 7. **Ingress Configuration** (External Access)

```hcl
ingress_enabled = true
ingress_host = "app-dev.example.com"
ingress_tls_enabled = false  # SSL/TLS
```

**What this means:**
- **Ingress**: External URL to access your app
- **TLS**: HTTPS encryption (usually `false` in dev, `true` in prod)

**What you'll edit:**
- **ingress_host**: Your app's domain name
- **ingress_tls_enabled**: Enable HTTPS (requires certificate setup)

---

## 🔧 How to Use the Terraform Module

### Step 1: Edit Environment Configuration

Edit the `.tfvars` file for your environment:

```bash
# For dev environment
terraform/environments/dev.tfvars

# For UAT
terraform/environments/uat.tfvars

# For prod
terraform/environments/prod.tfvars
```

### Step 2: Common Changes You'll Make

#### **Deploy New Version:**
```hcl
# In dev.tfvars
app_image_tag = "v1.2.3"  # Change from "dev-latest"
```

#### **Add Environment Variable:**
```hcl
# In dev.tfvars
spring_config_additional = {
  "NEW_CONFIG_KEY" = "new-value"
  "EXISTING_KEY" = "updated-value"
}
```

#### **Scale Up/Down:**
```hcl
# In dev.tfvars
app_replicas = {
  dev = 2  # Changed from 1
}
```

#### **Change Resource Limits:**
```hcl
# In dev.tfvars
app_memory_limit = "2Gi"  # Increased from 1Gi
app_cpu_limit = "2000m"   # Increased from 1000m
```

### Step 3: Apply Changes

```bash
# Navigate to terraform directory
cd terraform

# Initialize (first time only)
terraform init

# Plan (see what will change)
terraform plan -var-file=environments/dev.tfvars

# Apply (make changes)
terraform apply -var-file=environments/dev.tfvars
```

---

## 🎓 Understanding the Architecture

### What Happens When You Deploy:

1. **Terraform creates/updates:**
   - EKS cluster (if not exists)
   - Node group (EC2 instances)
   - Kubernetes namespace
   - ConfigMap (your Spring Boot config)
   - Deployment (your app pods)
   - Service (internal load balancer)
   - Ingress (external access)

2. **Your Spring Boot app runs as:**
   - **Pods** on the node group instances
   - Multiple pods if `replicas > 1`
   - Each pod = one Spring Boot JVM instance

3. **Traffic flow:**
   ```
   Internet → Ingress → Service → Pods (your Spring Boot app)
   ```

---

## 🐛 Common Scenarios & Solutions

### Scenario 1: "I need to deploy a new version"

**Solution:**
```hcl
# Update in .tfvars
app_image_tag = "new-version-tag"

# Apply
terraform apply -var-file=environments/dev.tfvars
```

**What happens:** Kubernetes will:
1. Create new pods with new image
2. Wait for new pods to be ready (readiness probe)
3. Route traffic to new pods
4. Terminate old pods (rolling update)

---

### Scenario 2: "My app is running out of memory"

**Solution:**
```hcl
# Increase memory limit
app_memory_limit = "2Gi"  # Was 1Gi

# Apply
terraform apply -var-file=environments/dev.tfvars
```

**Check current usage:**
```bash
kubectl top pods -n spring-boot-app
```

---

### Scenario 3: "I need to add a database connection"

**Solution:**
```hcl
# Add to spring_config_additional
spring_config_additional = {
  "SPRING_DATASOURCE_URL" = "jdbc:postgresql://db-host:5432/mydb"
  "SPRING_DATASOURCE_USERNAME" = "user"
  "SPRING_DATASOURCE_PASSWORD" = "password"
}

# Apply
terraform apply -var-file=environments/dev.tfvars
```

**Note:** For passwords/secrets, consider using Kubernetes Secrets (ask your DevOps team).

---

### Scenario 4: "App is slow, need more CPU"

**Solution:**
```hcl
# Increase CPU limit
app_cpu_limit = "2000m"  # Was 1000m (1 core → 2 cores)

# Apply
terraform apply -var-file=environments/dev.tfvars
```

---

### Scenario 5: "Need to handle more traffic"

**Solution:**
```hcl
# Option 1: More replicas (horizontal scaling)
app_replicas = {
  dev = 3  # Was 1
}

# Option 2: Larger instances (vertical scaling)
node_group_instance_types = ["t3.medium"]  # Was t3.small

# Option 3: Both
app_replicas = { dev = 2 }
node_group_instance_types = ["t3.medium"]
```

---

## 📊 Monitoring Your App

### Check Pod Status:
```bash
kubectl get pods -n spring-boot-app
```

### View Logs:
```bash
# All pods
kubectl logs -n spring-boot-app -l app=spring-boot-app

# Specific pod
kubectl logs -n spring-boot-app <pod-name>
```

### Check Resource Usage:
```bash
kubectl top pods -n spring-boot-app
kubectl top nodes
```

### Describe Pod (for debugging):
```bash
kubectl describe pod <pod-name> -n spring-boot-app
```

### Check Ingress:
```bash
kubectl get ingress -n spring-boot-app
```

---

## 🔐 Important Concepts (Simplified)

### 1. **Namespaces**
- Like folders in Kubernetes
- Your app runs in its own namespace (`spring-boot-app`)
- Isolates resources

### 2. **ConfigMaps**
- Stores configuration (environment variables)
- Created from `spring_config_additional`
- Mounted into pods

### 3. **Deployments**
- Manages your app pods
- Handles rolling updates
- Ensures desired number of replicas

### 4. **Services**
- Internal load balancer
- Routes traffic to pods
- Type `ClusterIP` = internal only

### 5. **Ingress**
- External access point
- Routes external traffic to service
- Handles SSL/TLS

### 6. **Health Checks**
- **Liveness Probe**: Is app running? (`/actuator/health`)
- **Readiness Probe**: Is app ready for traffic?
- Kubernetes uses these to manage pods

---

## ⚠️ Things to Remember

1. **Always run `terraform plan` first** - See what will change
2. **Don't edit `main.tf`** - That's the module, edit `.tfvars` files
3. **Environment variables** - Use `spring_config_additional` map
4. **Image tags** - Update `app_image_tag` to deploy new versions
5. **Replicas** - More = better availability, but more cost
6. **Resource limits** - Too low = app crashes, too high = wasted money
7. **Check logs** - Use `kubectl logs` when debugging

---

## 🚀 Quick Reference

### Most Common Changes:

| What to Change | Where | Example |
|---------------|-------|---------|
| Deploy new version | `app_image_tag` | `"v1.2.3"` |
| Add env variable | `spring_config_additional` | `{"KEY" = "value"}` |
| Scale replicas | `app_replicas` | `{dev = 2}` |
| Change memory | `app_memory_limit` | `"2Gi"` |
| Change CPU | `app_cpu_limit` | `"2000m"` |
| Change domain | `ingress_host` | `"app.example.com"` |

### Commands:

```bash
# Plan changes
terraform plan -var-file=environments/dev.tfvars

# Apply changes
terraform apply -var-file=environments/dev.tfvars

# Check pods
kubectl get pods -n spring-boot-app

# View logs
kubectl logs -n spring-boot-app -l app=spring-boot-app

# Check resources
kubectl top pods -n spring-boot-app
```

---

## 📚 Next Steps

1. **Practice:** Make small changes in dev environment
2. **Monitor:** Check logs and resource usage after changes
3. **Learn:** Understand Kubernetes basics (pods, services, deployments)
4. **Ask:** Consult DevOps team for advanced scenarios

---

## 🆘 When to Ask DevOps Team

- Need to change cluster-level settings
- Need to add IAM roles/permissions
- Need to configure secrets management
- Need to change VPC/subnet configuration
- Need to troubleshoot cluster/node issues
- Need to set up monitoring/alerting

---

**Remember:** You're configuring your application deployment, not managing the infrastructure. The Terraform module handles the complex parts - you just need to know what values to set! 🎯
