#!/bin/bash

# This script runs the Postman collection using Newman
# Make sure you have Node.js and Newman installed
# npm install -g newman

# Set variables
COLLECTION_FILE="MyCollection.json"
ENVIRONMENT_FILE="environment.json"
REPORT_DIR="newman-reports"

# Create reports directory if it doesn't exist
mkdir -p "$REPORT_DIR"

# Run Newman with HTML reporter
echo "Running API tests with Newman..."
newman run "$COLLECTION_FILE" \
  --environment "$ENVIRONMENT_FILE" \
  --reporters cli,htmlextra \
  --reporter-htmlextra-export "$REPORT_DIR/report-$(date +%Y%m%d-%H%M%S).html" \
  --reporter-htmlextra-title "API Test Report" \
  --reporter-htmlextra-darkTheme \
  --timeout-request 5000

# Check if Newman run was successful
if [ $? -eq 0 ]; then
  echo "API tests completed successfully!"
  exit 0
else
  echo "API tests failed!"
  exit 1
fi