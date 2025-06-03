#!/usr/bin/env pwsh
param (
  [string]$repo = "",
  [string]$tag = "latest"
)

if (-not  [string]::IsNullOrEmpty($repo))
{
  $repo = "$repo/"
}

docker build `
  -t ${repo}power-scheduler-worker-sample-frameless `
  -f ../power-scheduler-worker-sample/power-scheduler-worker-sample-frameless/Dockerfile `
  ../
