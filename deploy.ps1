mvn clean install
if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed. Exiting..."
    exit 1
}


docker build -t aktey/outdry-backend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker build failed. Exiting..."
    exit 1
}

docker push aktey/outdry-backend:latest
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker push failed. Exiting..."
    exit 1
}

Write-Host "All commands executed successfully!"