# CloudFormation ↔ Terraform Concept Correlation

This document maps AWS CloudFormation concepts to their Terraform equivalents, making it easier to understand both Infrastructure as Code (IaC) tools.

---

## 1. **Core Concept**

- **CloudFormation**: Templates (JSON/YAML) → Stacks → Resources
- **Terraform**: Configuration files (HCL) → Workspaces/States → Resources

---

## 2. **Template Structure**

| CloudFormation | Terraform Equivalent |
|----------------|---------------------|
| `AWSTemplateFormatVersion` | Terraform version block (`terraform { required_version = ">= 1.0" }`) |
| `Description` | Comments (`# Description`) |
| `Parameters` | **Variables** (`variable "instance_type" {}`) |
| `Mappings` | **Locals** (`locals { region_map = {...} }`) |
| `Conditions` | **Conditional expressions** (`count`, `for_each`, `if` expressions) |
| `Transform` | **Modules** (reusable components) |
| `Resources` | **Resources** (`resource "aws_instance" "my_instance" {}`) |
| `Outputs` | **Outputs** (`output "instance_id" {}`) |

---

## 3. **Intrinsic Functions**

| CloudFormation | Terraform Equivalent |
|----------------|---------------------|
| `!Ref Parameter` | `var.parameter_name` |
| `!Ref Resource` | `resource_type.resource_name.id` (or `.attribute`) |
| `!GetAtt Resource.Attribute` | `resource_type.resource_name.attribute` |
| `!Sub '${Var}-text'` | String interpolation: `"${var.env}-text"` |
| `!Join [",", [...]]` | `join(",", [...])` |
| `!Select [0, List]` | `list[0]` or `element(list, 0)` |
| `!FindInMap [Map, Key1, Key2]` | `local.map_name["key1"]["key2"]` |
| `!If [Condition, TrueVal, FalseVal]` | `condition ? true_val : false_val` |
| `!Equals [A, B]` | `A == B` |
| `!And`, `!Or`, `!Not` | `&&`, `\|\|`, `!` |

---

## 4. **Pseudo Parameters**

| CloudFormation | Terraform Equivalent |
|----------------|---------------------|
| `AWS::AccountId` | `data.aws_caller_identity.current.account_id` |
| `AWS::Region` | `data.aws_region.current.name` or `var.aws_region` |
| `AWS::StackName` | `terraform.workspace` or custom variable |
| `AWS::StackId` | `terraform.workspace` or resource tags |

---

## 5. **Parameters/Variables**

### CloudFormation:
```yaml
Parameters:
  InstanceType:
    Type: String
    Default: t2.micro
    AllowedValues: [t2.micro, t2.small]
    Description: 'EC2 instance type'
```

### Terraform:
```hcl
variable "instance_type" {
  type        = string
  default     = "t2.micro"
  description = "EC2 instance type"
  
  validation {
    condition     = contains(["t2.micro", "t2.small"], var.instance_type)
    error_message = "Instance type must be t2.micro or t2.small"
  }
}
```

---

## 6. **Mappings/Locals**

### CloudFormation:
```yaml
Mappings:
  RegionMap:
    us-east-1:
      AMI: ami-123
      InstanceType: t2.micro
    us-west-2:
      AMI: ami-456
      InstanceType: t2.small
```

### Terraform:
```hcl
locals {
  region_map = {
    us-east-1 = {
      ami           = "ami-123"
      instance_type = "t2.micro"
    }
    us-west-2 = {
      ami           = "ami-456"
      instance_type = "t2.small"
    }
  }
}

# Usage:
resource "aws_instance" "my_instance" {
  ami           = local.region_map[data.aws_region.current.name].ami
  instance_type = local.region_map[data.aws_region.current.name].instance_type
}
```

---

## 7. **Conditions**

### CloudFormation:
```yaml
Conditions:
  CreateProd: !Equals [!Ref Environment, prod]

Resources:
  MyDB:
    Type: AWS::RDS::DBInstance
    Condition: CreateProd
    Properties:
      # Database properties
```

### Terraform:
```hcl
# Using count
resource "aws_rds_instance" "my_db" {
  count = var.environment == "prod" ? 1 : 0
  
  engine         = "mysql"
  instance_class = "db.t2.micro"
  # ... other properties
}

# Using for_each (more flexible)
resource "aws_rds_instance" "my_db" {
  for_each = var.environment == "prod" ? { prod = true } : {}
  
  engine         = "mysql"
  instance_class = "db.t2.micro"
  # ... other properties
}

# Using conditional expressions in properties
resource "aws_instance" "my_instance" {
  instance_type = var.environment == "prod" ? "t2.large" : "t2.micro"
  # ... other properties
}
```

---

## 8. **Resources**

### CloudFormation:
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      InstanceType: !Ref InstanceType
      ImageId: ami-0abcdef1234567890
      Tags:
        - Key: Name
          Value: MyInstance
```

### Terraform:
```hcl
resource "aws_instance" "my_instance" {
  instance_type = var.instance_type
  ami           = "ami-0abcdef1234567890"
  
  tags = {
    Name = "MyInstance"
  }
}
```

---

## 9. **Dependencies**

### CloudFormation:
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    DependsOn: MySecurityGroup
    Properties:
      SecurityGroupIds:
        - !Ref MySecurityGroup
```

### Terraform:
```hcl
# Automatic dependency via reference (preferred)
resource "aws_instance" "my_instance" {
  security_group_ids = [aws_security_group.my_sg.id]
  # Terraform automatically detects dependency
}

# Explicit dependency (when needed)
resource "aws_instance" "my_instance" {
  depends_on = [aws_security_group.my_sg]
  # ... properties
}
```

---

## 10. **Outputs**

### CloudFormation:
```yaml
Outputs:
  InstanceId:
    Description: 'EC2 Instance ID'
    Value: !Ref MyInstance
    Export:
      Name: MyStack-InstanceId
```

### Terraform:
```hcl
output "instance_id" {
  value       = aws_instance.my_instance.id
  description = "EC2 Instance ID"
}

# For cross-module references, outputs are automatically available
# when using modules
```

---

## 11. **Cross-Stack References**

### CloudFormation:
**Stack 1 (Network):**
```yaml
Outputs:
  VPCId:
    Value: !Ref MyVPC
    Export:
      Name: NetworkStack-VPCId
```

**Stack 2 (Application):**
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      SubnetId: !ImportValue NetworkStack-VPCId
```

### Terraform:
**Using Remote State:**
```hcl
# Network module/stack outputs
output "vpc_id" {
  value = aws_vpc.my_vpc.id
}

# Application stack references network stack
data "terraform_remote_state" "network" {
  backend = "s3"
  config = {
    bucket = "my-terraform-state"
    key    = "network/terraform.tfstate"
    region = "us-east-1"
  }
}

resource "aws_instance" "my_instance" {
  subnet_id = data.terraform_remote_state.network.outputs.vpc_id
  # ... other properties
}
```

**Using Modules (Preferred):**
```hcl
# Network module
module "network" {
  source = "./modules/network"
  # ... inputs
}

# Application uses network module outputs
resource "aws_instance" "my_instance" {
  subnet_id = module.network.vpc_id
  # ... other properties
}
```

---

## 12. **Nested Stacks**

### CloudFormation:
```yaml
Resources:
  NetworkStack:
    Type: AWS::CloudFormation::Stack
    Properties:
      TemplateURL: https://s3.amazonaws.com/bucket/network-template.yaml
      Parameters:
        Environment: !Ref Environment
```

### Terraform:
```hcl
module "network" {
  source = "./modules/network"
  # or
  # source = "git::https://github.com/example/terraform-network-module.git"
  
  environment = var.environment
  # ... other inputs
}

# Use module outputs
resource "aws_instance" "my_instance" {
  subnet_id = module.network.subnet_id
}
```

---

## 13. **Change Sets / Plan**

### CloudFormation:
```bash
# Create change set
aws cloudformation create-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set \
  --template-body file://template.yaml

# Describe change set
aws cloudformation describe-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set

# Execute change set
aws cloudformation execute-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set
```

### Terraform:
```bash
# Preview changes (equivalent to change set)
terraform plan

# Apply changes
terraform apply

# Plan with output file
terraform plan -out=tfplan
terraform apply tfplan
```

---

## 14. **Deployment Concepts: Stacks vs Workspaces/State**

### Stack Concept

**CloudFormation:**
- **Stack**: A collection of AWS resources managed as a single unit
- All resources in a stack are defined by the stack's CloudFormation template
- Stack is the deployment unit - you create, update, or delete entire stacks
- Stack name is required and unique within an AWS account/region

**Terraform:**
- **State**: The deployment unit (equivalent to a stack)
- All resources in a state file are managed together
- State file tracks the actual infrastructure
- Workspace or directory typically represents the "stack" concept
- State file name/location identifies the deployment unit

---

## 15. **Stack Operations**

### Create Stack

**CloudFormation:**
```bash
aws cloudformation create-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod \
               ParameterKey=InstanceType,ParameterValue=t2.large \
  --capabilities CAPABILITY_IAM \
  --tags Key=Project,Value=MyProject
```

**Terraform:**
```bash
# Initialize Terraform (download providers, modules)
terraform init

# Plan the deployment
terraform plan \
  -var="environment=prod" \
  -var="instance_type=t2.large" \
  -out=tfplan

# Apply the plan (creates resources)
terraform apply tfplan

# Or apply directly with variables
terraform apply \
  -var="environment=prod" \
  -var="instance_type=t2.large"

# Using variable files
terraform apply -var-file="prod.tfvars"
```

**Using Terraform Workspaces:**
```bash
# Create/select workspace (similar to stack name)
terraform workspace new prod
terraform workspace select prod

# Apply with workspace-specific variables
terraform apply -var-file="workspaces/prod.tfvars"
```

---

### Update Stack

**CloudFormation:**
```bash
aws cloudformation update-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod \
               ParameterKey=InstanceType,ParameterValue=t2.xlarge
```

**Terraform:**
```bash
# Plan changes
terraform plan \
  -var="instance_type=t2.xlarge" \
  -out=tfplan

# Apply changes
terraform apply tfplan

# Or apply directly
terraform apply -var="instance_type=t2.xlarge"
```

---

### Delete Stack

**CloudFormation:**
```bash
aws cloudformation delete-stack --stack-name my-stack
```

**Terraform:**
```bash
# Destroy all resources in the state
terraform destroy

# Destroy with variables
terraform destroy -var="environment=prod"

# Destroy specific resources
terraform destroy -target=aws_instance.my_instance

# Destroy with plan file
terraform destroy -out=destroy-plan
terraform apply destroy-plan
```

---

### Describe/Inspect Stack

**CloudFormation:**
```bash
# Describe stack
aws cloudformation describe-stacks --stack-name my-stack

# List stack resources
aws cloudformation describe-stack-resources --stack-name my-stack

# Get stack events
aws cloudformation describe-stack-events --stack-name my-stack

# List all stacks
aws cloudformation list-stacks
```

**Terraform:**
```bash
# Show current state
terraform show

# Show state in JSON format
terraform show -json

# List resources in state
terraform state list

# Show specific resource
terraform state show aws_instance.my_instance

# Show plan output
terraform show tfplan

# Validate configuration
terraform validate

# Format configuration files
terraform fmt

# Check what would change
terraform plan
```

---

## 16. **Stack States**

### CloudFormation Stack States:

| CloudFormation State | Description | Terraform Equivalent |
|---------------------|-------------|---------------------|
| `CREATE_IN_PROGRESS` | Stack creation in progress | `terraform apply` running |
| `CREATE_COMPLETE` | Stack created successfully | `terraform apply` completed successfully |
| `CREATE_FAILED` | Stack creation failed | `terraform apply` failed |
| `ROLLBACK_IN_PROGRESS` | Rolling back after failed creation | Terraform auto-rollback (if enabled) or manual cleanup |
| `ROLLBACK_COMPLETE` | Rollback completed | Resources cleaned up after failure |
| `UPDATE_IN_PROGRESS` | Stack update in progress | `terraform apply` updating resources |
| `UPDATE_COMPLETE` | Stack update completed | `terraform apply` completed |
| `UPDATE_ROLLBACK_IN_PROGRESS` | Rolling back after failed update | Terraform rollback |
| `UPDATE_ROLLBACK_COMPLETE` | Update rollback completed | Rollback completed |
| `DELETE_IN_PROGRESS` | Stack deletion in progress | `terraform destroy` running |
| `DELETE_COMPLETE` | Stack deleted | `terraform destroy` completed |

**Terraform State Management:**
```bash
# Check state status
terraform state list

# Verify state is locked (if using remote state)
# State locking prevents concurrent modifications

# Refresh state (sync with actual infrastructure)
terraform refresh

# Import existing resource into state
terraform import aws_instance.my_instance i-1234567890abcdef0

# Remove resource from state (without destroying)
terraform state rm aws_instance.my_instance

# Move resource in state (rename)
terraform state mv aws_instance.old_name aws_instance.new_name

# Pull state from remote backend
terraform state pull > state.json

# Push state to remote backend
terraform state push state.json
```

---

## 17. **Change Sets (Detailed)**

### CloudFormation Change Sets:

**Create Change Set:**
```bash
aws cloudformation create-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set \
  --template-body file://template.yaml \
  --parameters ParameterKey=InstanceType,ParameterValue=t2.xlarge \
  --capabilities CAPABILITY_IAM
```

**Describe Change Set:**
```bash
# List changes in change set
aws cloudformation describe-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set

# Get change set summary
aws cloudformation list-change-sets --stack-name my-stack
```

**Execute Change Set:**
```bash
aws cloudformation execute-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set
```

**Delete Change Set:**
```bash
aws cloudformation delete-change-set \
  --stack-name my-stack \
  --change-set-name my-change-set
```

### Terraform Plan (Equivalent to Change Sets):

**Basic Plan:**
```bash
# Create plan (equivalent to change set)
terraform plan -out=tfplan

# Show plan details
terraform show tfplan

# Apply plan (equivalent to execute change set)
terraform apply tfplan
```

**Advanced Plan Options:**
```bash
# Plan with detailed output
terraform plan -detailed-exitcode

# Plan with refresh disabled (faster)
terraform plan -refresh=false

# Plan specific targets
terraform plan -target=aws_instance.my_instance -out=tfplan

# Plan with destroy
terraform plan -destroy -out=destroy-plan

# Plan and save to file
terraform plan -out=tfplan > plan-output.txt

# Plan with JSON output
terraform plan -out=tfplan -json > plan.json
```

**Plan File Management:**
```bash
# Show plan
terraform show tfplan

# Validate plan file
terraform show -json tfplan | jq .

# Apply saved plan
terraform apply tfplan

# Plan files are binary - use terraform show to read them
```

**Key Differences:**
- **CloudFormation**: Change sets are stored server-side and can be reviewed in console
- **Terraform**: Plan files are local binary files (can be version controlled, but not recommended)
- **CloudFormation**: Change sets can be executed later by different users
- **Terraform**: Plan files are tied to specific state and may become stale

---

## 18. **Stack Sets vs Terraform Cloud/Enterprise**

### CloudFormation Stack Sets:

**Purpose**: Deploy stacks across multiple AWS accounts and regions

**Create Stack Set:**
```bash
aws cloudformation create-stack-set \
  --stack-set-name my-stack-set \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod \
  --administration-role-arn arn:aws:iam::123456789012:role/CloudFormationStackSetAdministrationRole \
  --execution-role-name CloudFormationStackSetExecutionRole
```

**Create Stack Instances:**
```bash
aws cloudformation create-stack-instances \
  --stack-set-name my-stack-set \
  --accounts 123456789012 234567890123 \
  --regions us-east-1 us-west-2
```

**Update Stack Set:**
```bash
aws cloudformation update-stack-set \
  --stack-set-name my-stack-set \
  --template-body file://template.yaml
```

### Terraform Cloud / Terraform Enterprise:

**Purpose**: Collaborative infrastructure management, workspaces, and policy enforcement

**Terraform Cloud Workspaces:**
```hcl
# Workspace configuration (in Terraform Cloud UI or via API)
# Each workspace = one "stack" concept

# Workspace variables can be set via:
# - UI
# - API
# - CLI
```

**CLI-driven Workflow:**
```bash
# Login to Terraform Cloud
terraform login

# Configure backend
terraform {
  cloud {
    organization = "my-org"
    workspaces {
      name = "prod-infrastructure"
    }
  }
}

# Initialize
terraform init

# Plan (runs in Terraform Cloud)
terraform plan

# Apply (runs in Terraform Cloud)
terraform apply
```

**API-driven Workflow:**
```bash
# Create workspace via API
curl \
  --header "Authorization: Bearer $TOKEN" \
  --header "Content-Type: application/vnd.api+json" \
  --request POST \
  --data @workspace.json \
  https://app.terraform.io/api/v2/organizations/my-org/workspaces
```

**Alternative: Multiple State Files:**
```bash
# Deploy to multiple environments using different state files
# prod/terraform.tfstate
terraform apply -var-file="prod.tfvars" -state="prod/terraform.tfstate"

# staging/terraform.tfstate
terraform apply -var-file="staging.tfvars" -state="staging/terraform.tfstate"

# Or use workspaces
terraform workspace new prod
terraform apply -var-file="prod.tfvars"

terraform workspace new staging
terraform apply -var-file="staging.tfvars"
```

**Multi-Account/Region Deployment:**
```bash
# Use different provider aliases
provider "aws" {
  alias  = "us_east"
  region = "us-east-1"
}

provider "aws" {
  alias  = "us_west"
  region = "us-west-2"
}

# Deploy to multiple regions
resource "aws_instance" "east" {
  provider = aws.us_east
  # ...
}

resource "aws_instance" "west" {
  provider = aws.us_west
  # ...
}
```

---

## 19. **Stack Policies vs Terraform State Locking**

### CloudFormation Stack Policies:

**Purpose**: Protect resources from accidental updates/deletions

**Stack Policy Example:**
```json
{
  "Statement": [
    {
      "Effect": "Deny",
      "Action": "Update:Delete",
      "Principal": "*",
      "Resource": "LogicalResourceId/ProductionDB"
    },
    {
      "Effect": "Allow",
      "Action": "Update:*",
      "Principal": "*",
      "Resource": "*"
    }
  ]
}
```

**Apply Stack Policy:**
```bash
aws cloudformation set-stack-policy \
  --stack-name my-stack \
  --stack-policy-body file://stack-policy.json
```

### Terraform State Locking:

**Purpose**: Prevent concurrent modifications to the same state

**Automatic Locking (with remote backend):**
```hcl
terraform {
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-state-lock"  # Enables locking
    encrypt        = true
  }
}
```

**Manual Lock Management:**
```bash
# Force unlock (if lock is stale)
terraform force-unlock <LOCK_ID>

# Lock is automatically released after apply/plan completes
```

**Resource Protection (Terraform equivalent to stack policies):**
```hcl
resource "aws_rds_instance" "production_db" {
  # ... properties
  
  lifecycle {
    prevent_destroy = true  # Prevents deletion
    ignore_changes  = [tags]  # Ignore tag changes
  }
}

# Or use terraform.tfvars with sensitive = true
variable "db_password" {
  type      = string
  sensitive = true
}
```

---

## 20. **Stack Events vs Terraform Logs**

### CloudFormation Stack Events:

**View Events:**
```bash
# Get stack events
aws cloudformation describe-stack-events --stack-name my-stack

# Watch events in real-time
watch -n 2 'aws cloudformation describe-stack-events --stack-name my-stack --max-items 5'
```

**Event Types:**
- Resource creation/update/deletion events
- Stack operation events
- Error messages
- Rollback events

### Terraform Logs:

**Enable Debug Logging:**
```bash
# Set log level
export TF_LOG=DEBUG
export TF_LOG_PATH=./terraform.log

# Run terraform commands
terraform apply

# View logs
cat terraform.log
```

**Log Levels:**
- `TRACE` - Most verbose
- `DEBUG` - Debug information
- `INFO` - General information
- `WARN` - Warnings
- `ERROR` - Errors only

**View Apply Output:**
```bash
# Verbose output
terraform apply -verbose

# JSON output
terraform apply -json

# Save output to file
terraform apply 2>&1 | tee apply.log
```

**CloudWatch Integration (for remote operations):**
- Terraform Cloud/Enterprise logs are available in the UI
- Can integrate with external logging systems
- State changes can trigger webhooks/notifications

---

## 21. **Parameter Files vs Variable Files**

### CloudFormation Parameter Files:

**Parameter File (JSON):**
```json
[
  {
    "ParameterKey": "Environment",
    "ParameterValue": "prod"
  },
  {
    "ParameterKey": "InstanceType",
    "ParameterValue": "t2.large"
  }
]
```

**Use Parameter File:**
```bash
aws cloudformation create-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters file://parameters.json
```

### Terraform Variable Files:

**Variable File (.tfvars):**
```hcl
# prod.tfvars
environment   = "prod"
instance_type = "t2.large"
db_size       = "large"
```

**Use Variable File:**
```bash
terraform apply -var-file="prod.tfvars"

# Multiple variable files
terraform apply \
  -var-file="common.tfvars" \
  -var-file="prod.tfvars"
```

**Auto-loaded Variable Files:**
```bash
# terraform.tfvars (auto-loaded)
# terraform.auto.tfvars (auto-loaded)
# *.auto.tfvars (auto-loaded)

# No need to specify -var-file for these
terraform apply
```

**Environment-specific Files:**
```bash
# Structure:
# environments/
#   prod.tfvars
#   staging.tfvars
#   dev.tfvars

terraform apply -var-file="environments/prod.tfvars"
```

---

## 22. **Stack Validation vs Terraform Validation**

### CloudFormation Template Validation:

```bash
# Validate template syntax
aws cloudformation validate-template \
  --template-body file://template.yaml

# Check for best practices (using cfn-lint)
cfn-lint template.yaml
```

### Terraform Validation:

```bash
# Validate configuration syntax
terraform validate

# Format configuration
terraform fmt

# Check formatting
terraform fmt -check

# Validate with custom checks (using check blocks in Terraform 1.5+)
# Or use external tools:
# - tflint (Terraform linter)
# - tfsec (security scanner)
# - checkov (security & compliance)

# Example with tflint
tflint

# Example with tfsec
tfsec .
```

---

## 23. **Rollback Mechanisms**

### CloudFormation Rollback:

**Automatic Rollback:**
- CloudFormation automatically rolls back on stack creation failure
- Can disable with `--disable-rollback` flag
- Rollback happens resource by resource in reverse order

**Manual Rollback:**
```bash
# Continue rollback if stuck
aws cloudformation continue-update-rollback \
  --stack-name my-stack

# Cancel update stack
aws cloudformation cancel-update-stack \
  --stack-name my-stack
```

### Terraform Rollback:

**No Built-in Rollback:**
- Terraform doesn't have automatic rollback
- Must manually revert changes or use version control

**Rollback Strategies:**

1. **Using Version Control:**
```bash
# Revert to previous version
git checkout HEAD~1 main.tf
terraform apply
```

2. **Using State History:**
```bash
# If using remote state with versioning (S3)
# Restore previous state file version
aws s3 cp s3://bucket/terraform.tfstate \
  s3://bucket/terraform.tfstate --version-id <version-id>

terraform state pull > current-state.json
terraform state push previous-state.json
```

3. **Using Terraform Cloud:**
- Terraform Cloud maintains state history
- Can restore previous state versions via UI

4. **Manual Resource Cleanup:**
```bash
# Destroy specific resources
terraform destroy -target=aws_instance.bad_instance

# Or remove from state and recreate
terraform state rm aws_instance.bad_instance
terraform apply
```

---

## 24. **State Management**

### CloudFormation:
- State is managed by AWS automatically
- Stack state is stored in CloudFormation service
- No manual state file management needed

### Terraform:
- State file tracks resource state
- Can be stored locally or remotely (S3, Azure Storage, etc.)
- Requires state file management

**Remote State Configuration:**
```hcl
terraform {
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "environments/prod/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-state-lock"
  }
}
```

---

## 25. **Deletion Policy**

### CloudFormation:
```yaml
Resources:
  MyDB:
    Type: AWS::RDS::DBInstance
    DeletionPolicy: Retain  # Options: Delete, Retain, Snapshot
    Properties:
      # Database properties
```

### Terraform:
```hcl
resource "aws_rds_instance" "my_db" {
  engine         = "mysql"
  instance_class = "db.t2.micro"
  
  lifecycle {
    prevent_destroy = true  # Prevents deletion
  }
}

# Or remove from state without destroying
# terraform state rm aws_rds_instance.my_db
```

---

## 26. **Complete Example Comparison**

### CloudFormation:
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Simple EC2 instance template'

Parameters:
  InstanceType:
    Type: String
    Default: t2.micro
    Description: 'EC2 instance type'
  
  Environment:
    Type: String
    Default: dev
    AllowedValues: [dev, staging, prod]

Mappings:
  EnvironmentMap:
    dev:
      InstanceType: t2.micro
    prod:
      InstanceType: t2.large

Conditions:
  IsProduction: !Equals [!Ref Environment, prod]

Resources:
  MySecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: 'Security group for EC2'
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 0.0.0.0/0

  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      InstanceType: !If
        - IsProduction
        - !FindInMap [EnvironmentMap, !Ref Environment, InstanceType]
        - !Ref InstanceType
      ImageId: ami-0abcdef1234567890
      SecurityGroupIds:
        - !Ref MySecurityGroup
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-instance'
        - Key: Environment
          Value: !Ref Environment

Outputs:
  InstanceId:
    Description: 'EC2 Instance ID'
    Value: !Ref MyInstance
  
  InstancePublicIP:
    Description: 'Public IP address'
    Value: !GetAtt MyInstance.PublicIp
```

### Terraform:
```hcl
terraform {
  required_version = ">= 1.0"
}

variable "instance_type" {
  type        = string
  default     = "t2.micro"
  description = "EC2 instance type"
}

variable "environment" {
  type        = string
  default     = "dev"
  description = "Environment name"
  
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod"
  }
}

locals {
  environment_map = {
    dev = {
      instance_type = "t2.micro"
    }
    prod = {
      instance_type = "t2.large"
    }
  }
  
  is_production = var.environment == "prod"
  
  instance_type = local.is_production ? local.environment_map[var.environment].instance_type : var.instance_type
}

resource "aws_security_group" "my_sg" {
  name        = "${var.environment}-sg"
  description = "Security group for EC2"
  
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "my_instance" {
  instance_type = local.instance_type
  ami           = "ami-0abcdef1234567890"
  
  security_groups = [aws_security_group.my_sg.name]
  
  tags = {
    Name        = "${var.environment}-instance"
    Environment = var.environment
  }
}

output "instance_id" {
  description = "EC2 Instance ID"
  value       = aws_instance.my_instance.id
}

output "instance_public_ip" {
  description = "Public IP address"
  value       = aws_instance.my_instance.public_ip
}
```

---

## Key Differences Summary

| Aspect | CloudFormation | Terraform |
|--------|----------------|-----------|
| **Language** | JSON/YAML | HCL (HashiCorp Configuration Language) |
| **State Management** | Managed by AWS | State file (local or remote) |
| **Multi-Cloud** | AWS only | Multi-cloud (AWS, Azure, GCP, etc.) |
| **Functions** | Intrinsic functions (`!Ref`, `!GetAtt`) | HCL expressions and functions |
| **Modules** | Nested stacks | Modules (more flexible) |
| **Change Preview** | Change sets | `terraform plan` |
| **Provider Support** | AWS services only | Multiple providers via plugins |
| **Syntax** | Declarative YAML/JSON | More programmatic HCL |
| **Learning Curve** | AWS-specific | General IaC concepts |

---

## When to Use Which?

### Use CloudFormation when:
- Working exclusively with AWS
- Need tight integration with AWS services
- Prefer AWS-managed state
- Team is already familiar with AWS ecosystem

### Use Terraform when:
- Working with multiple cloud providers
- Need more flexible and readable syntax
- Want better module ecosystem
- Prefer open-source, vendor-neutral tool
- Need more programmatic capabilities

---

## Additional Resources

- **Terraform AWS Provider**: https://registry.terraform.io/providers/hashicorp/aws/latest/docs
- **Terraform Documentation**: https://www.terraform.io/docs
- **CloudFormation Documentation**: https://docs.aws.amazon.com/cloudformation/
- **Terraform vs CloudFormation Comparison**: Various online resources

---

## Migration Tips

1. **Start Small**: Convert one stack at a time
2. **Use Terraform Import**: Import existing resources into Terraform state
3. **Validate**: Use `terraform validate` and `terraform plan` frequently
4. **State Management**: Set up remote state early (S3 + DynamoDB for AWS)
5. **Modules**: Break down large configurations into reusable modules
6. **Testing**: Use `terraform plan` extensively before `terraform apply`

---

## Common Patterns Translation

### Pattern 1: Environment-Specific Resources

**CloudFormation:**
```yaml
Conditions:
  IsProduction: !Equals [!Ref Environment, prod]

Resources:
  ProductionDB:
    Type: AWS::RDS::DBInstance
    Condition: IsProduction
```

**Terraform:**
```hcl
resource "aws_rds_instance" "production_db" {
  count = var.environment == "prod" ? 1 : 0
  # ... properties
}
```

### Pattern 2: Dynamic Resource Naming

**CloudFormation:**
```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub '${Environment}-${AWS::StackName}-${AWS::AccountId}'
```

**Terraform:**
```hcl
data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

resource "aws_s3_bucket" "my_bucket" {
  bucket = "${var.environment}-${terraform.workspace}-${data.aws_caller_identity.current.account_id}"
}
```

### Pattern 3: User Data Scripts

**CloudFormation:**
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      UserData:
        Fn::Base64: !Sub |
          #!/bin/bash
          yum update -y
```

**Terraform:**
```hcl
resource "aws_instance" "my_instance" {
  user_data = base64encode(<<-EOF
    #!/bin/bash
    yum update -y
  EOF
  )
}
```

---

This correlation guide should help you translate CloudFormation knowledge to Terraform and vice versa!
