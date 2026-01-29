#!/bin/bash
# Bash script to deploy Spring Boot app to EKS
# Usage: ./deploy.sh <environment> <action>
# Example: ./deploy.sh dev apply

set -e

ENVIRONMENT=$1
ACTION=${2:-apply}

if [ -z "$ENVIRONMENT" ]; then
    echo "Error: Environment not specified"
    echo "Usage: $0 <dev|uat|prod> [init|plan|apply|destroy|output]"
    exit 1
fi

if [[ ! "$ENVIRONMENT" =~ ^(dev|uat|prod)$ ]]; then
    echo "Error: Environment must be one of: dev, uat, prod"
    exit 1
fi

VAR_FILE="environments/${ENVIRONMENT}.tfvars"

if [ ! -f "$VAR_FILE" ]; then
    echo "Error: Environment file not found: $VAR_FILE"
    exit 1
fi

echo "========================================="
echo "Terraform EKS Deployment Script"
echo "Environment: $ENVIRONMENT"
echo "Action: $ACTION"
echo "========================================="
echo ""

case $ACTION in
    init)
        echo "Initializing Terraform..."
        terraform init
        ;;
    
    plan)
        echo "Running Terraform plan..."
        terraform plan -var-file="$VAR_FILE" -out=tfplan
        ;;
    
    apply)
        echo "Applying Terraform configuration..."
        terraform apply -var-file="$VAR_FILE" -auto-approve
        if [ $? -eq 0 ]; then
            echo ""
            echo "Deployment completed successfully!"
            echo ""
            echo "Getting cluster information..."
            terraform output -var-file="$VAR_FILE"
        fi
        ;;
    
    destroy)
        echo "WARNING: This will destroy all resources!"
        read -p "Type 'yes' to confirm: " confirm
        if [ "$confirm" = "yes" ]; then
            terraform destroy -var-file="$VAR_FILE" -auto-approve
        else
            echo "Destroy cancelled."
        fi
        ;;
    
    output)
        echo "Terraform outputs:"
        terraform output -var-file="$VAR_FILE"
        ;;
    
    *)
        echo "Error: Unknown action: $ACTION"
        echo "Valid actions: init, plan, apply, destroy, output"
        exit 1
        ;;
esac

echo ""
echo "Done!"
