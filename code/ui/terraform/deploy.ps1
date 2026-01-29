# PowerShell script to deploy Spring Boot app to EKS
# Usage: .\deploy.ps1 -Environment dev|uat|prod [-Action plan|apply|destroy]

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("dev", "uat", "prod")]
    [string]$Environment,
    
    [Parameter(Mandatory=$false)]
    [ValidateSet("init", "plan", "apply", "destroy", "output")]
    [string]$Action = "apply"
)

$ErrorActionPreference = "Stop"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Terraform EKS Deployment Script" -ForegroundColor Cyan
Write-Host "Environment: $Environment" -ForegroundColor Yellow
Write-Host "Action: $Action" -ForegroundColor Yellow
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$varFile = "environments\$Environment.tfvars"

if (-not (Test-Path $varFile)) {
    Write-Host "Error: Environment file not found: $varFile" -ForegroundColor Red
    exit 1
}

switch ($Action) {
    "init" {
        Write-Host "Initializing Terraform..." -ForegroundColor Green
        terraform init
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Terraform init failed!" -ForegroundColor Red
            exit 1
        }
    }
    
    "plan" {
        Write-Host "Running Terraform plan..." -ForegroundColor Green
        terraform plan -var-file=$varFile -out=tfplan
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Terraform plan failed!" -ForegroundColor Red
            exit 1
        }
    }
    
    "apply" {
        Write-Host "Applying Terraform configuration..." -ForegroundColor Green
        terraform apply -var-file=$varFile -auto-approve
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Terraform apply failed!" -ForegroundColor Red
            exit 1
        }
        
        Write-Host ""
        Write-Host "Deployment completed successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Getting cluster information..." -ForegroundColor Cyan
        terraform output -var-file=$varFile
    }
    
    "destroy" {
        Write-Host "WARNING: This will destroy all resources!" -ForegroundColor Red
        $confirm = Read-Host "Type 'yes' to confirm"
        if ($confirm -eq "yes") {
            terraform destroy -var-file=$varFile -auto-approve
            if ($LASTEXITCODE -ne 0) {
                Write-Host "Terraform destroy failed!" -ForegroundColor Red
                exit 1
            }
        } else {
            Write-Host "Destroy cancelled." -ForegroundColor Yellow
        }
    }
    
    "output" {
        Write-Host "Terraform outputs:" -ForegroundColor Green
        terraform output -var-file=$varFile
    }
}

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
