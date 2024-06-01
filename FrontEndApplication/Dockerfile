FROM node:latest as builder
RUN mkdir -p /app
WORKDIR /app

COPY . .
RUN npm install
RUN npm run prod

FROM nginx:alpine
COPY src/nginx/etc/conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder app/dist/front-end-application usr/share/nginx/html
