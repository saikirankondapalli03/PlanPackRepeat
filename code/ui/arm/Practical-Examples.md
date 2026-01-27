# Practical Examples: AWS CloudFormation

This document provides practical examples of common infrastructure patterns using AWS CloudFormation templates.

---

## Example 1: Simple Web Server

### AWS CloudFormation
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Simple web server on EC2'

Parameters:
  InstanceType:
    Type: String
    Default: t2.micro
    AllowedValues: [t2.micro, t2.small, t3.micro]
  
  KeyPairName:
    Type: AWS::EC2::KeyPair::KeyName
    Description: Name of an existing EC2 KeyPair

Resources:
  WebServerSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Security group for web server
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 0.0.0.0/0

  WebServerInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: ami-0c55b159cbfafe1f0  # Amazon Linux 2
      InstanceType: !Ref InstanceType
      KeyName: !Ref KeyPairName
      SecurityGroupIds:
        - !Ref WebServerSecurityGroup
      UserData:
        Fn::Base64: |
          #!/bin/bash
          yum update -y
          yum install -y httpd
          systemctl start httpd
          systemctl enable httpd
          echo "<h1>Hello from AWS</h1>" > /var/www/html/index.html

Outputs:
  WebServerPublicIP:
    Description: Public IP address of the web server
    Value: !GetAtt WebServerInstance.PublicIp
    Export:
      Name: !Sub '${AWS::StackName}-PublicIP'
  
  WebServerInstanceArn:
    Description: ARN of the EC2 instance
    Value: !GetAtt WebServerInstance.Arn
    Export:
      Name: !Sub '${AWS::StackName}-InstanceArn'
  
  WebServerSecurityGroupId:
    Description: Security Group ID
    Value: !Ref WebServerSecurityGroup
    Export:
      Name: !Sub '${AWS::StackName}-SecurityGroupId'
```

### Understanding Outputs and Exports

**What the Outputs section does:**
- Makes values available after the stack is created
- You can view these in the AWS Console or via CLI/API
- Useful for getting resource information like IPs, ARNs, IDs, etc.

**What `Export` does:**
- Makes the output value available to OTHER CloudFormation stacks
- Without `Export`: The output is only visible in the stack's outputs (not usable by other stacks)
- With `Export`: Other stacks can import this value using `!ImportValue`

**Example: How to use the exported value in another stack**

```yaml
# Stack 1: Web Server (exports the IP)
Outputs:
  WebServerPublicIP:
    Description: Public IP address of the web server
    Value: !GetAtt WebServerInstance.PublicIp
    Export:
      Name: !Sub '${AWS::StackName}-PublicIP'
      # If stack name is "web-server-stack", export name becomes: "web-server-stack-PublicIP"

# Stack 2: Monitoring/Alerting (imports the IP)
Resources:
  CloudWatchAlarm:
    Type: AWS::CloudWatch::Alarm
    Properties:
      AlarmName: WebServerHealthCheck
      MetricName: CPUUtilization
      Namespace: AWS/EC2
      # This stack can now use the IP from Stack 1
      Dimensions:
        - Name: InstanceId
          Value: !ImportValue web-server-stack-PublicIP  # Uses the exported value
```

**Real-world scenario:**
1. **Stack 1** creates a web server and exports its IP address
2. **Stack 2** (monitoring) imports that IP to set up alerts
3. **Stack 3** (load balancer) imports the IP to configure routing

**Without Export (output only, not importable):**
```yaml
Outputs:
  WebServerPublicIP:
    Description: Public IP address
    Value: !GetAtt WebServerInstance.PublicIp
    # No Export section - can only be viewed, not imported by other stacks
```

**With Export (can be imported by other stacks):**
```yaml
Outputs:
  WebServerPublicIP:
    Description: Public IP address
    Value: !GetAtt WebServerInstance.PublicIp
    Export:
      Name: !Sub '${AWS::StackName}-PublicIP'  # Makes it importable
```

**Key Points:**
- `Value`: What you're outputting (the actual IP address in this case)
- `Export.Name`: A unique name that other stacks use to import this value
- Export names must be unique across all stacks in your AWS account/region
- Export names are case-sensitive and must match exactly when using `!ImportValue`

---

## Example 2: Storage Account with Blob Container

### AWS CloudFormation (S3)
```yaml
Resources:
  MyBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: my-unique-bucket-name-12345
      VersioningConfiguration:
        Status: Enabled
      PublicAccessBlockConfiguration:
        BlockPublicAcls: true
        BlockPublicPolicy: true
        IgnorePublicAcls: true
        RestrictPublicBuckets: true
      LifecycleConfiguration:
        Rules:
          - Id: DeleteOldFiles
            Status: Enabled
            ExpirationInDays: 90
            NoncurrentVersionExpirationInDays: 30

  BucketPolicy:
    Type: AWS::S3::BucketPolicy
    Properties:
      Bucket: !Ref MyBucket
      PolicyDocument:
        Statement:
          - Effect: Allow
            Principal:
              AWS: !GetAtt MyRole.Arn
            Action: s3:*
            Resource:
              - !Sub '${MyBucket}/*'
              - !GetAtt MyBucket.Arn

Outputs:
  BucketName:
    Description: Name of the S3 bucket
    Value: !Ref MyBucket
    Export:
      Name: !Sub '${AWS::StackName}-BucketName'
  
  BucketArn:
    Description: ARN of the S3 bucket
    Value: !GetAtt MyBucket.Arn
    Export:
      Name: !Sub '${AWS::StackName}-BucketArn'
```

---

## Example 3: Load Balanced Web Application

### AWS CloudFormation
```yaml
Resources:
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true

  PublicSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.1.0/24
      AvailabilityZone: !Select [0, !GetAZs '']

  PublicSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.2.0/24
      AvailabilityZone: !Select [1, !GetAZs '']

  InternetGateway:
    Type: AWS::EC2::InternetGateway

  AttachGateway:
    Type: AWS::EC2::VPCGatewayAttachment
    Properties:
      VpcId: !Ref VPC
      InternetGatewayId: !Ref InternetGateway

  PublicRouteTable:
    Type: AWS::EC2::RouteTable
    Properties:
      VpcId: !Ref VPC

  DefaultPublicRoute:
    Type: AWS::EC2::Route
    DependsOn: AttachGateway
    Properties:
      RouteTableId: !Ref PublicRouteTable
      DestinationCidrBlock: 0.0.0.0/0
      GatewayId: !Ref InternetGateway

  PublicSubnet1RouteTableAssociation:
    Type: AWS::EC2::SubnetRouteTableAssociation
    Properties:
      SubnetId: !Ref PublicSubnet1
      RouteTableId: !Ref PublicRouteTable

  PublicSubnet2RouteTableAssociation:
    Type: AWS::EC2::SubnetRouteTableAssociation
    Properties:
      SubnetId: !Ref PublicSubnet2
      RouteTableId: !Ref PublicRouteTable

  LoadBalancerSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Security group for load balancer
      VpcId: !Ref VPC
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0

  ApplicationLoadBalancer:
    Type: AWS::ElasticLoadBalancingV2::LoadBalancer
    Properties:
      Name: my-app-lb
      Subnets:
        - !Ref PublicSubnet1
        - !Ref PublicSubnet2
      SecurityGroups:
        - !Ref LoadBalancerSecurityGroup
      Scheme: internet-facing
      Type: application

  TargetGroup:
    Type: AWS::ElasticLoadBalancingV2::TargetGroup
    Properties:
      Name: my-app-targets
      Port: 80
      Protocol: HTTP
      VpcId: !Ref VPC
      HealthCheckPath: /health
      HealthCheckProtocol: HTTP

  Listener:
    Type: AWS::ElasticLoadBalancingV2::Listener
    Properties:
      DefaultActions:
        - Type: forward
          TargetGroupArn: !Ref TargetGroup
      LoadBalancerArn: !Ref ApplicationLoadBalancer
      Port: 80
      Protocol: HTTP

  AutoScalingGroup:
    Type: AWS::AutoScaling::AutoScalingGroup
    Properties:
      VPCZoneIdentifier:
        - !Ref PublicSubnet1
        - !Ref PublicSubnet2
      LaunchConfigurationName: !Ref LaunchConfiguration
      MinSize: 2
      MaxSize: 10
      DesiredCapacity: 2
      TargetGroupARNs:
        - !Ref TargetGroup
      HealthCheckType: ELB
      HealthCheckGracePeriod: 300

  LaunchConfiguration:
    Type: AWS::AutoScaling::LaunchConfiguration
    Properties:
      ImageId: ami-0c55b159cbfafe1f0
      InstanceType: t2.micro
      SecurityGroups:
        - !Ref LoadBalancerSecurityGroup
      UserData:
        Fn::Base64: |
          #!/bin/bash
          yum install -y httpd
          systemctl start httpd
          systemctl enable httpd

Outputs:
  LoadBalancerDNS:
    Description: DNS name of the load balancer
    Value: !GetAtt ApplicationLoadBalancer.DNSName
    Export:
      Name: !Sub '${AWS::StackName}-LoadBalancerDNS'
  
  LoadBalancerArn:
    Description: ARN of the Application Load Balancer
    Value: !GetAtt ApplicationLoadBalancer.LoadBalancerFullName
    Export:
      Name: !Sub '${AWS::StackName}-LoadBalancerArn'
  
  TargetGroupArn:
    Description: ARN of the Target Group
    Value: !Ref TargetGroup
    Export:
      Name: !Sub '${AWS::StackName}-TargetGroupArn'
```

---

## Example 4: Database with Private Access

### AWS CloudFormation (RDS)
```yaml
Resources:
  DBSubnetGroup:
    Type: AWS::RDS::DBSubnetGroup
    Properties:
      DBSubnetGroupDescription: Subnet group for RDS
      SubnetIds:
        - !Ref PrivateSubnet1
        - !Ref PrivateSubnet2

  DBSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Security group for RDS database
      VpcId: !Ref VPC
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 3306
          ToPort: 3306
          SourceSecurityGroupId: !Ref AppSecurityGroup

  Database:
    Type: AWS::RDS::DBInstance
    Properties:
      DBInstanceIdentifier: my-database
      Engine: mysql
      EngineVersion: '8.0'
      DBInstanceClass: db.t3.micro
      AllocatedStorage: 20
      StorageType: gp2
      MasterUsername: admin
      MasterUserPassword: !Ref DatabasePassword
      DBSubnetGroupName: !Ref DBSubnetGroup
      VPCSecurityGroups:
        - !Ref DBSecurityGroup
      BackupRetentionPeriod: 7
      MultiAZ: false
      PubliclyAccessible: false

Outputs:
  DatabaseEndpoint:
    Description: RDS instance endpoint
    Value: !GetAtt Database.Endpoint.Address
    Export:
      Name: !Sub '${AWS::StackName}-DatabaseEndpoint'
  
  DatabasePort:
    Description: RDS instance port
    Value: !GetAtt Database.Endpoint.Port
    Export:
      Name: !Sub '${AWS::StackName}-DatabasePort'
  
  DatabaseArn:
    Description: ARN of the RDS database instance
    Value: !GetAtt Database.DBInstanceArn
    Export:
      Name: !Sub '${AWS::StackName}-DatabaseArn'
```

---

## Example 5: Cross-Stack References with ImportValue

### Stack 1: Network Infrastructure (Exports values)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Network infrastructure stack'

Resources:
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true

  PublicSubnet:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.1.0/24
      AvailabilityZone: !Select [0, !GetAZs '']

Outputs:
  VPCId:
    Description: VPC ID
    Value: !Ref VPC
    Export:
      Name: !Sub '${AWS::StackName}-VPCId'
  
  VPCArn:
    Description: VPC ARN
    Value: !GetAtt VPC.VpcId
    Export:
      Name: !Sub '${AWS::StackName}-VPCArn'
  
  PublicSubnetId:
    Description: Public Subnet ID
    Value: !Ref PublicSubnet
    Export:
      Name: !Sub '${AWS::StackName}-PublicSubnetId'
```

### Stack 2: Application (Imports values from Stack 1)
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Application stack using imported network resources'

Parameters:
  NetworkStackName:
    Type: String
    Description: Name of the network stack to import values from
    Default: network-stack

Resources:
  WebServerSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Security group for web server
      VpcId: !ImportValue
        Fn::Sub: '${NetworkStackName}-VPCId'
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0

  WebServerInstance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: ami-0c55b159cbfafe1f0
      InstanceType: t2.micro
      SubnetId: !ImportValue
        Fn::Sub: '${NetworkStackName}-PublicSubnetId'
      SecurityGroupIds:
        - !Ref WebServerSecurityGroup
```

**Note**: `!ImportValue` is used to import exported values from other CloudFormation stacks. The exported value name must match exactly.

---

## Key Takeaways

1. **Outputs and Exports**: Use `Outputs` with `Export` to make values available to other stacks
2. **ImportValue**: Use `!ImportValue` to reference exported values from other stacks
3. **ARNs**: Use `!GetAtt ResourceName.Arn` to get the ARN of any resource
4. **Resource Attributes**: Use `!GetAtt` to access resource-specific attributes (e.g., `PublicIp`, `DNSName`, `Endpoint.Address`)
5. **Cross-Stack Dependencies**: Stacks that import values must be deployed after the exporting stack
6. **Stack Names**: When using `!ImportValue`, the export name must match exactly (case-sensitive)

---

## Practice Exercises

1. Create a CloudFormation template that:
   - Creates a VPC with public and private subnets
   - Creates a NAT Gateway in the public subnet
   - Creates an EC2 instance in the private subnet
   - Outputs the VPC ID, Subnet IDs, and NAT Gateway ID with exports

2. Create a CloudFormation template that:
   - Creates an S3 bucket with versioning enabled
   - Creates an IAM role with S3 read/write permissions
   - Attaches a bucket policy
   - Outputs the bucket ARN and name with exports

3. Create a multi-stack deployment:
   - Stack 1: Network infrastructure (VPC, subnets, NAT Gateway)
   - Stack 2: Database (RDS in private subnet using ImportValue)
   - Stack 3: Application (EC2 instances using ImportValue for network resources)
