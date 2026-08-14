#!/bin/sh
set -e

NGINX_PORT=${PORT:-80}

if [ -z "$BACKEND_URL" ]; then
  echo "WARNUNG: BACKEND_URL nicht gesetzt – API-Proxy deaktiviert, nur SPA wird ausgeliefert"
  cat > /etc/nginx/conf.d/default.conf << EOF
server {
    listen ${NGINX_PORT};
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    gzip on;
    gzip_vary on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
    gzip_min_length 1000;

    location ~* \.(js|css|png|jpg|ico|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF
else
  envsubst '${BACKEND_URL} ${PORT}' < /etc/nginx/conf.d/nginx.conf.template > /etc/nginx/conf.d/default.conf
fi

exec nginx -g "daemon off;"
