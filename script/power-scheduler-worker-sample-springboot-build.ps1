#!/usr/bin/env pwsh
param (
  [string]$repo = "",
  [string]$tag = "latest"
)

if ( [string]::IsNullOrEmpty($repo))
{
  $repo = "$repo/"
}

docker build `
  -t power-scheduler-worker-sample-springboot `
  -f ../power-scheduler-worker-sample/power-scheduler-worker-sample-springboot/Dockerfile `
  ../
