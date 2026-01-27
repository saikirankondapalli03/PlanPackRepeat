# AWS to Azure ARM Correlation Guide

## Overview
This guide helps AWS cloud developers understand Azure Resource Manager (ARM) by correlating familiar AWS concepts with their Azure equivalents.

---

## Core Concepts Mapping

### 1. Infrastructure as Code

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| CloudFormation | ARM Templates (JSON/Bicep) | Infrastructure as Code tool |
| CloudFormation Stack | Resource Group + Deployment | Logical grouping of resources |
| CloudFormation Template | ARM Template / Bicep File | Declarative infrastructure definition |
| StackSets | Management Groups / Subscription-level deployments | Multi-account/region deployments |

### 2. Resource Organization

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| AWS Account | Azure Subscription | Top-level billing and access boundary |
| AWS Region | Azure Region | Geographic location |
| AWS Availability Zone | Azure Availability Zone | Isolated location within a region |
| AWS Organization | Azure Management Group | Hierarchical organization structure |
| Tag | Tag | Key-value metadata for resources |
| Resource Group | Resource Group | Logical container for related resources |

### 3. Compute Services

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| EC2 Instance | Virtual Machine (VM) | Virtualized compute instance |
| EC2 Instance Type | VM Size (e.g., Standard_D2s_v3) | Compute capacity specification |
| Auto Scaling Group | Virtual Machine Scale Set (VMSS) | Auto-scaling compute group |
| Elastic Load Balancer | Azure Load Balancer / Application Gateway | Traffic distribution |
| Lambda | Azure Functions | Serverless compute |
| ECS (Elastic Container Service) | Azure Container Instances / AKS | Container orchestration |
| EKS | Azure Kubernetes Service (AKS) | Managed Kubernetes |

### 4. Storage Services

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| S3 | Azure Blob Storage | Object storage |
| EBS | Azure Managed Disks | Block storage for VMs |
| EFS | Azure Files | Managed file shares |
| Glacier | Azure Archive Storage | Long-term archival storage |
| S3 Lifecycle Policies | Blob Lifecycle Management | Automated data lifecycle |

### 5. Networking

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| VPC | Virtual Network (VNet) | Isolated network environment |
| Subnet | Subnet | Network segment within VPC/VNet |
| Internet Gateway | Internet Gateway (implicit) | Internet connectivity |
| NAT Gateway | NAT Gateway | Outbound internet for private subnets |
| Security Group | Network Security Group (NSG) | Firewall rules |
| Route Table | Route Table | Network routing rules |
| VPC Peering | VNet Peering | Connect two VNets |
| VPN Gateway | VPN Gateway | Site-to-site VPN |
| Direct Connect | ExpressRoute | Dedicated private connection |

### 6. Identity & Access Management

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| IAM | Azure Active Directory (Azure AD) / RBAC | Identity and access management |
| IAM User | Azure AD User | Individual user account |
| IAM Role | Managed Identity / Service Principal | Service-to-service authentication |
| IAM Policy | Role-Based Access Control (RBAC) | Permission definitions |
| AssumeRole | Managed Identity / Service Principal | Temporary credentials |
| AWS SSO | Azure AD SSO | Single sign-on |

### 7. Database Services

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| RDS | Azure SQL Database / Azure Database | Managed relational database |
| DynamoDB | Cosmos DB | NoSQL database |
| ElastiCache | Azure Cache for Redis | In-memory caching |
| Redshift | Azure Synapse Analytics | Data warehouse |
| DocumentDB | Cosmos DB (MongoDB API) | Document database |

### 8. Monitoring & Logging

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| CloudWatch | Azure Monitor | Monitoring and metrics |
| CloudWatch Logs | Log Analytics Workspace | Centralized logging |
| CloudWatch Alarms | Azure Monitor Alerts | Alerting |
| CloudTrail | Azure Activity Log | Audit logging |
| X-Ray | Application Insights | Application performance monitoring |

### 9. Security & Compliance

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| AWS Secrets Manager | Azure Key Vault | Secrets management |
| AWS KMS | Azure Key Vault | Key management |
| AWS WAF | Azure Application Gateway WAF | Web application firewall |
| AWS Shield | Azure DDoS Protection | DDoS protection |
| AWS Config | Azure Policy | Compliance and governance |

### 10. DevOps & CI/CD

| AWS | Azure ARM | Description |
|-----|-----------|-------------|
| CodePipeline | Azure DevOps Pipelines | CI/CD orchestration |
| CodeBuild | Azure Pipelines | Build service |
| CodeDeploy | Azure Pipelines / Azure DevOps | Deployment service |
| Systems Manager | Azure Automation | Configuration management |

---

## Key Differences to Remember

### 1. Resource Groups
- **AWS**: Resources are standalone; tags used for organization
- **Azure**: Resources MUST belong to a Resource Group (logical container)
- **Impact**: Resource groups are fundamental to ARM; all resources are scoped to a resource group

### 2. ARM Templates vs CloudFormation
- **CloudFormation**: YAML or JSON, imperative at times
- **ARM Templates**: JSON (verbose) or Bicep (simpler DSL)
- **Bicep**: Azure's domain-specific language, compiles to ARM JSON

### 3. Deployment Model
- **AWS**: Stack-based (create, update, delete stack)
- **Azure**: Resource Group + Deployment (deploy to resource group)
- **Azure**: Supports incremental and complete deployment modes

### 4. Naming Conventions
- **AWS**: Flexible naming (with some restrictions)
- **Azure**: Strict naming rules (lowercase, alphanumeric, hyphens)
- **Azure**: Resource names often include resource type abbreviation

### 5. State Management
- **AWS CloudFormation**: Maintains state in CloudFormation service
- **Azure ARM**: State maintained in Azure Resource Manager
- **Both**: Idempotent operations (can run multiple times safely)

---

## ARM Template Structure

### Basic ARM Template (JSON)
```json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "vmName": {
      "type": "string",
      "defaultValue": "myVM"
    }
  },
  "variables": {
    "storageAccountName": "[concat('storage', uniqueString(resourceGroup().id))]"
  },
  "resources": [
    {
      "type": "Microsoft.Storage/storageAccounts",
      "apiVersion": "2021-04-01",
      "name": "[variables('storageAccountName')]",
      "location": "[resourceGroup().location]",
      "sku": {
        "name": "Standard_LRS"
      },
      "kind": "StorageV2"
    }
  ],
  "outputs": {
    "storageAccountName": {
      "type": "string",
      "value": "[variables('storageAccountName')]"
    }
  }
}
```

### Equivalent AWS CloudFormation Template (JSON)
```json
{
  "AWSTemplateFormatVersion": "2010-09-09",
  "Description": "Basic CloudFormation template equivalent to ARM template",
  "Parameters": {
    "VMName": {
      "Type": "String",
      "Default": "myVM",
      "Description": "Name of the virtual machine"
    }
  },
  "Resources": {
    "StorageBucket": {
      "Type": "AWS::S3::Bucket",
      "Properties": {
        "BucketName": {
          "Fn::Sub": "storage-${AWS::StackName}-${AWS::AccountId}"
        }
      },
      "DeletionPolicy": "Retain"
    }
  },
  "Outputs": {
    "StorageBucketName": {
      "Description": "Name of the S3 bucket",
      "Value": {
        "Ref": "StorageBucket"
      },
      "Export": {
        "Name": {
          "Fn::Sub": "${AWS::StackName}-StorageBucketName"
        }
      }
    },
    "StorageBucketArn": {
      "Description": "ARN of the S3 bucket",
      "Value": {
        "Fn::GetAtt": ["StorageBucket", "Arn"]
      }
    }
  }
}
```

### Equivalent AWS CloudFormation Template (YAML)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: Basic CloudFormation template equivalent to ARM template

Parameters:
  VMName:
    Type: String
    Default: myVM
    Description: Name of the virtual machine

Resources:
  StorageBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub 'storage-${AWS::StackName}-${AWS::AccountId}'
    DeletionPolicy: Retain

Outputs:
  StorageBucketName:
    Description: Name of the S3 bucket
    Value: !Ref StorageBucket
    Export:
      Name: !Sub '${AWS::StackName}-StorageBucketName'
  
  StorageBucketArn:
    Description: ARN of the S3 bucket
    Value: !GetAtt StorageBucket.Arn
```

### Key Differences: ARM vs CloudFormation

| ARM Template | CloudFormation | Notes |
|--------------|----------------|-------|
| `parameters` | `Parameters` | Both define input values |
| `variables` | No direct equivalent | Use `Mappings` or `Conditions` for similar functionality |
| `resources` | `Resources` | Core resource definitions |
| `outputs` | `Outputs` | Return values from stack |
| `[concat(...)]` | `!Sub` or `Fn::Join` | String concatenation |
| `[uniqueString(...)]` | `AWS::StackName` + `AWS::AccountId` | Generate unique names |
| `[resourceGroup().location]` | `AWS::Region` | Get deployment region |
| `[reference(...)]` | `!Ref` or `!GetAtt` | Reference other resources |
| `apiVersion` | Implicit (latest) | API versioning handled automatically |

### Equivalent Bicep (Simpler)
```bicep
@description('Name of the virtual machine')
param vmName string = 'myVM'

var storageAccountName = 'storage${uniqueString(resourceGroup().id)}'

resource storageAccount 'Microsoft.Storage/storageAccounts@2021-04-01' = {
  name: storageAccountName
  location: resourceGroup().location
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
}

output storageAccountName string = storageAccountName
```

---

## Common ARM Template Functions (vs CloudFormation)

| CloudFormation | ARM Template | Purpose |
|----------------|--------------|---------|
| `!Ref` | `[resourceId(...)]` or `[reference(...)]` | Reference resource |
| `!GetAtt` | `[reference(...).property]` | Get resource attribute |
| `!Sub` | `[concat(...)]` or string interpolation | String substitution |
| `!Join` | `[concat(...)]` | Join strings |
| `!Select` | `[split(...)]` or array indexing | Array operations |
| `!FindInMap` | `[variables('mapName')['key']]` | Map lookup |
| `!If` | `[if(condition, trueValue, falseValue)]` | Conditional |
| `Fn::Base64` | `[base64(...)]` | Base64 encoding |
| `Fn::Sub` | `[format(...)]` | Format strings |

---

## Deployment Commands

### AWS CloudFormation
```bash
aws cloudformation create-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters ParameterKey=KeyName,ParameterValue=MyKey
```

### Azure ARM
```bash
# Using Azure CLI
az deployment group create \
  --resource-group myResourceGroup \
  --template-file template.json \
  --parameters @parameters.json

# Using PowerShell
New-AzResourceGroupDeployment \
  -ResourceGroupName myResourceGroup \
  -TemplateFile template.json \
  -TemplateParameterFile parameters.json
```

---

## Best Practices

### 1. Resource Naming
- Use consistent naming conventions
- Include resource type in name (e.g., `rg-prod-eastus`)
- Use tags for additional metadata

### 2. Template Organization
- Use linked/nested templates for complex deployments
- Separate parameters into parameter files
- Use variables for computed values

### 3. Security
- Never hardcode secrets in templates
- Use Key Vault references for secrets
- Use managed identities instead of service principals where possible

### 4. Testing
- Validate templates before deployment (`az deployment group validate`)
- Use `what-if` operations to preview changes
- Test in non-production first

### 5. Version Control
- Store templates in source control
- Use version tags for template versions
- Document changes in templates

---

## Interview Preparation Tips

1. **Understand ARM Template Structure**: Know parameters, variables, resources, outputs
2. **Know Bicep Basics**: Modern Azure IaC language (compiles to ARM JSON)
3. **Resource Group Concepts**: Everything in Azure is scoped to resource groups
4. **Deployment Modes**: Incremental vs Complete
5. **ARM Template Functions**: Common functions like `concat`, `reference`, `uniqueString`
6. **RBAC vs IAM**: Understand Azure's role-based access control
7. **Managed Identities**: Azure's equivalent to IAM roles
8. **Azure CLI vs PowerShell**: Know both command-line tools
9. **ARM Template Validation**: How to validate before deployment
10. **What-If Operations**: Preview changes before applying

---

## Additional Resources

- **Azure ARM Template Reference**: https://docs.microsoft.com/azure/azure-resource-manager/templates/
- **Bicep Documentation**: https://docs.microsoft.com/azure/azure-resource-manager/bicep/
- **Azure Quickstart Templates**: https://azure.microsoft.com/resources/templates/
- **ARM Template Functions**: https://docs.microsoft.com/azure/azure-resource-manager/templates/template-functions
