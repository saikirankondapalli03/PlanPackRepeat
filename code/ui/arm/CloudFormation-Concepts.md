# AWS CloudFormation Template Concepts

## Overview
This guide covers fundamental AWS CloudFormation concepts and template structure. Use this as a foundation before learning Azure ARM templates to understand the correlation between the two Infrastructure as Code (IaC) approaches.

---

## What is CloudFormation?

**AWS CloudFormation** is a service that helps you model and set up AWS resources so you can spend less time managing those resources and more time focusing on your applications. You create a template that describes all the AWS resources you want, and CloudFormation takes care of provisioning and configuring those resources for you.

### Key Benefits
- **Infrastructure as Code**: Define infrastructure declaratively
- **Version Control**: Templates can be stored in Git
- **Repeatability**: Deploy the same infrastructure multiple times
- **Dependency Management**: CloudFormation handles resource dependencies automatically
- **Rollback**: Automatic rollback on failure
- **Cost Tracking**: Resources are tagged with stack information

---

## CloudFormation Template Structure

A CloudFormation template is a JSON or YAML formatted text file that describes your AWS infrastructure. The template consists of several sections:

### Basic Template Structure

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Optional description of the template'

Parameters:
  # Input values for the template

Mappings:
  # Static lookup tables

Conditions:
  # Conditional resource creation

Transform:
  # Transform functions (e.g., AWS::Serverless)

Resources:
  # Required: The AWS resources to create

Outputs:
  # Values returned after stack creation
```

### Template Sections Explained

#### 1. AWSTemplateFormatVersion (Optional)
Specifies the CloudFormation template format version.

```yaml
AWSTemplateFormatVersion: '2010-09-09'
```

#### 2. Description (Optional)
A text string that describes the template.

```yaml
Description: 'Creates a VPC with public and private subnets'
```

#### 3. Parameters (Optional)
Input values that are passed to your template when you create or update a stack.

```yaml
Parameters:
  InstanceType:
    Type: String
    Default: t2.micro
    AllowedValues:
      - t2.micro
      - t2.small
      - t2.medium
    Description: 'EC2 instance type'
  
  Environment:
    Type: String
    Default: dev
    Description: 'Environment name (dev, staging, prod)'
  
  AdminEmail:
    Type: String
    Description: 'Email address for notifications'
    NoEcho: true  # Hides value in console/CLI
```

**Common Parameter Types:**
- `String` - Text value
- `Number` - Numeric value
- `List<Number>` - Array of numbers
- `CommaDelimitedList` - Comma-separated list
- `AWS::EC2::AvailabilityZone::Name` - Availability zone
- `AWS::EC2::Image::Id` - AMI ID
- `AWS::EC2::KeyPair::KeyName` - Key pair name
- `AWS::EC2::SecurityGroup::Id` - Security group ID
- `AWS::EC2::Subnet::Id` - Subnet ID
- `AWS::EC2::VPC::Id` - VPC ID

#### 4. Mappings (Optional)
A mapping of keys to a corresponding set of named values. Useful for selecting values based on input parameters.

```yaml
Mappings:
  RegionMap:
    us-east-1:
      AMI: ami-0abcdef1234567890
      InstanceType: t2.micro
    us-west-2:
      AMI: ami-0fedcba9876543210
      InstanceType: t2.small
  
  EnvironmentMap:
    dev:
      InstanceType: t2.micro
      MinSize: 1
      MaxSize: 2
    prod:
      InstanceType: t2.large
      MinSize: 2
      MaxSize: 10
```

**Usage:**
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: !FindInMap [RegionMap, !Ref 'AWS::Region', AMI]
      InstanceType: !FindInMap [EnvironmentMap, !Ref Environment, InstanceType]
```

#### 5. Conditions (Optional)
Conditions that control whether certain resources are created or whether certain resource properties are assigned a value.

```yaml
Conditions:
  CreateProdResources: !Equals [!Ref Environment, prod]
  UseLargeInstance: !Or
    - !Equals [!Ref Environment, prod]
    - !Equals [!Ref InstanceType, t2.large]

Resources:
  ProductionDatabase:
    Type: AWS::RDS::DBInstance
    Condition: CreateProdResources
    Properties:
      # Database properties
```

**Common Condition Functions:**
- `!Equals` - Checks if two values are equal
- `!And` - Returns true if all conditions are true
- `!Or` - Returns true if any condition is true
- `!Not` - Negates a condition
- `!If` - Returns one value if condition is true, another if false

#### 6. Transform (Optional)
Specifies one or more macros that CloudFormation uses to process your template.

```yaml
Transform: AWS::Serverless-2016-10-31
```

Common transforms:
- `AWS::Serverless-2016-10-31` - SAM (Serverless Application Model)
- `AWS::Include` - Include reusable template snippets
- `AWS::LanguageExtensions` - Additional functions

#### 7. Resources (Required)
The only required section. Defines the AWS resources you want to create.

```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: my-unique-bucket-name
      VersioningConfiguration:
        Status: Enabled
      Tags:
        - Key: Environment
          Value: !Ref Environment
  
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: ami-0abcdef1234567890
      InstanceType: !Ref InstanceType
      KeyName: my-key-pair
      SecurityGroupIds:
        - !Ref MySecurityGroup
      SubnetId: !Ref MySubnet
      Tags:
        - Key: Name
          Value: MyEC2Instance
```

**Resource Properties:**
- `Type` - Required. The resource type (e.g., `AWS::EC2::Instance`)
- `Properties` - Optional. Resource-specific configuration
- `DependsOn` - Optional. Explicit dependency on another resource
- `CreationPolicy` - Optional. Controls resource creation behavior
- `UpdatePolicy` - Optional. Controls resource update behavior
- `DeletionPolicy` - Optional. Controls resource deletion behavior
- `Metadata` - Optional. Additional metadata about the resource

**DeletionPolicy Examples:**
```yaml
Resources:
  MyDatabase:
    Type: AWS::RDS::DBInstance
    DeletionPolicy: Retain  # Options: Delete, Retain, Snapshot
    Properties:
      # Database properties
```

#### 8. Outputs (Optional)
Describes the values that are returned whenever you view your stack's properties.

```yaml
Outputs:
  InstanceId:
    Description: 'EC2 Instance ID'
    Value: !Ref MyInstance
    Export:
      Name: !Sub '${AWS::StackName}-InstanceId'
  
  InstancePublicIP:
    Description: 'Public IP address of the instance'
    Value: !GetAtt MyInstance.PublicIp
  
  BucketName:
    Description: 'S3 Bucket Name'
    Value: !Ref MyBucket
    Export:
      Name: !Sub '${AWS::StackName}-BucketName'
```

**Output Properties:**
- `Description` - Optional. Description of the output
- `Value` - Required. The value to return
- `Export` - Optional. Export name for cross-stack references
- `Condition` - Optional. Condition that must be true for output to be created

---

## Intrinsic Functions

CloudFormation provides built-in functions (intrinsic functions) that help you manage your stacks.

### Reference Functions

#### !Ref
Returns the value of the specified parameter or resource.

```yaml
Parameters:
  InstanceType:
    Type: String

Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      InstanceType: !Ref InstanceType  # Returns parameter value
      SecurityGroupIds:
        - !Ref MySecurityGroup  # Returns resource ID
```

**What !Ref returns depends on the resource type:**
- `AWS::EC2::Instance` → Instance ID
- `AWS::S3::Bucket` → Bucket name
- `AWS::IAM::Role` → Role ARN
- `AWS::EC2::VPC` → VPC ID
- Parameter → Parameter value

#### !GetAtt
Returns the value of an attribute from a resource.

```yaml
Outputs:
  InstancePublicIP:
    Value: !GetAtt MyInstance.PublicIp
  
  BucketArn:
    Value: !GetAtt MyBucket.Arn
  
  LoadBalancerDNS:
    Value: !GetAtt MyLoadBalancer.DNSName
```

### String Functions

#### !Sub
Substitutes variables in a string with values that you specify.

```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub '${Environment}-bucket-${AWS::AccountId}'
  
  MyTopic:
    Type: AWS::SNS::Topic
    Properties:
      DisplayName: !Sub 'Topic for ${AWS::StackName}'
```

**Short form:**
```yaml
BucketName: !Sub '${Environment}-bucket-${AWS::AccountId}'
```

**Long form (with mapping):**
```yaml
BucketName: !Sub
  - '${Prefix}-bucket-${Suffix}'
  - Prefix: !Ref Environment
    Suffix: !Ref AWS::AccountId
```

#### !Join
Joins an array of values with a delimiter.

```yaml
Resources:
  MySecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: !Join
            - '.'
            - - '10'
              - '0'
              - '0'
              - '0/16'
```

**Equivalent using !Sub:**
```yaml
CidrIp: !Sub '10.0.0.0/16'
```

#### !Split
Splits a string into a list of string values.

```yaml
Parameters:
  SubnetIds:
    Type: CommaDelimitedList
    Default: 'subnet-123,subnet-456,subnet-789'

Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      SubnetId: !Select [0, !Ref SubnetIds]  # Gets first subnet
```

### Array Functions

#### !Select
Returns a single object from a list of objects by index.

```yaml
Parameters:
  AvailabilityZones:
    Type: List<AWS::EC2::AvailabilityZone::Name>
    Default:
      - us-east-1a
      - us-east-1b
      - us-east-1c

Resources:
  MySubnet:
    Type: AWS::EC2::Subnet
    Properties:
      AvailabilityZone: !Select [0, !Ref AvailabilityZones]  # First AZ
```

#### !FindInMap
Returns the value corresponding to keys in a two-level map.

```yaml
Mappings:
  RegionMap:
    us-east-1:
      AMI: ami-0abcdef1234567890
      InstanceType: t2.micro
    us-west-2:
      AMI: ami-0fedcba9876543210
      InstanceType: t2.small

Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: !FindInMap [RegionMap, !Ref 'AWS::Region', AMI]
      InstanceType: !FindInMap [RegionMap, !Ref 'AWS::Region', InstanceType]
```

### Conditional Functions

#### !If
Returns one value if the specified condition evaluates to true, or another value if the condition evaluates to false.

```yaml
Conditions:
  CreateProdResources: !Equals [!Ref Environment, prod]

Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      InstanceType: !If
        - CreateProdResources
        - t2.large
        - t2.micro
```

#### !And, !Or, !Not, !Equals
Used in Conditions section.

```yaml
Conditions:
  IsProduction: !Equals [!Ref Environment, prod]
  IsLargeInstance: !Or
    - !Equals [!Ref InstanceType, t2.large]
    - !Equals [!Ref InstanceType, t2.xlarge]
  CreateBackup: !And
    - !Condition IsProduction
    - !Not [!Equals [!Ref BackupEnabled, false]]
```

### Other Functions

#### !Base64
Returns the Base64 representation of the input string.

```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      UserData:
        Fn::Base64: !Sub |
          #!/bin/bash
          yum update -y
          yum install -y httpd
          systemctl start httpd
```

#### !Cidr
Returns an array of CIDR address blocks.

```yaml
Resources:
  MyVPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
  
  PublicSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref MyVPC
      CidrBlock: !Select [0, !Cidr [!GetAtt MyVPC.CidrBlock, 1, 8]]
```

#### !GetAZs
Returns an array that lists Availability Zones for a specified region.

```yaml
Resources:
  MySubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      AvailabilityZone: !Select [0, !GetAZs '']
  
  MySubnet2:
    Type: AWS::EC2::Subnet
    Properties:
      AvailabilityZone: !Select [1, !GetAZs '']
```

#### !ImportValue
Imports the value of an output exported by another stack.

```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      SubnetId: !ImportValue 
        Fn::Sub: '${NetworkStackName}-PublicSubnet1'
```

---

## Pseudo Parameters

CloudFormation provides predefined parameters (pseudo parameters) that you can use at any time in your template.

| Pseudo Parameter | Description | Example |
|------------------|-------------|---------|
| `AWS::AccountId` | AWS account ID | `123456789012` |
| `AWS::Region` | AWS region | `us-east-1` |
| `AWS::StackName` | Name of the stack | `my-stack` |
| `AWS::StackId` | Stack ID | `arn:aws:cloudformation:...` |
| `AWS::URLSuffix` | URL suffix for AWS services | `amazonaws.com` |
| `AWS::Partition` | Partition (aws, aws-cn, aws-us-gov) | `aws` |
| `AWS::NotificationARNs` | SNS topic ARNs for notifications | Array of ARNs |

**Usage:**
```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub '${AWS::StackName}-bucket-${AWS::AccountId}'
      Tags:
        - Key: StackName
          Value: !Ref AWS::StackName
        - Key: Region
          Value: !Ref AWS::Region
```

---

## Complete Example Template

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Complete CloudFormation template example'

Parameters:
  Environment:
    Type: String
    Default: dev
    AllowedValues:
      - dev
      - staging
      - prod
    Description: 'Environment name'
  
  InstanceType:
    Type: String
    Default: t2.micro
    Description: 'EC2 instance type'

Mappings:
  EnvironmentMap:
    dev:
      InstanceType: t2.micro
      MinSize: 1
      MaxSize: 2
    prod:
      InstanceType: t2.large
      MinSize: 2
      MaxSize: 10

Conditions:
  CreateProdResources: !Equals [!Ref Environment, prod]

Resources:
  # VPC
  MyVPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-VPC'
        - Key: Environment
          Value: !Ref Environment
  
  # Internet Gateway
  MyInternetGateway:
    Type: AWS::EC2::InternetGateway
    Properties:
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-IGW'
  
  # Attach Internet Gateway to VPC
  MyInternetGatewayAttachment:
    Type: AWS::EC2::VPCGatewayAttachment
    Properties:
      InternetGatewayId: !Ref MyInternetGateway
      VpcId: !Ref MyVPC
  
  # Public Subnet
  MyPublicSubnet:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref MyVPC
      AvailabilityZone: !Select [0, !GetAZs '']
      CidrBlock: 10.0.1.0/24
      MapPublicIpOnLaunch: true
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-PublicSubnet'
  
  # Route Table
  MyPublicRouteTable:
    Type: AWS::EC2::RouteTable
    Properties:
      VpcId: !Ref MyVPC
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-PublicRouteTable'
  
  # Default Route
  MyDefaultPublicRoute:
    Type: AWS::EC2::Route
    DependsOn: MyInternetGatewayAttachment
    Properties:
      RouteTableId: !Ref MyPublicRouteTable
      DestinationCidrBlock: 0.0.0.0/0
      GatewayId: !Ref MyInternetGateway
  
  # Subnet Route Table Association
  MyPublicSubnetRouteTableAssociation:
    Type: AWS::EC2::SubnetRouteTableAssociation
    Properties:
      SubnetId: !Ref MyPublicSubnet
      RouteTableId: !Ref MyPublicRouteTable
  
  # Security Group
  MySecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupName: !Sub '${AWS::StackName}-SecurityGroup'
      GroupDescription: 'Security group for EC2 instances'
      VpcId: !Ref MyVPC
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 0.0.0.0/0
          Description: 'SSH access'
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0
          Description: 'HTTP access'
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-SecurityGroup'
  
  # EC2 Instance
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: ami-0abcdef1234567890  # Replace with actual AMI ID
      InstanceType: !Ref InstanceType
      SubnetId: !Ref MyPublicSubnet
      SecurityGroupIds:
        - !Ref MySecurityGroup
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-Instance'
        - Key: Environment
          Value: !Ref Environment
  
  # S3 Bucket
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub '${AWS::StackName}-bucket-${AWS::AccountId}'
      VersioningConfiguration:
        Status: Enabled
      PublicAccessBlockConfiguration:
        BlockPublicAcls: true
        BlockPublicPolicy: true
        IgnorePublicAcls: true
        RestrictPublicBuckets: true
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-Bucket'
        - Key: Environment
          Value: !Ref Environment

Outputs:
  VPCId:
    Description: 'VPC ID'
    Value: !Ref MyVPC
    Export:
      Name: !Sub '${AWS::StackName}-VPCId'
  
  PublicSubnetId:
    Description: 'Public Subnet ID'
    Value: !Ref MyPublicSubnet
    Export:
      Name: !Sub '${AWS::StackName}-PublicSubnetId'
  
  InstanceId:
    Description: 'EC2 Instance ID'
    Value: !Ref MyInstance
  
  InstancePublicIP:
    Description: 'Public IP address of the instance'
    Value: !GetAtt MyInstance.PublicIp
  
  BucketName:
    Description: 'S3 Bucket Name'
    Value: !Ref MyBucket
    Export:
      Name: !Sub '${AWS::StackName}-BucketName'
  
  SecurityGroupId:
    Description: 'Security Group ID'
    Value: !Ref MySecurityGroup
    Export:
      Name: !Sub '${AWS::StackName}-SecurityGroupId'
```

---

## Deployment Concepts

### Stack
A collection of AWS resources that you manage as a single unit. All resources in a stack are defined by the stack's CloudFormation template.

### Stack Operations

#### Create Stack
```bash
aws cloudformation create-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod \
               ParameterKey=InstanceType,ParameterValue=t2.large \
  --capabilities CAPABILITY_IAM
```

#### Update Stack
```bash
aws cloudformation update-stack \
  --stack-name my-stack \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod
```

#### Delete Stack
```bash
aws cloudformation delete-stack --stack-name my-stack
```

#### Describe Stack
```bash
aws cloudformation describe-stacks --stack-name my-stack
```

### Stack States
- `CREATE_IN_PROGRESS` - Stack creation in progress
- `CREATE_COMPLETE` - Stack created successfully
- `CREATE_FAILED` - Stack creation failed
- `ROLLBACK_IN_PROGRESS` - Rolling back after failed creation
- `ROLLBACK_COMPLETE` - Rollback completed
- `UPDATE_IN_PROGRESS` - Stack update in progress
- `UPDATE_COMPLETE` - Stack update completed
- `UPDATE_ROLLBACK_IN_PROGRESS` - Rolling back after failed update
- `UPDATE_ROLLBACK_COMPLETE` - Update rollback completed
- `DELETE_IN_PROGRESS` - Stack deletion in progress
- `DELETE_COMPLETE` - Stack deleted

### Change Sets
Preview changes before updating a stack.

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

### Nested Stacks
Break complex templates into smaller, reusable templates.

```yaml
Resources:
  NetworkStack:
    Type: AWS::CloudFormation::Stack
    Properties:
      TemplateURL: https://s3.amazonaws.com/bucket/network-template.yaml
      Parameters:
        Environment: !Ref Environment
```

### Stack Sets
Deploy stacks across multiple accounts and regions.

```bash
aws cloudformation create-stack-set \
  --stack-set-name my-stack-set \
  --template-body file://template.yaml \
  --parameters ParameterKey=Environment,ParameterValue=prod
```

---

## Best Practices

### 1. Template Organization
- Use YAML for better readability (JSON is also supported)
- Separate complex templates into nested stacks
- Use parameter files for different environments
- Store templates in version control (Git)

### 2. Parameters
- Provide meaningful defaults
- Use `AllowedValues` to restrict input
- Use `NoEcho: true` for sensitive parameters
- Use parameter files for consistent deployments

### 3. Resources
- Use `DependsOn` only when necessary (CloudFormation auto-detects most dependencies)
- Use `DeletionPolicy: Retain` for critical resources (databases, etc.)
- Tag all resources for cost tracking and organization
- Use conditions to create environment-specific resources

### 4. Security
- Never hardcode credentials or secrets
- Use AWS Systems Manager Parameter Store or Secrets Manager
- Use IAM roles instead of access keys
- Enable MFA for stack operations in production

### 5. Testing
- Validate templates before deployment
- Use change sets to preview changes
- Test in non-production environments first
- Use stack policies to prevent accidental updates/deletions

### 6. Error Handling
- Monitor stack events during deployment
- Use CloudWatch alarms for stack status
- Implement proper rollback strategies
- Log all stack operations

### 7. Cost Optimization
- Use conditions to create resources only when needed
- Tag resources for cost allocation
- Use stack policies to prevent expensive resource creation
- Review and clean up unused stacks

---

## Common Patterns

### Pattern 1: Environment-Specific Resources
```yaml
Conditions:
  IsProduction: !Equals [!Ref Environment, prod]

Resources:
  ProductionDatabase:
    Type: AWS::RDS::DBInstance
    Condition: IsProduction
    Properties:
      # Production database config
```

### Pattern 2: Cross-Stack References
```yaml
# Stack 1 - Network
Outputs:
  VPCId:
    Value: !Ref MyVPC
    Export:
      Name: NetworkStack-VPCId

# Stack 2 - Application
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      SubnetId: !ImportValue NetworkStack-VPCId
```

### Pattern 3: Dynamic Resource Naming
```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub '${Environment}-${AWS::StackName}-${AWS::AccountId}'
```

### Pattern 4: User Data Scripts
```yaml
Resources:
  MyInstance:
    Type: AWS::EC2::Instance
    Properties:
      UserData:
        Fn::Base64: !Sub |
          #!/bin/bash
          yum update -y
          yum install -y httpd
          echo "<h1>Hello from ${AWS::StackName}</h1>" > /var/www/html/index.html
          systemctl start httpd
          systemctl enable httpd
```

---

## Template Validation

### Validate Template Syntax
```bash
aws cloudformation validate-template \
  --template-body file://template.yaml
```

### Check Template for Best Practices
Use `cfn-lint` (CloudFormation Linter):
```bash
pip install cfn-lint
cfn-lint template.yaml
```

---

## Key Concepts Summary

1. **Template Structure**: Parameters, Mappings, Conditions, Resources, Outputs
2. **Intrinsic Functions**: !Ref, !GetAtt, !Sub, !Join, !Select, !FindInMap, !If
3. **Pseudo Parameters**: AWS::AccountId, AWS::Region, AWS::StackName
4. **Stack Lifecycle**: Create, Update, Delete operations
5. **Dependencies**: Automatic detection and explicit DependsOn
6. **Cross-Stack References**: Exports and Imports
7. **Nested Stacks**: Breaking complex templates into smaller pieces
8. **Change Sets**: Preview changes before applying
9. **Stack Policies**: Protect resources from accidental updates/deletions
10. **Best Practices**: Security, organization, testing, cost optimization

---

## Next Steps

After understanding CloudFormation concepts, refer to the **AWS-to-Azure-ARM-Correlation.md** file to see how these concepts map to Azure ARM templates. This will help you:

1. Understand the correlation between CloudFormation and ARM templates
2. Translate your CloudFormation knowledge to ARM templates
3. Recognize equivalent functions and patterns
4. Understand key differences in deployment models

---

## Additional Resources

- **AWS CloudFormation Documentation**: https://docs.aws.amazon.com/cloudformation/
- **CloudFormation Template Reference**: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-reference.html
- **Intrinsic Functions Reference**: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference.html
- **AWS CloudFormation Sample Templates**: https://github.com/awslabs/aws-cloudformation-templates
- **cfn-lint**: https://github.com/aws-cloudformation/cfn-lint
