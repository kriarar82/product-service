#!/bin/bash

echo "Starting Product Service in DEBUG mode..."
echo "Debug port: 5005"
echo "Application port: 8080"
echo ""

# Kill any existing process on port 8080
lsof -ti:8080 | xargs kill -9 2>/dev/null || true

# Start in debug mode
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" \
  -Dspring-boot.run.arguments="--debug"
