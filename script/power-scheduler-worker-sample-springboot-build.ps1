#!/bin/pwsh
docker build `
  -t power-scheduler-worker-sample-springboot `
  -f ../power-scheduler-worker-sample/power-scheduler-worker-sample-springboot/Dockerfile ../
