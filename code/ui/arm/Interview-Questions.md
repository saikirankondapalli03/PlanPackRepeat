# Azure ARM Interview Questions for AWS Developers

## Table of Contents
1. [Fundamental Concepts](#fundamental-concepts)
2. [ARM Templates](#arm-templates)
3. [Resource Groups](#resource-groups)
4. [Deployment & Operations](#deployment--operations)
5. [Networking](#networking)
6. [Security & Identity](#security--identity)
7. [Advanced Topics](#advanced-topics)
8. [Scenario-Based Questions](#scenario-based-questions)

---

## Fundamental Concepts

### Q1: What is Azure Resource Manager (ARM) and how does it differ from AWS CloudFormation?

**Answer:**
Azure Resource Manager (ARM) is Azure's deployment and management service that provides a consistent management layer for creating, updating, and deleting resources in your Azure subscription.

**Key Differences:**
- **Resource Groups**: ARM requires all resources to be in a Resource Group (logical container), while AWS resources are standalone with tags for organization
- **Template Language**: ARM uses JSON templates (or Bicep), while CloudFormation uses YAML/JSON
- **Deployment Scope**: ARM deployments are scoped to Resource Groups, while CloudFormation stacks are account/region scoped
- **State Management**: Both maintain state, but ARM's state is tied to Resource Groups

**AWS Equivalent**: CloudFormation is the closest equivalent, but ARM is more tightly integrated with Azure's resource model.

---

### Q2: Explain the concept of Resource Groups in Azure. How do they compare to AWS resource organization?

**Answer:**
Resource Groups are logical containers that hold related resources for an Azure solution. They are fundamental to ARM - every resource must belong to exactly one resource group.

**Key Characteristics:**
- Resources can be moved between resource groups
- Resource groups can be deleted (deletes all contained resources)
- Resource groups have a location (metadata storage location)
- Resources in a group can be in different regions

**AWS Comparison:**
- **AWS**: Uses tags for organization; no mandatory grouping
- **Azure**: Mandatory resource groups; tags are supplementary
- **AWS**: Resources are independent entities
- **Azure**: Resources are scoped to resource groups

**Example:**
```bash
# Azure - Resources must be in a resource group
az group create --name myResourceGroup --location eastus
az vm create --resource-group myResourceGroup --name myVM ...

# AWS - Resources are standalone, organized by tags
aws ec2 run-instances --instance-type t2.micro --tags Key=Environment,Value=Prod
```

---

### Q3: What are the main components of an ARM template?

**Answer:**
An ARM template has four main sections:

1. **$schema**: Points to the JSON schema file that describes the version of the template language
2. **contentVersion**: Version of the template (e.g., "1.0.0.0")
3. **parameters**: Values provided during deployment to customize the deployment
4. **variables**: Values that are used multiple times in the template
5. **resources**: Resources to deploy or update
6. **outputs**: Values returned after deployment

**Example Structure:**
```json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": { },
  "variables": { },
  "resources": [ ],
  "outputs": { }
}
```

**AWS Comparison:**
- CloudFormation templates have similar structure: Parameters, Mappings, Conditions, Resources, Outputs

---

## ARM Templates

### Q4: What is Bicep and how does it relate to ARM templates?

**Answer:**
Bicep is a domain-specific language (DSL) that provides a simpler syntax for authoring ARM templates. It's a transparent abstraction over ARM template JSON.

**Key Points:**
- Bicep compiles to ARM JSON templates
- More readable and concise than JSON
- Type-safe with IntelliSense support
- No runtime cost (compiles to JSON before deployment)
- Official Azure IaC language (recommended over JSON)

**Example Comparison:**

**ARM JSON:**
```json
{
  "type": "Microsoft.Storage/storageAccounts",
  "apiVersion": "2021-04-01",
  "name": "[concat('storage', uniqueString(resourceGroup().id))]",
  "location": "[resourceGroup().location]",
  "sku": { "name": "Standard_LRS" }
}
```

**Bicep:**
```bicep
resource storageAccount 'Microsoft.Storage/storageAccounts@2021-04-01' = {
  name: 'storage${uniqueString(resourceGroup().id)}'
  location: resourceGroup().location
  sku: {
    name: 'Standard_LRS'
  }
}
```

**AWS Comparison:**
- Similar to how CDK (Cloud Development Kit) provides a higher-level abstraction over CloudFormation

---

### Q5: Explain ARM template functions. How do they compare to CloudFormation intrinsic functions?

**Answer:**
ARM template functions are built-in functions that can be used in templates to retrieve values, work with strings and numbers, and get deployment information.

**Common Functions:**

| ARM Function | CloudFormation Equivalent | Purpose |
|--------------|---------------------------|---------|
| `[reference(resourceId)]` | `!Ref` | Reference a resource |
| `[reference(resourceId).property]` | `!GetAtt` | Get resource attribute |
| `[concat('str1', 'str2')]` | `!Join` or `!Sub` | Concatenate strings |
| `[uniqueString(...)]` | Custom solution needed | Generate unique string |
| `[resourceGroup().location]` | `!Ref AWS::Region` | Get deployment location |
| `[if(condition, true, false)]` | `!If` | Conditional logic |
| `[parameters('paramName')]` | `!Ref ParameterName` | Reference parameter |

**Example:**
```json
{
  "name": "[concat(parameters('storagePrefix'), uniqueString(resourceGroup().id))]",
  "location": "[resourceGroup().location]",
  "properties": {
    "accountType": "[if(equals(parameters('environment'), 'prod'), 'Standard_GRS', 'Standard_LRS')]"
  }
}
```

---

### Q6: What are linked templates and when would you use them?

**Answer:**
Linked templates allow you to break down complex deployments into smaller, reusable templates. A parent template calls one or more linked templates.

**Use Cases:**
- Modularize large templates
- Reuse templates across multiple deployments
- Separate concerns (networking, compute, storage)
- Manage template size and complexity

**Example:**
```json
{
  "resources": [
    {
      "type": "Microsoft.Resources/deployments",
      "apiVersion": "2021-04-01",
      "name": "linkedTemplate",
      "properties": {
        "mode": "Incremental",
        "templateLink": {
          "uri": "https://mystorageaccount.blob.core.windows.net/templates/linkedTemplate.json"
        },
        "parameters": {
          "storageAccountName": { "value": "[parameters('storageAccountName')]" }
        }
      }
    }
  ]
}
```

**AWS Comparison:**
- Similar to CloudFormation nested stacks or stack imports

---

## Resource Groups

### Q7: Can resources in a resource group be in different regions? Explain.

**Answer:**
Yes, resources in a resource group can be in different Azure regions. The resource group itself has a location (metadata storage), but individual resources can be deployed to any region.

**Important Points:**
- Resource group location stores metadata about the resource group
- Resources can be in different regions than their resource group
- Consider latency and data residency requirements when choosing regions
- Some resources (like storage accounts) have region-specific naming requirements

**Example:**
```bash
# Resource group in East US
az group create --name myRG --location eastus

# VM in East US
az vm create --resource-group myRG --location eastus --name vm1

# Storage account in West US
az storage account create --resource-group myRG --location westus --name mystorage
```

---

### Q8: What happens when you delete a resource group?

**Answer:**
Deleting a resource group deletes ALL resources contained within it. This is a cascading delete operation.

**Important Considerations:**
- **Irreversible**: Cannot be undone (unless soft delete is enabled for specific resources)
- **Cascading**: All resources in the group are deleted
- **Dependencies**: Resources with dependencies outside the group may prevent deletion
- **Locks**: Resource group locks can prevent deletion

**Best Practices:**
- Use resource locks for production resource groups
- Enable soft delete for critical resources (Key Vault, Storage)
- Test deletions in non-production first
- Use separate resource groups for production and non-production

**AWS Comparison:**
- Similar to deleting a CloudFormation stack (deletes all stack resources)

---

## Deployment & Operations

### Q9: What are the different deployment modes in ARM and when would you use each?

**Answer:**
ARM supports two deployment modes:

1. **Incremental Mode** (Default)
   - Only adds/updates resources defined in the template
   - Resources not in the template remain unchanged
   - Use for: Most deployments, adding resources to existing infrastructure

2. **Complete Mode**
   - Deletes resources in the resource group that aren't in the template
   - Use for: Clean deployments, ensuring only template-defined resources exist
   - **Warning**: Can delete resources not in the template!

**Example:**
```bash
# Incremental (default)
az deployment group create --resource-group myRG --template-file template.json

# Complete mode
az deployment group create \
  --resource-group myRG \
  --template-file template.json \
  --mode Complete
```

**AWS Comparison:**
- CloudFormation always operates in "incremental" mode (updates stack)
- To delete resources, remove them from template and update stack

---

### Q10: How do you validate an ARM template before deployment?

**Answer:**
ARM provides several validation methods:

1. **Template Validation** (`az deployment group validate`)
   - Validates template syntax and structure
   - Checks parameter types and required parameters
   - Does NOT check if resources can actually be created

2. **What-If Operation** (`az deployment group what-if`)
   - Preview changes before deployment
   - Shows what resources will be created, modified, or deleted
   - Helps prevent unintended changes

**Example:**
```bash
# Validate template
az deployment group validate \
  --resource-group myRG \
  --template-file template.json \
  --parameters @parameters.json

# Preview changes (what-if)
az deployment group what-if \
  --resource-group myRG \
  --template-file template.json \
  --parameters @parameters.json
```

**AWS Comparison:**
- CloudFormation: `aws cloudformation validate-template`
- CloudFormation: `aws cloudformation create-change-set` (preview changes)

---

### Q11: Explain ARM template parameters and how to handle sensitive data.

**Answer:**
Parameters allow you to customize deployments without changing the template. They're defined in the template and provided during deployment.

**Handling Sensitive Data:**
1. **Never hardcode secrets** in templates or parameter files
2. **Use Azure Key Vault references** for secrets
3. **Use secureString type** for sensitive parameters
4. **Use managed identities** instead of storing credentials

**Example:**
```json
{
  "parameters": {
    "adminPassword": {
      "type": "secureString",
      "metadata": {
        "description": "Admin password for the VM"
      }
    },
    "sqlConnectionString": {
      "type": "secureString",
      "metadata": {
        "description": "SQL connection string"
      }
    }
  },
  "resources": [
    {
      "type": "Microsoft.KeyVault/vaults/secrets",
      "apiVersion": "2021-06-01-preview",
      "name": "[concat(parameters('keyVaultName'), '/', 'sqlConnectionString')]",
      "properties": {
        "value": "[parameters('sqlConnectionString')]"
      }
    }
  ]
}
```

**Key Vault Reference:**
```json
{
  "properties": {
    "connectionString": {
      "reference": {
        "keyVault": {
          "id": "/subscriptions/{subscription-id}/resourceGroups/{rg}/providers/Microsoft.KeyVault/vaults/{vault-name}"
        },
        "secretName": "sqlConnectionString"
      }
    }
  }
}
```

**AWS Comparison:**
- Similar to CloudFormation parameters with `NoEcho: true`
- AWS Systems Manager Parameter Store or Secrets Manager for secrets

---

## Networking

### Q12: How does Azure Virtual Network (VNet) compare to AWS VPC?

**Answer:**
Azure VNet and AWS VPC are conceptually similar but have some differences:

**Similarities:**
- Both provide isolated network environments
- Both support subnets, route tables, and network security groups
- Both support VPN and private connectivity options

**Key Differences:**

| Feature | AWS VPC | Azure VNet |
|---------|---------|------------|
| **Default VPC** | Yes (created automatically) | No (must create) |
| **CIDR Blocks** | Single CIDR per VPC | Multiple CIDR blocks per VNet |
| **Subnet Types** | Public/Private | Subnet (no implicit public/private) |
| **Internet Gateway** | Explicit resource | Implicit (via public IP) |
| **NAT Gateway** | Explicit resource | Explicit resource (similar) |
| **Peering** | VPC Peering | VNet Peering (similar) |

**Example ARM Template for VNet:**
```json
{
  "resources": [
    {
      "type": "Microsoft.Network/virtualNetworks",
      "apiVersion": "2021-05-01",
      "name": "[parameters('vnetName')]",
      "location": "[resourceGroup().location]",
      "properties": {
        "addressSpace": {
          "addressPrefixes": ["10.0.0.0/16"]
        },
        "subnets": [
          {
            "name": "subnet1",
            "properties": {
              "addressPrefix": "10.0.1.0/24"
            }
          }
        ]
      }
    }
  ]
}
```

---

### Q13: What is a Network Security Group (NSG) and how does it compare to AWS Security Groups?

**Answer:**
Network Security Groups (NSG) are Azure's firewall solution, similar to AWS Security Groups.

**Key Differences:**

| Feature | AWS Security Group | Azure NSG |
|---------|-------------------|-----------|
| **Stateful** | Yes | Yes |
| **Default Rules** | Deny all inbound, allow all outbound | Deny all inbound and outbound |
| **Application** | Applied to ENI/instance | Applied to subnet or NIC |
| **Rules** | Inbound/Outbound separate | Inbound/Outbound in same group |
| **Priority** | N/A | Priority-based (100-4096) |

**NSG Rules:**
- **Priority**: Lower numbers = higher priority
- **Direction**: Inbound or Outbound
- **Source/Destination**: IP, service tag, or application security group
- **Protocol**: TCP, UDP, ICMP, or Any
- **Port Range**: Single port or range

**Example:**
```json
{
  "type": "Microsoft.Network/networkSecurityGroups",
  "apiVersion": "2021-05-01",
  "name": "[parameters('nsgName')]",
  "properties": {
    "securityRules": [
      {
        "name": "AllowHTTP",
        "properties": {
          "priority": 1000,
          "access": "Allow",
          "direction": "Inbound",
          "destinationPortRange": "80",
          "protocol": "Tcp",
          "sourceAddressPrefix": "*",
          "destinationAddressPrefix": "*"
        }
      }
    ]
  }
}
```

---

## Security & Identity

### Q14: What are Managed Identities and how do they compare to AWS IAM Roles?

**Answer:**
Managed Identities are Azure's solution for service-to-service authentication, eliminating the need to manage credentials.

**Types:**
1. **System-assigned Managed Identity**
   - Tied to a specific Azure resource
   - Created and deleted with the resource
   - Unique to that resource

2. **User-assigned Managed Identity**
   - Standalone resource
   - Can be assigned to multiple resources
   - Independent lifecycle

**AWS Comparison:**
- Similar to IAM Roles for EC2/ECS/Lambda
- No need to store access keys or secrets
- Automatically rotated credentials

**Example:**
```json
{
  "type": "Microsoft.Compute/virtualMachines",
  "apiVersion": "2021-11-01",
  "identity": {
    "type": "SystemAssigned"
  },
  "properties": {
    // VM properties
  }
}
```

**Usage:**
```bash
# Azure - Get token using managed identity
az rest --method GET \
  --uri 'https://management.azure.com/subscriptions/{subscription-id}/resourceGroups/{rg}?api-version=2021-04-01' \
  --headers Authorization="Bearer $(az account get-access-token --query accessToken -o tsv)"
```

---

### Q15: Explain Azure RBAC and how it differs from AWS IAM.

**Answer:**
Azure Role-Based Access Control (RBAC) is Azure's authorization system, similar to AWS IAM.

**Key Concepts:**
- **Security Principal**: User, group, service principal, or managed identity
- **Role Definition**: Collection of permissions (like IAM policy)
- **Scope**: Management group, subscription, resource group, or resource
- **Role Assignment**: Assigns a role to a principal at a scope

**Built-in Roles:**
- **Owner**: Full access including access management
- **Contributor**: Full access except access management
- **Reader**: Read-only access
- **User Access Administrator**: Manage user access

**AWS Comparison:**

| AWS IAM | Azure RBAC | Description |
|---------|------------|-------------|
| IAM User | Azure AD User | Individual identity |
| IAM Role | Managed Identity / Service Principal | Service identity |
| IAM Policy | Role Definition | Permission set |
| IAM Policy Attachment | Role Assignment | Assign permissions |
| Resource-based Policy | Resource-level role assignment | Resource-specific permissions |

**Example:**
```bash
# Assign Contributor role to a user at resource group scope
az role assignment create \
  --assignee user@example.com \
  --role Contributor \
  --scope /subscriptions/{subscription-id}/resourceGroups/{rg}
```

---

## Advanced Topics

### Q16: What are ARM template conditions and when would you use them?

**Answer:**
Conditions allow you to deploy resources conditionally based on parameter values or other conditions.

**Use Cases:**
- Deploy different resources based on environment (dev vs prod)
- Enable/disable features based on flags
- Conditional resource creation based on parameter values

**Example:**
```json
{
  "parameters": {
    "environment": {
      "type": "string",
      "allowedValues": ["dev", "prod"]
    },
    "enableMonitoring": {
      "type": "bool",
      "defaultValue": false
    }
  },
  "resources": [
    {
      "condition": "[equals(parameters('environment'), 'prod')]",
      "type": "Microsoft.Insights/components",
      "name": "[parameters('appInsightsName')]",
      // ... properties
    },
    {
      "condition": "[parameters('enableMonitoring')]",
      "type": "Microsoft.OperationalInsights/workspaces",
      // ... properties
    }
  ]
}
```

**AWS Comparison:**
- Similar to CloudFormation Conditions

---

### Q17: Explain ARM template dependencies and how they're handled.

**Answer:**
ARM automatically determines resource dependencies based on references, but you can explicitly define them using `dependsOn`.

**Automatic Dependencies:**
- Created when you use `reference()` function
- Created when using `resourceId()` function
- Created when referencing resource properties

**Explicit Dependencies:**
- Use `dependsOn` array in resource definition
- Useful when implicit dependency isn't detected
- Can specify multiple dependencies

**Example:**
```json
{
  "resources": [
    {
      "type": "Microsoft.Storage/storageAccounts",
      "name": "[parameters('storageAccountName')]",
      // ... properties
    },
    {
      "type": "Microsoft.Compute/virtualMachines",
      "name": "[parameters('vmName')]",
      "dependsOn": [
        "[resourceId('Microsoft.Storage/storageAccounts', parameters('storageAccountName'))]",
        "[resourceId('Microsoft.Network/networkInterfaces', parameters('nicName'))]"
      ],
      // ... properties
    }
  ]
}
```

**AWS Comparison:**
- Similar to CloudFormation DependsOn attribute
- CloudFormation also infers dependencies from Ref/GetAtt

---

### Q18: What are deployment scripts and when would you use them?

**Answer:**
Deployment scripts allow you to run scripts (PowerShell or Bash) as part of ARM template deployment.

**Use Cases:**
- Custom configuration not available in ARM
- Post-deployment setup tasks
- Running commands that require runtime execution
- Complex logic that's difficult to express in ARM

**Example:**
```json
{
  "type": "Microsoft.Resources/deploymentScripts",
  "apiVersion": "2020-10-01",
  "name": "configureApp",
  "kind": "AzurePowerShell",
  "properties": {
    "azPowerShellVersion": "6.2",
    "scriptContent": "Write-Host 'Configuring application...'",
    "timeout": "PT30M",
    "cleanupPreference": "Always"
  }
}
```

**AWS Comparison:**
- Similar to CloudFormation Custom Resources with Lambda
- Or using AWS Systems Manager during instance launch

---

## Scenario-Based Questions

### Q19: How would you migrate an AWS CloudFormation stack to Azure ARM?

**Answer:**
Migration steps:

1. **Analyze AWS Resources**
   - List all resources in the CloudFormation stack
   - Document dependencies and relationships
   - Identify equivalent Azure services

2. **Map AWS to Azure Services**
   - Use the correlation guide to map services
   - Note differences in configuration
   - Identify Azure-specific requirements

3. **Create Resource Group**
   ```bash
   az group create --name myResourceGroup --location eastus
   ```

4. **Convert Template**
   - Start with core resources (VNet, Storage, Compute)
   - Convert CloudFormation functions to ARM functions
   - Adjust parameter definitions
   - Update resource properties to Azure format

5. **Test Incrementally**
   - Deploy and test each component
   - Validate networking connectivity
   - Test application functionality

6. **Handle Differences**
   - Resource groups (mandatory in Azure)
   - Naming conventions
   - Region/availability zone concepts
   - IAM vs RBAC

**Example Migration:**
```yaml
# AWS CloudFormation
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: my-bucket-name
```

```json
// Azure ARM
{
  "resources": [
    {
      "type": "Microsoft.Storage/storageAccounts",
      "apiVersion": "2021-04-01",
      "name": "[concat('storage', uniqueString(resourceGroup().id))]",
      "kind": "StorageV2",
      "sku": { "name": "Standard_LRS" }
    }
  ]
}
```

---

### Q20: Design a multi-tier application using ARM templates (Web, App, Database tiers).

**Answer:**
Design considerations:

**Architecture:**
1. **Resource Group**: Single resource group for the application
2. **Networking**: VNet with subnets for each tier
3. **Web Tier**: App Service or VM Scale Set with Load Balancer
4. **App Tier**: VM Scale Set or App Service (internal)
5. **Database Tier**: Azure SQL Database or Cosmos DB
6. **Security**: NSGs, Key Vault for secrets

**Template Structure:**
```json
{
  "parameters": {
    "environment": { "type": "string" },
    "appName": { "type": "string" }
  },
  "variables": {
    "vnetName": "[concat(parameters('appName'), '-vnet')]",
    "webSubnetName": "web-subnet",
    "appSubnetName": "app-subnet",
    "dbSubnetName": "db-subnet"
  },
  "resources": [
    {
      // VNet with subnets
      "type": "Microsoft.Network/virtualNetworks",
      "name": "[variables('vnetName')]",
      "properties": {
        "addressSpace": { "addressPrefixes": ["10.0.0.0/16"] },
        "subnets": [
          { "name": "[variables('webSubnetName')]", "addressPrefix": "10.0.1.0/24" },
          { "name": "[variables('appSubnetName')]", "addressPrefix": "10.0.2.0/24" },
          { "name": "[variables('dbSubnetName')]", "addressPrefix": "10.0.3.0/24" }
        ]
      }
    },
    {
      // NSG for web tier
      "type": "Microsoft.Network/networkSecurityGroups",
      "name": "[concat(parameters('appName'), '-web-nsg')]",
      "properties": {
        "securityRules": [
          {
            "name": "AllowHTTP",
            "properties": {
              "priority": 1000,
              "access": "Allow",
              "direction": "Inbound",
              "destinationPortRange": "80",
              "protocol": "Tcp",
              "sourceAddressPrefix": "*"
            }
          }
        ]
      }
    },
    {
      // App Service Plan
      "type": "Microsoft.Web/serverfarms",
      "name": "[concat(parameters('appName'), '-plan')]",
      "sku": { "name": "B1", "tier": "Basic" }
    },
    {
      // Web App
      "type": "Microsoft.Web/sites",
      "name": "[concat(parameters('appName'), '-web')]",
      "dependsOn": [
        "[resourceId('Microsoft.Web/serverfarms', concat(parameters('appName'), '-plan'))]"
      ]
    },
    {
      // SQL Database
      "type": "Microsoft.Sql/servers",
      "name": "[concat(parameters('appName'), '-sql')]",
      "properties": {
        "administratorLogin": "[parameters('sqlAdminLogin')]",
        "administratorLoginPassword": "[parameters('sqlAdminPassword')]"
      }
    },
    {
      "type": "Microsoft.Sql/servers/databases",
      "name": "[concat(parameters('appName'), '-sql/', parameters('appName'), '-db')]",
      "dependsOn": [
        "[resourceId('Microsoft.Sql/servers', concat(parameters('appName'), '-sql'))]"
      ],
      "properties": {
        "edition": "Basic",
        "requestedServiceObjectiveName": "Basic"
      }
    }
  ]
}
```

**Key Design Decisions:**
- Use App Service for PaaS (easier management) or VMs for IaaS (more control)
- Separate subnets with NSGs for network segmentation
- Use Key Vault for database credentials
- Consider using Private Endpoints for database access
- Implement health probes and auto-scaling

---

## Quick Reference: Common Interview Topics

### Must-Know Concepts
- ✅ Resource Groups (mandatory in Azure)
- ✅ ARM Template structure (parameters, variables, resources, outputs)
- ✅ Bicep vs ARM JSON
- ✅ Deployment modes (Incremental vs Complete)
- ✅ Template validation and what-if operations
- ✅ Managed Identities vs Service Principals
- ✅ RBAC roles and scopes
- ✅ VNet vs AWS VPC differences
- ✅ NSG rules and priorities
- ✅ ARM template functions (reference, concat, uniqueString, etc.)

### Common Mistakes to Avoid
- ❌ Forgetting that resources must be in a resource group
- ❌ Not understanding deployment modes (Complete mode can delete resources!)
- ❌ Hardcoding secrets in templates
- ❌ Not validating templates before deployment
- ❌ Not using what-if to preview changes
- ❌ Confusing Managed Identities with Service Principals
- ❌ Not understanding NSG rule priorities

---

## Practice Exercises

1. **Create an ARM template** that deploys:
   - A VNet with 3 subnets
   - A VM in one subnet
   - A storage account
   - NSG rules to allow SSH/RDP

2. **Convert this CloudFormation to ARM:**
   - EC2 instance with security group
   - S3 bucket
   - RDS database

3. **Design a solution** using ARM templates for:
   - High-availability web application
   - Multi-region deployment
   - Disaster recovery setup

---

Good luck with your interview! 🚀
