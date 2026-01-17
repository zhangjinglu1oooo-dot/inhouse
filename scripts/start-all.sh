#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"
PID_FILE="${ROOT_DIR}/logs/services.pids"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: mvn is not installed or not in PATH." >&2
  exit 1
fi

mkdir -p "${LOG_DIR}"
: > "${PID_FILE}"

services=(
  "services/iam-service"
  "services/permission-service"
  "services/registry-service"
  "services/ai-service"
  "services/event-service"
  "services/observability-service"
  "services/gateway-service"
)

log_files=()

cleanup() {
  if [[ -s "${PID_FILE}" ]]; then
    echo "Stopping services..."
    while read -r pid; do
      if kill -0 "${pid}" >/dev/null 2>&1; then
        kill "${pid}" || true
      fi
    done < "${PID_FILE}"
  fi
}

trap cleanup EXIT

pushd "${ROOT_DIR}" >/dev/null

echo "Building all services..."
mvn clean package

echo "Starting services..."
for service in "${services[@]}"; do
  service_name="$(basename "${service}")"
  log_file="${LOG_DIR}/${service_name}.log"
  log_files+=("${log_file}")
  echo "- ${service_name} (logs: ${log_file})"
  mvn -pl "${service}" spring-boot:run > "${log_file}" 2>&1 &
  echo $! >> "${PID_FILE}"
  sleep 1
  if ! kill -0 $! >/dev/null 2>&1; then
    echo "Failed to start ${service_name}. Check ${log_file}." >&2
    exit 1
  fi
done

popd >/dev/null

echo "All services started. Press Ctrl+C to stop."

# Tail logs to keep the script running and provide feedback.
tail -f "${log_files[@]}"
