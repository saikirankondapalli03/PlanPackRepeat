# Azure ARM Interview Preparation Guide

Welcome! This directory contains comprehensive interview preparation materials for Azure Resource Manager (ARM), specifically designed for AWS cloud developers transitioning to Azure.

## 📚 Study Materials

### 1. [AWS to Azure ARM Correlation Guide](./AWS-to-Azure-ARM-Correlation.md)
**Start here!** This guide maps familiar AWS concepts to their Azure ARM equivalents, making it easier to understand Azure through your AWS knowledge.

**Topics Covered:**
- Infrastructure as Code comparison
- Resource organization (Resource Groups vs AWS organization)
- Compute, Storage, Networking services mapping
- Identity & Access Management (IAM vs RBAC)
- Monitoring, Security, and DevOps tools

### 2. [Interview Questions](./Interview-Questions.md)
Comprehensive list of interview questions with detailed answers, organized by topic areas.

**Sections:**
- Fundamental Concepts
- ARM Templates
- Resource Groups
- Deployment & Operations
- Networking
- Security & Identity
- Advanced Topics
- Scenario-Based Questions

**Total: 20+ questions** covering everything from basics to advanced scenarios.

### 3. [Practical Examples](./Practical-Examples.md)
Side-by-side code comparisons of AWS CloudFormation and Azure ARM templates for common infrastructure patterns.

**Examples Included:**
- Simple Web Server (EC2 vs VM)
- Storage Account (S3 vs Blob Storage)
- Load Balanced Web Application (ELB vs Azure Load Balancer)
- Database with Private Access (RDS vs Azure SQL)

Each example includes:
- AWS CloudFormation (YAML)
- Azure ARM Template (JSON)
- Azure ARM Template (Bicep)

### 4. [Quick Reference Cheat Sheet](./Quick-Reference-Cheat-Sheet.md)
A handy reference guide for quick lookups during interview preparation.

**Includes:**
- Essential Azure CLI and PowerShell commands
- Common ARM template functions
- Service mapping table
- Naming conventions
- RBAC roles
- Common patterns and troubleshooting tips

## 🎯 Study Path Recommendation

### Week 1: Foundation
1. Read the [AWS to Azure ARM Correlation Guide](./AWS-to-Azure-ARM-Correlation.md)
2. Focus on understanding Resource Groups (fundamental difference from AWS)
3. Learn ARM template structure (parameters, variables, resources, outputs)
4. Practice with [Practical Examples](./Practical-Examples.md) - Example 1

### Week 2: Core Concepts
1. Study [Interview Questions](./Interview-Questions.md) - Fundamental Concepts section
2. Understand deployment modes (Incremental vs Complete)
3. Learn ARM template functions
4. Practice with [Practical Examples](./Practical-Examples.md) - Examples 2 & 3

### Week 3: Advanced Topics
1. Study [Interview Questions](./Interview-Questions.md) - Advanced Topics section
2. Learn about Bicep (Azure's DSL)
3. Understand Managed Identities vs Service Principals
4. Study networking concepts (VNet, NSG, Load Balancer)
5. Practice with [Practical Examples](./Practical-Examples.md) - Example 4

### Week 4: Interview Prep
1. Review all [Interview Questions](./Interview-Questions.md)
2. Practice scenario-based questions
3. Use [Quick Reference Cheat Sheet](./Quick-Reference-Cheat-Sheet.md) for quick reviews
4. Practice converting AWS CloudFormation to ARM templates
5. Mock interviews focusing on:
   - Explaining differences between AWS and Azure
   - Designing solutions using ARM templates
   - Troubleshooting deployment issues

## 🔑 Key Concepts to Master

### Must-Know Topics
- ✅ **Resource Groups**: Mandatory in Azure (unlike AWS tags)
- ✅ **ARM Template Structure**: Parameters, Variables, Resources, Outputs
- ✅ **Bicep**: Modern Azure IaC language (compiles to ARM JSON)
- ✅ **Deployment Modes**: Incremental (default) vs Complete
- ✅ **Template Validation**: `az deployment group validate`
- ✅ **What-If Operations**: Preview changes before deployment
- ✅ **Managed Identities**: Azure's equivalent to IAM roles
- ✅ **RBAC**: Role-Based Access Control (different from AWS IAM)
- ✅ **VNet vs VPC**: Key differences in networking
- ✅ **NSG Rules**: Priority-based firewall rules

### Common Interview Topics
1. **Explain Resource Groups and why they're mandatory**
2. **Compare ARM templates to CloudFormation**
3. **What is Bicep and why use it?**
4. **Deployment modes: Incremental vs Complete**
5. **How do you handle secrets in ARM templates?**
6. **Managed Identities vs Service Principals**
7. **VNet peering and connectivity**
8. **NSG rules and priorities**
9. **Template validation and what-if operations**
10. **Design a multi-tier application using ARM**

## 🛠️ Hands-On Practice

### Setup Azure Environment
```bash
# Install Azure CLI
# Windows: https://aka.ms/installazurecliwindows
# Mac: brew install azure-cli
# Linux: curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login
az login

# Set subscription
az account set --subscription "Your Subscription Name"

# Create a test resource group
az group create --name rg-practice --location eastus
```

### Practice Exercises

1. **Convert AWS to Azure**
   - Take a simple CloudFormation template
   - Convert it to ARM template (JSON)
   - Convert it to Bicep
   - Deploy and test

2. **Create from Scratch**
   - Design a 3-tier application
   - Create ARM template
   - Validate and deploy
   - Test what-if operations

3. **Troubleshooting**
   - Intentionally create errors in templates
   - Practice reading error messages
   - Fix and redeploy

## 📝 Interview Tips

### Before the Interview
- Review the [Quick Reference Cheat Sheet](./Quick-Reference-Cheat-Sheet.md)
- Practice explaining concepts out loud
- Prepare examples from your AWS experience
- Review common ARM template functions

### During the Interview
- **Start with AWS equivalent**: "In AWS, we have X, and in Azure it's similar to Y, but..."
- **Emphasize differences**: Resource Groups, deployment modes, Bicep
- **Show understanding**: Explain why Azure does things differently
- **Use examples**: Reference the practical examples you've studied
- **Ask clarifying questions**: Show you think about requirements

### Common Questions Format
1. **Conceptual**: "What is X and how does it work?"
2. **Comparison**: "How does X compare to AWS Y?"
3. **Practical**: "Design a solution for..."
4. **Troubleshooting**: "What would you do if..."
5. **Best Practices**: "How would you handle..."

## 🚨 Common Mistakes to Avoid

1. ❌ Forgetting that resources MUST be in a resource group
2. ❌ Not understanding Complete mode can delete resources
3. ❌ Hardcoding secrets in templates
4. ❌ Not validating templates before deployment
5. ❌ Confusing Managed Identities with Service Principals
6. ❌ Not understanding NSG rule priorities
7. ❌ Assuming Azure works exactly like AWS
8. ❌ Not knowing when to use Bicep vs ARM JSON

## 📖 Additional Resources

### Official Documentation
- [Azure ARM Templates](https://docs.microsoft.com/azure/azure-resource-manager/templates/)
- [Bicep Documentation](https://docs.microsoft.com/azure/azure-resource-manager/bicep/)
- [Azure Quickstart Templates](https://azure.microsoft.com/resources/templates/)
- [ARM Template Functions](https://docs.microsoft.com/azure/azure-resource-manager/templates/template-functions)

### Learning Paths
- [Azure Fundamentals](https://docs.microsoft.com/learn/paths/azure-fundamentals/)
- [ARM Templates](https://docs.microsoft.com/learn/paths/deploy-manage-resource-manager-templates/)
- [Azure Networking](https://docs.microsoft.com/learn/paths/azure-networking-fundamentals/)

### Practice
- [Azure Sandbox](https://docs.microsoft.com/learn/modules/create-azure-resources/)
- [Azure Portal](https://portal.azure.com) - Create resources manually, then convert to ARM
- [Bicep Playground](https://bicepdemo.z22.web.core.windows.net/)

## 🎓 Certification Path

If you're pursuing Azure certifications:
- **AZ-900**: Azure Fundamentals (good starting point)
- **AZ-104**: Azure Administrator (covers ARM templates)
- **AZ-305**: Azure Solutions Architect (advanced ARM/Bicep)

## 💡 Pro Tips

1. **Use Bicep for new projects** - It's cleaner and easier to read
2. **Always validate before deploying** - Saves time and prevents errors
3. **Use what-if operations** - Preview changes in production
4. **Leverage your AWS knowledge** - Many concepts translate well
5. **Focus on differences** - Interviewers want to know you understand Azure specifics
6. **Practice explaining concepts** - Can you explain Resource Groups to someone?

## 🤝 Contributing

Found an error or want to add more content? Feel free to:
- Add more examples
- Expand on interview questions
- Add more AWS to Azure mappings
- Include more troubleshooting scenarios

## 📅 Study Schedule Template

```
Week 1: Foundation
├── Day 1-2: Read Correlation Guide
├── Day 3-4: Study ARM Template Structure
└── Day 5-7: Practice Example 1

Week 2: Core Concepts
├── Day 1-2: Interview Questions (Fundamentals)
├── Day 3-4: Deployment Modes & Functions
└── Day 5-7: Practice Examples 2 & 3

Week 3: Advanced
├── Day 1-2: Bicep & Advanced Topics
├── Day 3-4: Networking & Security
└── Day 5-7: Practice Example 4 & Scenarios

Week 4: Interview Prep
├── Day 1-2: Review All Questions
├── Day 3-4: Mock Interviews
└── Day 5-7: Final Review & Practice
```

---

**Good luck with your Azure ARM interview! 🚀**

Remember: Your AWS experience is valuable - use it to understand Azure concepts while highlighting your ability to learn and adapt to Azure's specific approaches.
