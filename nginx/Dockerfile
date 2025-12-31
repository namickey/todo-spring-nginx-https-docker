FROM nginx:1.27-alpine

RUN apk add --no-cache openssl
COPY conf.d/default.conf /etc/nginx/conf.d/default.conf
RUN mkdir -p /etc/nginx/certs \
    && openssl req -x509 -nodes -days 365 -subj "/CN=localhost" \
       -newkey rsa:2048 -keyout /etc/nginx/certs/selfsigned.key \
       -out /etc/nginx/certs/selfsigned.pem

EXPOSE 80 443
