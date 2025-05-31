#!/bin/bash
docker build \
  -t power-scheduler-worker-sample-frameless \
  -f ../power-scheduler-worker-sample/power-scheduler-worker-sample-frameless/Dockerfile ../
