#!/bin/bash

# Configuration
REQUIRED_SERVICES=("SP-GATEWAY" "AUTH-SERVER" "SP-USER-SERVICE" "SP-PRODUCT-SERVICE" "SP-PAYMENT-SERVICE" "SP-ORDER-SERVICE")
EUREKA_URL="http://localhost:8761/eureka/apps"
CHECK_INTERVAL=10  # Check every 10 seconds

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Starting continuous service health check...${NC}"
echo -e "${YELLOW}Required services: ${REQUIRED_SERVICES[*]}${NC}"
echo -e "${YELLOW}Checking every ${CHECK_INTERVAL} seconds until all services are ready${NC}\n"

while true; do
  # Record check start time
  CHECK_START_TIME=$(date +%s)

  # Fetch Eureka registry
  if ! EUREKA_RESPONSE=$(curl -s "$EUREKA_URL"); then
    echo -e "${RED}✗ [$(date '+%T')] Eureka server unavailable${NC}"
    sleep $CHECK_INTERVAL
    continue
  fi

  # Initialize status tracking
  ALL_SERVICES_READY=true
  MISSING_SERVICES=()
  UNHEALTHY_SERVICES=()

  # Check each service
  for SERVICE in "${REQUIRED_SERVICES[@]}"; do
    # Service registration check
    if ! echo "$EUREKA_RESPONSE" | grep -q "<name>$SERVICE</name>"; then
      ALL_SERVICES_READY=false
      MISSING_SERVICES+=("$SERVICE")
      continue
    fi

    # Service health check (at least one UP instance)
    if ! echo "$EUREKA_RESPONSE" | grep -A 15 "<name>$SERVICE</name>" | grep -q "<status>UP</status>"; then
      ALL_SERVICES_READY=false
      UNHEALTHY_SERVICES+=("$SERVICE")
    fi
  done

  # Calculate check duration
  CHECK_DURATION=$(( $(date +%s) - CHECK_START_TIME ))

  # Output results
  if $ALL_SERVICES_READY; then
    echo -e "${GREEN}✓ [$(date '+%T')] All services are registered and healthy!${NC}"
    sleep 15
    exit 0
  else
    STATUS_MESSAGE="[$(date '+%T')] Check took ${CHECK_DURATION}s"
    [ ${#MISSING_SERVICES[@]} -gt 0 ] && STATUS_MESSAGE+=" | Missing: ${MISSING_SERVICES[*]}"
    [ ${#UNHEALTHY_SERVICES[@]} -gt 0 ] && STATUS_MESSAGE+=" | Unhealthy: ${UNHEALTHY_SERVICES[*]}"
    echo -e "${YELLOW}⏳ ${STATUS_MESSAGE}${NC}"
  fi

  # Wait for next check
  sleep $CHECK_INTERVAL
done
