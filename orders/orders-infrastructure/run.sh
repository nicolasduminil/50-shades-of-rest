#!/bin/bash
curl -X POST \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     --data-binary "@customer.json" \
     http://localhost:8080/customers

for i in {1. .5}
do
  sleep 1
  curl http://localhost:8080/customers/random
done