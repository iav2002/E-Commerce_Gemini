# Stage 1: Build the React app
FROM node:18-alpine AS build

WORKDIR /app

# Install dependencies first (better layer caching)
COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .

# Build arg for backend URL (set at build time)
ARG VITE_BASE_URL=http://localhost:8080
ENV VITE_BASE_URL=$VITE_BASE_URL

RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy built files to Nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Custom Nginx config for SPA routing (React Router)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
