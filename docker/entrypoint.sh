#!/bin/sh
set -e

echo "🚀 Starting Agora Notifications API..."
java -jar app.jar &

APP_PID=$!

echo "⏳ Waiting for API to be ready..."
until curl -s http://localhost:8080/actuator/health | grep UP > /dev/null; do
  sleep 2
done

echo "✅ API is UP. Running demo requests..."

# ---------------------------
# Sync Email (rule-based)
# ---------------------------
curl -s -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EMAIL",
    "to": "user@example.com",
    "subject": "Email síncrono",
    "body": "Enviado usando reglas por prioridad",
    "priority": "HIGH",
    "metadata": {}
  }'
echo ""

# ---------------------------
# Async Email (forced provider)
# ---------------------------
curl -s -X POST http://localhost:8080/api/notifications/send-async \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EMAIL",
    "provider": "MAILGUN",
    "to": "user@example.com",
    "subject": "Email async",
    "body": "Ejemplo async con override de provider",
    "priority": "CRITICAL",
    "metadata": {}
  }'
echo ""

# ---------------------------
# Async SMS
# ---------------------------
curl -s -X POST http://localhost:8080/api/notifications/send-async \
  -H "Content-Type: application/json" \
  -d '{
    "type": "SMS",
    "to": "+593999000111",
    "body": "SMS async demo",
    "priority": "NORMAL",
    "metadata": {}
  }'
echo ""

echo "🎉 Demo requests executed successfully"
echo "📡 API running on http://localhost:8080"

wait $APP_PID
