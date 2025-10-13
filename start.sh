#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Set environment variables for the test environment
export POSTGRES_DB=vaudoise
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres

# Run docker-compose
docker-compose up --build