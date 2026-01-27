# Azure ARM Quick Reference Cheat Sheet

## Essential Commands

### Azure CLI
```bash
# Login
az login

# Set subscription
az account set --subscription "Subscription Name"

# Create resource group
az group create --name myRG --location eastus

# Deploy ARM template
az deployment group create \
  --resource-group myRG \
  --template-file template.json \
  --parameters @parameters.json

# Validate template
az deployment group validate \
  --resource-group myRG \
  --template-file template.json

# Preview changes (what-if)
az deployment group what-if \
  --resource-group myRG \
  --template-file template.json

# List deployments
az deployment group list --resource-group myRG

# Delete resource group
az group delete --name myRG --yes
```

### PowerShell
```powershell
# Login
Connect-AzAccount

# Set subscription
Set-AzContext -Subscription "Subscription Name"

# Create resource group
New-AzResourceGroup -Name myRG -Location eastus

# Deploy ARM template
New-AzResourceGroupDeployment \
  -ResourceGroupName myRG \
  -TemplateFile template.json \
  -TemplateParameterFile parameters.json

# Validate template
Test-AzResourceGroupDeployment \
  -ResourceGroupName myRG \
  -TemplateFile template.json

# Preview changes
Get-AzResourceGroupDeploymentWhatIfResult \
  -ResourceGroupName myRG \
  -TemplateFile template.json
```

---

## ARM Template Structure

```json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": { },
  "variables": { },
  "functions": { },
  "resources": [ ],
  "outputs": { }
}
```

---

## Common ARM Template Functions

| Function | Example | Purpose |
|----------|---------|---------|
| `concat()` | `[concat('str1', 'str2')]` | Concatenate strings |
| `reference()` | `[reference(resourceId(...))]` | Reference resource |
| `resourceId()` | `[resourceId('Microsoft.Storage/storageAccounts', 'name')]` | Get resource ID |
| `uniqueString()` | `[uniqueString(resourceGroup().id)]` | Generate unique string |
| `resourceGroup()` | `[resourceGroup().location]` | Get resource group info |
| `subscription()` | `[subscription().subscriptionId]` | Get subscription info |
| `if()` | `[if(condition, trueValue, falseValue)]` | Conditional |
| `equals()` | `[equals(param1, param2)]` | Compare values |
| `toLower()` | `[toLower('STRING')]` | Convert to lowercase |
| `toUpper()` | `[toUpper('string')]` | Convert to uppercase |
| `length()` | `[length('string')]` | Get string/array length |
| `split()` | `[split('a,b,c', ',')]` | Split string |
| `substring()` | `[substring('hello', 0, 2)]` | Get substring |
| `base64()` | `[base64('string')]` | Base64 encode |
| `uri()` | `[uri('https://example.com', 'path')]` | Create URI |
| `deployment()` | `[deployment().name]` | Get deployment info |
| `variables()` | `[variables('varName')]` | Reference variable |
| `parameters()` | `[parameters('paramName')]` | Reference parameter |

---

## AWS to Azure Service Mapping

| AWS Service | Azure Service | Notes |
|-------------|---------------|-------|
| EC2 | Virtual Machine (VM) | Compute instance |
| Auto Scaling Group | VM Scale Set (VMSS) | Auto-scaling |
| ELB | Load Balancer / Application Gateway | Traffic distribution |
| Lambda | Azure Functions | Serverless |
| ECS/EKS | Azure Container Instances / AKS | Containers |
| S3 | Blob Storage | Object storage |
| EBS | Managed Disks | Block storage |
| EFS | Azure Files | File shares |
| RDS | Azure SQL Database | Managed SQL |
| DynamoDB | Cosmos DB | NoSQL |
| ElastiCache | Azure Cache for Redis | Caching |
| VPC | Virtual Network (VNet) | Network isolation |
| Security Group | Network Security Group (NSG) | Firewall |
| IAM Role | Managed Identity | Service identity |
| CloudFormation | ARM Templates / Bicep | IaC |
| CloudWatch | Azure Monitor | Monitoring |
| Secrets Manager | Key Vault | Secrets |
| KMS | Key Vault | Key management |

---

## Resource Naming Conventions

### Azure Naming Rules
- **Lowercase only** (recommended)
- **Alphanumeric and hyphens** (`-`)
- **No underscores** (not recommended)
- **Length limits** vary by resource type
- **Globally unique** for some resources (storage accounts, key vaults)

### Common Patterns
```
Resource Group: rg-{environment}-{region}
Storage Account: st{appname}{env}{unique}
Virtual Machine: vm-{appname}-{role}-{number}
Virtual Network: vnet-{appname}-{region}
Subnet: snet-{appname}-{purpose}
Network Security Group: nsg-{appname}-{purpose}
Load Balancer: lb-{appname}-{purpose}
```

---

## Common Resource Types

### Compute
```
Microsoft.Compute/virtualMachines
Microsoft.Compute/virtualMachineScaleSets
Microsoft.ContainerService/managedClusters (AKS)
Microsoft.Web/serverfarms (App Service Plan)
Microsoft.Web/sites (App Service)
```

### Storage
```
Microsoft.Storage/storageAccounts
Microsoft.Storage/storageAccounts/blobServices
Microsoft.Storage/storageAccounts/blobServices/containers
```

### Networking
```
Microsoft.Network/virtualNetworks
Microsoft.Network/virtualNetworks/subnets
Microsoft.Network/networkSecurityGroups
Microsoft.Network/loadBalancers
Microsoft.Network/publicIPAddresses
Microsoft.Network/networkInterfaces
```

### Database
```
Microsoft.Sql/servers
Microsoft.Sql/servers/databases
Microsoft.DocumentDB/databaseAccounts (Cosmos DB)
```

### Security
```
Microsoft.KeyVault/vaults
Microsoft.KeyVault/vaults/secrets
Microsoft.ManagedIdentity/userAssignedIdentities
```

---

## Parameter Types

```json
{
  "parameters": {
    "stringParam": {
      "type": "string",
      "defaultValue": "default",
      "allowedValues": ["value1", "value2"],
      "minLength": 1,
      "maxLength": 50
    },
    "intParam": {
      "type": "int",
      "defaultValue": 1,
      "minValue": 1,
      "maxValue": 100
    },
    "boolParam": {
      "type": "bool",
      "defaultValue": false
    },
    "arrayParam": {
      "type": "array",
      "defaultValue": ["item1", "item2"]
    },
    "objectParam": {
      "type": "object",
      "defaultValue": {
        "key": "value"
      }
    },
    "secureStringParam": {
      "type": "secureString",
      "metadata": {
        "description": "Sensitive value"
      }
    }
  }
}
```

---

## Deployment Modes

### Incremental (Default)
- Only adds/updates resources in template
- Resources not in template remain unchanged
- **Use for**: Most deployments

```bash
az deployment group create \
  --resource-group myRG \
  --template-file template.json \
  --mode Incremental
```

### Complete
- Deletes resources not in template
- **Warning**: Can delete resources!
- **Use for**: Clean deployments

```bash
az deployment group create \
  --resource-group myRG \
  --template-file template.json \
  --mode Complete
```

---

## Bicep Quick Reference

### Basic Syntax
```bicep
// Parameters
@description('VM size')
param vmSize string = 'Standard_B1s'

@secure()
param adminPassword string

// Variables
var vmName = 'myVM'
var storageAccountName = 'st${uniqueString(resourceGroup().id)}'

// Resources
resource vm 'Microsoft.Compute/virtualMachines@2021-11-01' = {
  name: vmName
  location: resourceGroup().location
  properties: {
    // ...
  }
}

// Outputs
output vmName string = vm.name
```

### Decorators
```bicep
@description('Parameter description')
@secure()  // For sensitive parameters
@allowed(['value1', 'value2'])
@minLength(1)
@maxLength(50)
```

### Loops
```bicep
param vmNames array = ['vm1', 'vm2', 'vm3']

resource vms 'Microsoft.Compute/virtualMachines@2021-11-01' = [for name in vmNames: {
  name: name
  location: resourceGroup().location
  // ...
}]
```

### Conditions
```bicep
param enableMonitoring bool = false

resource appInsights 'Microsoft.Insights/components@2020-02-02' = if (enableMonitoring) {
  name: 'myAppInsights'
  // ...
}
```

---

## RBAC Roles

### Common Built-in Roles
- **Owner**: Full access including access management
- **Contributor**: Full access except access management
- **Reader**: Read-only access
- **User Access Administrator**: Manage user access
- **Network Contributor**: Manage networks
- **Storage Account Contributor**: Manage storage accounts
- **Virtual Machine Contributor**: Manage VMs

### Assign Role
```bash
az role assignment create \
  --assignee user@example.com \
  --role Contributor \
  --scope /subscriptions/{sub-id}/resourceGroups/{rg}
```

---

## Managed Identities

### System-assigned
```json
{
  "type": "Microsoft.Compute/virtualMachines",
  "identity": {
    "type": "SystemAssigned"
  }
}
```

### User-assigned
```json
{
  "type": "Microsoft.Compute/virtualMachines",
  "identity": {
    "type": "UserAssigned",
    "userAssignedIdentities": {
      "/subscriptions/{sub-id}/resourceGroups/{rg}/providers/Microsoft.ManagedIdentity/userAssignedIdentities/{identity-name}": {}
    }
  }
}
```

---

## Network Security Group Rules

### Rule Structure
```json
{
  "name": "RuleName",
  "properties": {
    "priority": 1000,           // 100-4096, lower = higher priority
    "access": "Allow",          // Allow or Deny
    "direction": "Inbound",     // Inbound or Outbound
    "destinationPortRange": "80",
    "protocol": "Tcp",          // Tcp, Udp, Icmp, or *
    "sourceAddressPrefix": "*", // IP, CIDR, service tag, or *
    "destinationAddressPrefix": "*"
  }
}
```

### Common Ports
- **22**: SSH
- **3389**: RDP
- **80**: HTTP
- **443**: HTTPS
- **3306**: MySQL
- **1433**: SQL Server
- **5432**: PostgreSQL

---

## Key Vault Secrets

### Reference Secret in Template
```json
{
  "properties": {
    "adminPassword": {
      "reference": {
        "keyVault": {
          "id": "/subscriptions/{sub-id}/resourceGroups/{rg}/providers/Microsoft.KeyVault/vaults/{vault-name}"
        },
        "secretName": "adminPassword"
      }
    }
  }
}
```

### Store Secret
```bash
az keyvault secret set \
  --vault-name myVault \
  --name adminPassword \
  --value "MyPassword123!"
```

---

## Common Patterns

### Unique Storage Account Name
```json
{
  "variables": {
    "storageAccountName": "[concat('storage', uniqueString(resourceGroup().id))]"
  }
}
```

### Conditional Resource
```json
{
  "condition": "[equals(parameters('environment'), 'prod')]",
  "type": "Microsoft.Insights/components",
  // ...
}
```

### Resource Dependency
```json
{
  "dependsOn": [
    "[resourceId('Microsoft.Storage/storageAccounts', variables('storageAccountName'))]"
  ]
}
```

### Reference Resource Property
```json
{
  "properties": {
    "storageAccountId": "[reference(resourceId('Microsoft.Storage/storageAccounts', variables('storageAccountName'))).id]"
  }
}
```

---

## Troubleshooting

### Common Issues

1. **Deployment Failed**
   ```bash
   # Check deployment details
   az deployment group show \
     --resource-group myRG \
     --name deployment-name
   ```

2. **Template Validation Error**
   ```bash
   # Validate before deployment
   az deployment group validate \
     --resource-group myRG \
     --template-file template.json
   ```

3. **Resource Already Exists**
   - Check if resource exists in another resource group
   - Some resources require globally unique names

4. **Permission Denied**
   - Check RBAC assignments
   - Verify subscription permissions
   - Check resource group permissions

5. **Invalid Template Function**
   - Verify function syntax
   - Check API version compatibility
   - Ensure proper bracket notation `[function()]`

---

## Best Practices Checklist

- [ ] Use resource groups for logical grouping
- [ ] Never hardcode secrets in templates
- [ ] Use Key Vault for sensitive data
- [ ] Validate templates before deployment
- [ ] Use what-if to preview changes
- [ ] Use Bicep for new templates (cleaner syntax)
- [ ] Use managed identities instead of service principals
- [ ] Follow naming conventions
- [ ] Use tags for additional metadata
- [ ] Test in non-production first
- [ ] Use linked templates for complex deployments
- [ ] Document parameters and variables
- [ ] Use version control for templates
- [ ] Enable soft delete for critical resources
- [ ] Use resource locks for production resources

---

## Quick Comparison: AWS vs Azure

| Concept | AWS | Azure |
|---------|-----|-------|
| **IaC Tool** | CloudFormation | ARM Templates / Bicep |
| **Resource Grouping** | Tags | Resource Groups (mandatory) |
| **Compute** | EC2 | Virtual Machines |
| **Serverless** | Lambda | Azure Functions |
| **Object Storage** | S3 | Blob Storage |
| **Block Storage** | EBS | Managed Disks |
| **Network** | VPC | Virtual Network |
| **Firewall** | Security Group | NSG |
| **Service Identity** | IAM Role | Managed Identity |
| **Secrets** | Secrets Manager | Key Vault |
| **Monitoring** | CloudWatch | Azure Monitor |
| **Deployment** | Stack | Resource Group Deployment |

---

## Interview Quick Tips

1. **Resource Groups are mandatory** - Every resource must be in one
2. **Bicep is preferred** over ARM JSON for new templates
3. **Incremental mode is default** - Complete mode can delete resources!
4. **Always validate** templates before deployment
5. **Use what-if** to preview changes
6. **Managed Identities** are like IAM roles - no credentials needed
7. **NSG rules have priorities** - Lower number = higher priority
8. **VNets can have multiple CIDR blocks** (unlike VPC)
9. **Private Endpoints** for private PaaS access (like VPC endpoints)
10. **ARM functions use bracket notation** `[function()]`

---

## Useful Links

- [ARM Template Reference](https://docs.microsoft.com/azure/azure-resource-manager/templates/)
- [Bicep Documentation](https://docs.microsoft.com/azure/azure-resource-manager/bicep/)
- [Quickstart Templates](https://azure.microsoft.com/resources/templates/)
- [Template Functions](https://docs.microsoft.com/azure/azure-resource-manager/templates/template-functions)
- [Azure CLI Reference](https://docs.microsoft.com/cli/azure/)
- [Azure PowerShell](https://docs.microsoft.com/powershell/azure/)

---

**Remember**: When in doubt, use `az deployment group validate` and `az deployment group what-if` before deploying!
