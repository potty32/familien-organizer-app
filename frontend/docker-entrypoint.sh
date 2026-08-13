#!/bin/sh
set -e

# BACKEND_URL muss als Umgebungsvariable gesetzt sein (Railway: interner Service-URL)
if [ -z "$BACKEND_URL" ]; then
  echo "WARNUNG: BACKEND_URL nicht gesetzt – API-Proxy deaktiviert"
  # Fallback: leere proxy_pass entfernen, nur SPA ausliefern
  cp /etc/nginx/conf.d/nginx.conf.template /etc/nginx/conf.d/default.conf
else
  envsubst '${BACKEND_URL}' < /etc/nginx/conf.d/nginx.conf.template > /etc/nginx/conf.d/default.conf
fi

exec nginx -g "daemon off;"
