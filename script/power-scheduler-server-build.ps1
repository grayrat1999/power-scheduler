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
    -t ${repo}power-scheduler-server:$tag  `
    -f ../power-scheduler-server/Dockerfile `
    ../
