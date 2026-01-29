# EKS Interview Guide - For Spring Boot Developers

**Your Role:** Backend Spring Boot Developer who configures and deploys applications to EKS using existing Terraform modules.

---

## 🎯 Opening Statement (30 seconds)

**"I'm a Spring Boot developer who works with EKS for deploying containerized applications. While our DevOps team manages the Terraform infrastructure modules, I'm responsible for configuring application deployments, managing environment variables, scaling, and ensuring our Spring Boot apps run efficiently on Kubernetes. I understand the key EKS concepts needed to work with our infrastructure-as-code setup."**

---

## 📋 Core Concepts You Understand (2-3 minutes)

### EKS Architecture Basics

**"From a developer perspective, I understand EKS as:**

**1. EKS Cluster:**
- AWS-managed Kubernetes control plane
- Handles orchestration, scheduling, and management
- We configure cluster name, version, and network settings

**2. Node Groups:**
- EC2 instances that run our application pods
- Configured with instance types (t3.small, t3.medium, etc.)
- Auto-scaling based on min/max/desired size
- I understand when to adjust these based on application needs

**3. Pods:**
- Our Spring Boot application instances run as pods
- Each pod = one JVM instance of our application
- Configured with replicas for high availability

**4. Services & Ingress:**
- Services provide internal load balancing between pods
- Ingress provides external access with domain names
- Configured with hostnames and TLS settings"

---

## 🔧 What You Configure (2-3 minutes)

### Application Configuration

**"In our setup, I work with Terraform `.tfvars` files to configure:**

**1. Application Deployment:**
- Docker image location and tags (`app_image`, `app_image_tag`)
- Number of replicas per environment (`app_replicas`)
- Container port configuration (`app_port`)

**2. Resource Management:**
- CPU and memory requests/limits (`app_cpu_request`, `app_memory_limit`)
- I monitor actual usage and adjust based on application needs
- Understand the difference between requests (guaranteed) and limits (maximum)

**3. Spring Boot Configuration:**
- Spring profiles (`spring_profiles_active`)
- Environment variables via `spring_config_additional` map
- Database connections, API keys, feature flags

**4. Scaling & Performance:**
- Node group instance types (when to use t3.small vs t3.medium)
- Replica counts for different environments
- Resource limits based on application behavior

**5. External Access:**
- NGINX Ingress Controller (deployed via Helm, using AWS NLB)
- Ingress hostnames (`ingress_host`) - environment-specific endpoints
- TLS/SSL configuration (`ingress_tls_enabled`) - enabled for UAT/prod, disabled for dev
- Domain routing for different environments (dev/uat/prod)"

---

## 💡 Practical Experience (2-3 minutes)

### Real-World Scenarios

**"I've handled several scenarios:**

**Scenario 1: Deploying New Versions**
- Update `app_image_tag` in `.tfvars` file
- Run `terraform plan` to review changes
- Apply with `terraform apply`
- Understand rolling updates - Kubernetes creates new pods, waits for readiness, then terminates old ones
- Monitor with `kubectl get pods` and `kubectl logs`

**Scenario 2: Performance Tuning**
- Identified memory issues through `kubectl top pods`
- Adjusted `app_memory_limit` based on actual usage
- Increased CPU limits when application was CPU-bound
- Balanced resource allocation with cost considerations

**Scenario 3: Scaling for Traffic**
- Increased replicas for high-traffic periods
- Adjusted node group instance types when needed
- Understand horizontal scaling (more pods) vs vertical scaling (larger instances)

**Scenario 4: Environment Configuration**
- Configure different settings per environment (dev/uat/prod)
- Manage Spring profiles and environment variables
- Handle database connections, external API endpoints
- Configure ingress for different domains per environment

**Scenario 5: Ingress Configuration (Fidelity Project Experience)**
- Worked with **NGINX Ingress Controller** deployed via Helm chart
- Configured environment-specific app endpoints:
  - **Dev**: `app-dev.example.com` (TLS disabled for easier testing)
  - **UAT**: `app-uat.example.com` (TLS enabled with cert-manager)
  - **Prod**: `app.example.com` (TLS enabled with production certificates)
- Used AWS Network Load Balancer (NLB) as the underlying load balancer for ingress
- Configured ingress class as "nginx" in Terraform variables
- Managed TLS certificates per environment using Kubernetes secrets
- Understood how ingress routes external traffic to internal ClusterIP services"

---

## 🐛 Troubleshooting Experience (1-2 minutes)

### Common Issues & Solutions

**"When troubleshooting application issues:**

**Issue 1: Pods Not Starting**
- Check pod status: `kubectl get pods -n <namespace>`
- View pod events: `kubectl describe pod <pod-name>`
- Check logs: `kubectl logs <pod-name>`
- Common causes: Image pull failures, resource limits too low, configuration errors

**Issue 2: Application Crashes (OOM)**
- Check resource usage: `kubectl top pods`
- Review memory limits vs actual usage
- Increase `app_memory_limit` if needed
- Check application logs for memory-related errors

**Issue 3: Slow Performance**
- Monitor CPU usage with `kubectl top pods`
- Check if CPU limits are too restrictive
- Consider increasing replicas for better distribution
- Review application logs for bottlenecks

**Issue 4: Configuration Issues**
- Verify environment variables are set correctly
- Check ConfigMap: `kubectl get configmap -n <namespace>`
- Ensure Spring profiles match environment
- Validate database connections and external service URLs

**Issue 5: Ingress Not Routing Traffic (Request Flow Troubleshooting)**
- **Step 1 - Check NLB:** `kubectl get svc -n ingress-nginx ingress-nginx-controller` - Verify LoadBalancer has external IP/hostname
- **Step 2 - Check Ingress Controller:** `kubectl get pods -n ingress-nginx` - Ensure ingress controller pods are running
- **Step 3 - Check Ingress Resource:** `kubectl get ingress -n <app-namespace>` - Verify ingress exists and has correct hostname
- **Step 4 - Check Service:** `kubectl get svc -n <app-namespace>` - Verify ClusterIP service exists and has endpoints
- **Step 5 - Check Pods:** `kubectl get pods -n <app-namespace>` - Ensure application pods are running and ready
- **Step 6 - Verify DNS:** Ensure DNS points to NLB hostname/IP
- **Debug ingress:** `kubectl describe ingress <ingress-name> -n <app-namespace>` - Check for routing errors
- **Check ingress logs:** `kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller` - View ingress controller logs"

---

## 🎓 Understanding Kubernetes Concepts (1-2 minutes)

### Key Concepts You Work With

**"I understand these Kubernetes concepts:**

**1. Namespaces:**
- Logical separation of resources
- Our apps run in dedicated namespaces
- Helps organize and isolate applications

**2. Deployments:**
- Manages pod lifecycle
- Handles rolling updates and rollbacks
- Ensures desired replica count

**3. Services:**
- Internal load balancer
- Routes traffic to healthy pods
- Type ClusterIP for internal access
- **Required even with Ingress** - Ingress routes to Services, not directly to pods

**Why ClusterIP Service is Needed with Ingress:**

**Key Point:** Ingress does NOT route directly to pods. Ingress routes to Services, and Services route to pods.

**Why We Need Both:**
1. **Ingress Role**: Routes based on Host header and path (e.g., `app-dev.example.com` → Service)
2. **Service Role**: Provides stable endpoint, load balancing across pods, and service discovery
3. **Separation of Concerns**:
   - **Ingress**: External routing (which domain/path → which service)
   - **Service**: Internal load balancing (which pod gets the request)

**In Our Setup:**
- Ingress resource references the Service: `backend.service.name = "spring-boot-app"`
- Ingress cannot reference pods directly - it MUST reference a Service
- Service maintains Endpoints object with all healthy pod IPs
- Service (via kube-proxy) handles round-robin load balancing to pods

**Without ClusterIP Service:**
- Ingress would have no backend to route to
- No way to load balance across multiple pods
- No stable endpoint (pods can be recreated with new IPs)
- No service discovery mechanism

**4. Ingress:**
- **NGINX Ingress Controller acts as a reverse proxy layer**
- Routes are configured in **Kubernetes Ingress resources** (not NGINX config files)
- Ingress resource defines:
  - Hostname (e.g., `app-dev.example.com`)
  - Path rules (e.g., `/` with Prefix type)
  - Backend service to route to
- NGINX reads Ingress resources and configures itself dynamically
- Routes external traffic to internal ClusterIP services
- Handles SSL/TLS termination
- Configured with AWS Network Load Balancer (NLB) as underlying load balancer
- Environment-specific hostnames (dev/uat/prod) configured via Terraform variables

**Request Flow (How Traffic Reaches Your App):**
Understanding the complete request flow from endpoint to application:

**Step 1: DNS Resolution**
- User types `app-dev.example.com` in browser
- DNS resolves to AWS Network Load Balancer (NLB) IP address
- NLB was created automatically when NGINX Ingress Controller was deployed

**Step 2: AWS Network Load Balancer (NLB)**
- NLB receives the HTTP/HTTPS request
- NLB is configured as `LoadBalancer` type service for ingress-nginx
- NLB forwards traffic to NGINX Ingress Controller pods running in `ingress-nginx` namespace
- NLB distributes traffic across multiple ingress controller pods for high availability

**Step 3: NGINX Ingress Controller (Reverse Proxy Layer)**
- NGINX Ingress Controller acts as a **reverse proxy** - it receives requests and forwards them to backend services
- Ingress Controller pods receive the request from NLB
- **Routes are configured in Kubernetes Ingress resources** (not directly in NGINX config files)
- NGINX reads Kubernetes Ingress resources to determine routing rules:
  - Checks the `Host` header (`app-dev.example.com`) against ingress rules
  - Matches path patterns (e.g., `/` with `Prefix` type)
  - Routes to the backend service specified in the ingress rule
- The Ingress resource defines:
  - **Host**: `app-dev.example.com` (from `ingress_host` variable)
  - **Path**: `/` (all paths under root)
  - **Backend Service**: `spring-boot-app` service on port 80
- NGINX dynamically configures itself based on these Ingress resources

**Step 4: Kubernetes Service (ClusterIP) - Load Balancing Layer**
- **NGINX does NOT directly route to pods** - it routes to the Kubernetes Service
- Ingress forwards request to the ClusterIP service (e.g., `spring-boot-app` service)
- **The Kubernetes Service handles load balancing to pods** (not NGINX, not EKS cluster)
- Service uses label selectors (`app = spring-boot-app`) to find matching pods
- Service maintains an **Endpoints** object that tracks all healthy pods
- **kube-proxy** (running on each EKS node) implements the load balancing algorithm
- Default algorithm: **Round-robin** - distributes requests evenly across all healthy pods
- Only routes to pods that pass readiness probes (healthy pods)

**Step 5: Application Pods**
- Request reaches one of your Spring Boot application pods (selected by Service load balancing)
- Pod processes the request and generates response
- Response flows back: Pod → Service → Ingress Controller → NLB → User

**Load Balancing Details:**
- **NGINX routes to Service**: NGINX Ingress Controller forwards to the Service IP/name
- **Service routes to Pods**: Kubernetes Service (via kube-proxy) distributes across pods
- **With 10 pods**: Service uses round-robin to distribute requests evenly (1→2→3→...→10→1...)
- **EKS Cluster**: Provides the infrastructure (nodes, networking), but doesn't decide pod selection
- **kube-proxy**: Runs on each node, implements iptables/IPVS rules for load balancing
- **Session Affinity**: Can be configured if needed (sticky sessions)

**Key Points:**
- EKS cluster endpoint is only for `kubectl` API access, not application traffic
- Application traffic flows: Internet → NLB → Ingress Controller → Service → Pods
- Each component handles different concerns: NLB (external access), Ingress (routing), Service (load balancing), Pods (application logic)

**Where Routes Are Configured:**

Routes are configured in the **Kubernetes Ingress resource** (not in NGINX config files). In our Terraform setup:

```hcl
resource "kubernetes_ingress_v1" "app" {
  spec {
    rule {
      host = var.ingress_host  # e.g., "app-dev.example.com"
      http {
        path {
          path      = "/"
          path_type = "Prefix"
          backend {
            service {
              name = kubernetes_service.app.metadata[0].name  # "spring-boot-app"
              port { number = 80 }
            }
          }
        }
      }
    }
  }
}
```

**How It Works:**
- **NGINX Ingress Controller** watches for Ingress resources via Kubernetes API
- When it sees an Ingress resource, it reads the routing rules
- NGINX configures itself as a reverse proxy based on these rules
- Routes are defined declaratively in Kubernetes (Infrastructure as Code)
- Multiple Ingress resources can exist, NGINX handles routing based on Host header and path

**Example Route Configuration:**
- **Host**: `app-dev.example.com` (from `dev.tfvars`)
- **Path**: `/` (all paths)
- **Backend**: `spring-boot-app` service → forwards to pods
- **TLS**: Disabled in dev, enabled in UAT/prod

**How Load Balancing Works with Multiple Pods:**

**Important:** NGINX Ingress Controller does NOT directly route to pods. Load balancing happens at the Kubernetes Service level.

**Example Scenario: 10 Pods Running**

1. **NGINX Routes to Service:**
   - NGINX receives request for `app-dev.example.com`
   - Routes to `spring-boot-app` Service (ClusterIP)
   - NGINX doesn't know about individual pods

2. **Service Load Balancing (kube-proxy):**
   - Service maintains an **Endpoints** object listing all healthy pods
   - **kube-proxy** (running on each EKS node) implements load balancing
   - Default algorithm: **Round-robin**
   - Request 1 → Pod 1, Request 2 → Pod 2, ..., Request 10 → Pod 10, Request 11 → Pod 1 (cycle repeats)

3. **How kube-proxy Works:**
   - Uses **iptables** rules (or IPVS in some setups) on each node
   - Creates rules that distribute traffic across pod IPs
   - Only includes pods that pass readiness probes
   - If a pod fails readiness probe, it's removed from Endpoints automatically

4. **EKS Cluster Role:**
   - EKS provides the infrastructure (nodes, networking, kube-proxy)
   - EKS doesn't decide which pod gets which request
   - Load balancing decision is made by kube-proxy based on Service configuration

**Load Balancing Flow:**
```
NGINX Ingress Controller
    ↓ (Routes to Service IP/name)
Kubernetes Service (spring-boot-app)
    ↓ (kube-proxy uses round-robin)
    ├─→ Pod 1 (10.0.1.5)
    ├─→ Pod 2 (10.0.1.6)
    ├─→ Pod 3 (10.0.1.7)
    ├─→ ...
    └─→ Pod 10 (10.0.1.14)
```

**Key Points:**
- **NGINX**: Routes to Service, doesn't know about individual pods
- **Service**: Acts as load balancer, maintains list of healthy pods
- **kube-proxy**: Implements round-robin algorithm on each node
- **EKS Cluster**: Provides infrastructure, doesn't make routing decisions
- **Readiness Probes**: Only healthy pods receive traffic

**5. ConfigMaps:**
- Stores configuration data
- Mounted as environment variables in pods
- We configure via `spring_config_additional`

**6. Health Checks:**
- Liveness probes: Is app running?
- Readiness probes: Is app ready for traffic?
- Our Spring Boot apps use `/actuator/health` endpoint"

---

## 🔄 Deployment Workflow (1 minute)

### Your Process

**"My typical deployment workflow:**

1. **Update Configuration:**
   - Edit `.tfvars` file for target environment
   - Update image tags, environment variables, or resource limits

2. **Review Changes:**
   - Run `terraform plan` to see what will change
   - Verify changes are correct before applying

3. **Deploy:**
   - Run `terraform apply` with appropriate `.tfvars` file
   - Monitor deployment progress

4. **Verify:**
   - Check pod status: `kubectl get pods`
   - View logs: `kubectl logs`
   - Test application endpoints
   - Monitor resource usage

5. **Monitor:**
   - Watch for errors in logs
   - Check resource utilization
   - Verify application functionality"

---

## 🎯 Best Practices You Follow (1 minute)

**"Key practices I follow:**

1. **Always Plan First:** Run `terraform plan` before applying changes
2. **Environment-Specific Configs:** Use separate `.tfvars` files per environment
3. **Resource Monitoring:** Regularly check actual usage vs limits
4. **Gradual Changes:** Make incremental changes and monitor impact
5. **Documentation:** Document environment variables and their purposes
6. **Health Checks:** Ensure Spring Boot actuator endpoints are configured
7. **Logging:** Use structured logging for easier troubleshooting"

---

## 🚀 Advanced Understanding (if asked)

### When to Escalate

**"I understand when to involve the DevOps team:**

- Cluster-level configuration changes
- IAM roles and permissions
- VPC and networking changes
- Node group infrastructure changes
- Security and secrets management
- Advanced monitoring and alerting setup

**I focus on application-level configuration while relying on the team for infrastructure-level concerns."**

---

## 📊 Monitoring & Observability (1 minute)

### How You Monitor

**"I use these tools and commands:**

**Kubernetes Commands:**
- `kubectl get pods` - Check pod status
- `kubectl logs` - View application logs
- `kubectl top pods` - Monitor resource usage
- `kubectl describe pod` - Debug pod issues

**Application Monitoring:**
- Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/metrics`)
- Application logs for errors and performance
- Resource usage trends over time

**I understand the importance of monitoring for:**
- Performance optimization
- Troubleshooting issues
- Capacity planning
- Cost optimization"

---

## 💼 Closing Statement (30 seconds)

**"As a Spring Boot developer working with EKS, I have hands-on experience configuring application deployments, managing resources, troubleshooting issues, and optimizing performance. I understand the balance between application needs and infrastructure constraints, and I work effectively with our DevOps team to ensure smooth deployments. My focus is on making our Spring Boot applications run efficiently and reliably on Kubernetes."**

---

## 🎯 Quick Reference: Key Points to Mention

### Must Mention:
- ✅ You configure application deployments via Terraform `.tfvars` files
- ✅ You understand pods, services, ingress, and deployments
- ✅ You manage resource limits, replicas, and environment variables
- ✅ You troubleshoot using `kubectl` commands
- ✅ You monitor resource usage and application health
- ✅ You understand when to scale horizontally vs vertically

### Practical Examples:
- ✅ Deploying new application versions
- ✅ Adjusting resource limits based on usage
- ✅ Configuring Spring Boot environment variables
- ✅ Scaling applications for traffic
- ✅ Troubleshooting pod and application issues

### What You DON'T Need to Deep Dive:
- ❌ Creating Terraform modules from scratch
- ❌ IAM role creation and management
- ❌ VPC and networking deep dive
- ❌ Cluster creation and management
- ❌ Advanced Kubernetes internals

---

## 📝 Sample Interview Questions & Answers

### Q: "How do you deploy a new version of your Spring Boot application?"

**A:** "I update the `app_image_tag` in the environment-specific `.tfvars` file (e.g., `dev.tfvars`), then run `terraform plan` to review the changes. After verifying, I apply with `terraform apply -var-file=environments/dev.tfvars`. Kubernetes performs a rolling update - it creates new pods with the new image, waits for them to pass readiness probes, routes traffic to the new pods, then terminates the old ones. I monitor the process with `kubectl get pods` and check logs to ensure the new version is working correctly."

### Q: "How do you handle application configuration across different environments?"

**A:** "We use separate `.tfvars` files for each environment (dev, uat, prod). Each file contains environment-specific values like `spring_profiles_active`, `ingress_host`, and `spring_config_additional` map with environment variables. For example, dev might point to a dev database, while prod points to the production database. I also configure different replica counts and resource limits per environment based on expected load."

### Q: "Your application is running out of memory. How do you troubleshoot and fix it?"

**A:** "First, I'd check current memory usage with `kubectl top pods` to see actual consumption. Then I'd review the pod logs with `kubectl logs` to identify any memory-related errors. If the app is hitting the memory limit, I'd increase `app_memory_limit` in the `.tfvars` file. I'd also check if there are memory leaks by monitoring usage over time. After updating, I'd apply the changes and monitor to ensure the issue is resolved without over-provisioning resources unnecessarily."

### Q: "How do you scale your application to handle increased traffic?"

**A:** "I have two main approaches: horizontal scaling by increasing `app_replicas` to run more pod instances, or vertical scaling by increasing node group instance types or application resource limits. For sudden traffic spikes, horizontal scaling is usually better as it distributes load across multiple pods. I'd update the replica count in the `.tfvars` file and apply. Kubernetes will create additional pods and distribute them across available nodes. I monitor the deployment and resource usage to ensure we're handling the load effectively."

### Q: "How do you monitor your Spring Boot application running on EKS?"

**A:** "I use several approaches: `kubectl get pods` to check pod status and health, `kubectl logs` to view application logs, and `kubectl top pods` to monitor CPU and memory usage. Our Spring Boot apps expose actuator endpoints like `/actuator/health` for health checks and `/actuator/metrics` for application metrics. I also check ingress status with `kubectl get ingress` to ensure external access is working. For deeper issues, I use `kubectl describe pod` to see events and configuration details."

### Q: "How did you configure ingress for external access to your applications?"

**A:** "In my Fidelity project, we used **NGINX Ingress Controller** installed via Helm chart. The ingress controller was configured to use AWS Network Load Balancer (NLB) as the underlying load balancer. For each environment, I configured different ingress hostnames in the `.tfvars` files: `app-dev.example.com` for dev (with TLS disabled for easier testing), `app-uat.example.com` for UAT, and `app.example.com` for production (both with TLS enabled). The ingress routes external traffic to our internal ClusterIP services, which then distribute traffic to the application pods. I configured the ingress class as 'nginx' and managed TLS certificates per environment using Kubernetes secrets. This setup allowed us to have environment-specific endpoints while maintaining proper security with TLS in non-dev environments."

### Q: "How does NGINX Ingress Controller work as a proxy layer? Where are the routes configured?"

**A:** "NGINX Ingress Controller acts as a **reverse proxy layer** in our EKS setup. Here's how it works:

**NGINX as Reverse Proxy:**
- NGINX receives requests from the AWS NLB
- It acts as a reverse proxy, forwarding requests to backend services
- It handles SSL/TLS termination, load balancing, and routing

**Where Routes Are Configured:**
Routes are **not configured in NGINX config files**. Instead, they're configured declaratively in **Kubernetes Ingress resources**. In our Terraform setup, we define an Ingress resource that specifies:
- **Host**: `app-dev.example.com` (from `ingress_host` variable)
- **Path**: `/` with `Prefix` type (matches all paths)
- **Backend Service**: Points to our `spring-boot-app` ClusterIP service on port 80

**How NGINX Reads Routes:**
- NGINX Ingress Controller watches Kubernetes API for Ingress resources
- When it detects an Ingress resource, it reads the routing rules
- NGINX dynamically configures itself based on these Ingress resources
- Multiple Ingress resources can exist - NGINX routes based on Host header and path matching

**Benefits of This Approach:**
- Routes are defined as Infrastructure as Code (Terraform)
- No need to manually edit NGINX config files
- Routes can be version-controlled and managed per environment
- NGINX automatically updates when Ingress resources change

This declarative approach means we configure routes in Kubernetes resources, and NGINX acts as the proxy layer that enforces those routing rules."

### Q: "When using an ingress provider like NGINX, why do you still need a ClusterIP service? Can't Ingress route directly to pods?"

**A:** "Great question! This is a fundamental Kubernetes concept. **Ingress cannot route directly to pods** - it must route to a Service. Here's why we need both:

**Why ClusterIP Service is Required:**

**1. Ingress Design Limitation:**
- Ingress resources can only reference Services in their backend configuration
- In our Terraform, the Ingress resource has: `backend.service.name = "spring-boot-app"`
- Ingress cannot specify pod IPs or pod selectors directly

**2. Service Provides Essential Functions:**
- **Stable Endpoint**: Service provides a stable IP/name that doesn't change when pods are recreated
- **Load Balancing**: Service (via kube-proxy) handles round-robin distribution across pods
- **Service Discovery**: Other services can reference the Service by name
- **Endpoints Management**: Service maintains an Endpoints object with all healthy pod IPs

**3. Separation of Concerns:**
- **Ingress**: Handles external routing (which domain/path → which service)
- **Service**: Handles internal load balancing (which pod gets the request)

**4. Pod Lifecycle:**
- Pods can be recreated with new IPs (e.g., during deployments, scaling, failures)
- Service provides a stable abstraction layer
- Ingress always routes to the Service, which automatically updates its Endpoints

**Without ClusterIP Service:**
- Ingress would have no backend to route to
- No way to load balance across multiple pods
- No stable endpoint (pods change IPs)
- No automatic health checking and endpoint updates

**In Summary:** Ingress handles 'where' (external routing), Service handles 'which pod' (internal load balancing). They work together - Ingress routes to Service, Service routes to pods."

### Q: "How does load balancing work when you have multiple pods? If you have 10 pods, how does NGINX decide which one to invoke? Or is it decided by the EKS cluster?"

**A:** "Great question! This is a common point of confusion. **NGINX does NOT directly route to pods**, and **EKS cluster doesn't decide which pod gets the request**. Load balancing happens at the **Kubernetes Service level**.

**Here's how it works with 10 pods:**

**1. NGINX Routes to Service:**
- NGINX Ingress Controller receives the request
- It routes to the Kubernetes Service (e.g., `spring-boot-app` Service)
- NGINX doesn't know about individual pods - it only knows about the Service

**2. Service Load Balancing (kube-proxy):**
- The Kubernetes Service maintains an **Endpoints** object that lists all healthy pods
- **kube-proxy** (running on each EKS node) implements the load balancing algorithm
- Default algorithm is **round-robin**:
  - Request 1 → Pod 1
  - Request 2 → Pod 2
  - Request 3 → Pod 3
  - ...
  - Request 10 → Pod 10
  - Request 11 → Pod 1 (cycle repeats)

**3. How kube-proxy Works:**
- kube-proxy uses **iptables** rules (or IPVS) on each node
- It creates iptables rules that distribute traffic across pod IPs
- Only pods that pass readiness probes are included in the Endpoints
- If a pod fails, it's automatically removed from Endpoints

**4. EKS Cluster Role:**
- EKS provides the infrastructure (nodes, networking, kube-proxy component)
- EKS doesn't make routing decisions
- The load balancing decision is made by kube-proxy based on Service configuration

**Flow Summary:**
```
NGINX → Service → kube-proxy (round-robin) → Pods
```

**Key Takeaway:** NGINX routes to the Service, and the Service (via kube-proxy) handles load balancing across pods. This is a two-tier approach: NGINX for external routing, Service for internal pod load balancing."

### Q: "Can you walk me through how a request flows from when a user types your app endpoint to when it reaches your Spring Boot application?"

**A:** "Absolutely. Here's the complete request flow:

**1. DNS Resolution:** When a user types `app-dev.example.com`, DNS resolves it to the AWS Network Load Balancer (NLB) IP address. The NLB was automatically created when we deployed the NGINX Ingress Controller as a LoadBalancer service type.

**2. AWS Network Load Balancer:** The NLB receives the HTTP/HTTPS request and forwards it to one of the NGINX Ingress Controller pods running in the `ingress-nginx` namespace. The NLB distributes traffic across multiple ingress controller pods for high availability.

**3. NGINX Ingress Controller:** The ingress controller pod receives the request and reads Kubernetes Ingress resources to determine routing rules. It checks the `Host` header (`app-dev.example.com`) against configured ingress rules, matches it to our application's ingress resource, and routes to the backend service specified in the ingress rule.

**4. Kubernetes Service (ClusterIP):** The ingress forwards the request to our ClusterIP service (e.g., `spring-boot-app` service). The service acts as an internal load balancer, uses label selectors to find matching pods, and distributes traffic across healthy pods based on readiness probes.

**5. Application Pod:** The request finally reaches one of our Spring Boot application pods. The pod processes the request, generates a response, and the response flows back through the same path: Pod → Service → Ingress Controller → NLB → User.

**Important note:** The EKS cluster endpoint is only for `kubectl` API access, not for application traffic. Application traffic flows entirely through the NLB → Ingress → Service → Pods path."

---

## 🔄 Request Flow Diagram (Quick Reference)

### Visual Flow:
```
User Browser
    ↓ (DNS: app-dev.example.com → NLB IP)
AWS Network Load Balancer (NLB)
    ↓ (LoadBalancer Service: ingress-nginx-controller)
NGINX Ingress Controller Pods (REVERSE PROXY LAYER)
    ↓ (Reads Kubernetes Ingress Resource for routing rules)
    ↓ (Matches Host: app-dev.example.com, Path: /)
    ↓ (Routes to backend SERVICE, NOT directly to pods)
Kubernetes Ingress Resource (app-namespace)
    ↓ (Defines routes: Host + Path → Backend Service)
    ↓ (Routes configured HERE, not in NGINX config files)
ClusterIP Service (spring-boot-app)
    ↓ (kube-proxy: Round-robin load balancing)
    ├─→ Pod 1 (Request 1, 11, 21...)
    ├─→ Pod 2 (Request 2, 12, 22...)
    ├─→ Pod 3 (Request 3, 13, 23...)
    └─→ ... (distributes evenly across all healthy pods)
Spring Boot Application Pods (app-namespace)
    ↓ (Processes request)
Response flows back: Pod → Service (kube-proxy) → NGINX Proxy → NLB → User
```

### Load Balancing Details:
- **NGINX**: Routes to Service only (doesn't know about individual pods)
- **Service**: Maintains Endpoints object with all healthy pods
- **kube-proxy**: Implements round-robin on each node using iptables/IPVS
- **EKS Cluster**: Provides infrastructure, doesn't make routing decisions

### Key Components:
- **EKS Cluster Endpoint**: Only for `kubectl` API access (NOT for app traffic)
- **NLB**: AWS-managed load balancer, entry point for external traffic
- **NGINX Ingress Controller**: Reverse proxy layer that reads Ingress resources for routing rules
- **Kubernetes Ingress Resource**: Where routes are configured (Host, Path, Backend Service)
- **Service (ClusterIP)**: **Required** - Ingress routes to Service, not pods. Provides stable endpoint, load balancing, and service discovery
- **kube-proxy**: Implements round-robin load balancing across pods (runs on each node)
- **Pods**: Your actual Spring Boot application instances

### Why Service is Needed with Ingress:
- **Ingress limitation**: Ingress can only reference Services, not pods directly
- **Service provides**: Stable endpoint, load balancing, service discovery, Endpoints management
- **Separation**: Ingress = external routing, Service = internal load balancing

### Where Routes Are Configured:
- **Routes are NOT in NGINX config files**
- **Routes ARE in Kubernetes Ingress resources** (defined via Terraform)
- NGINX watches Ingress resources and configures itself dynamically

### Verification Commands:
```bash
# Check NLB
kubectl get svc -n ingress-nginx ingress-nginx-controller

# Check Ingress Controller pods
kubectl get pods -n ingress-nginx

# Check Ingress resource
kubectl get ingress -n <app-namespace>

# Check Service (shows ClusterIP)
kubectl get svc -n <app-namespace>

# Check Endpoints (shows all healthy pods that Service routes to)
kubectl get endpoints -n <app-namespace>
kubectl describe endpoints <service-name> -n <app-namespace>

# Check Application pods (verify all pods are ready)
kubectl get pods -n <app-namespace>

# Verify load balancing - check which pods receive traffic
kubectl get pods -n <app-namespace> -o wide  # Shows pod IPs
kubectl logs <pod-name> -n <app-namespace>   # Check logs to see traffic distribution
```

---

## 🎓 Practice Tips

1. **Know Your Workflow:** Be able to walk through your deployment process step-by-step
2. **Use Real Examples:** Reference actual scenarios you've encountered
3. **Show Understanding:** Explain not just what you do, but why
4. **Be Honest:** It's okay to say you work with existing modules - that's your role
5. **Connect Concepts:** Link Kubernetes concepts to Spring Boot application needs
6. **Demonstrate Troubleshooting:** Show you can debug issues systematically

---

**Remember:** You're demonstrating that you can effectively work with EKS infrastructure, not that you built it from scratch. Focus on your practical experience configuring and deploying applications! 🎯
