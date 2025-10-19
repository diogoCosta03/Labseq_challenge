FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration=production

# Install serve
RUN npm install -g serve

EXPOSE 80

CMD ["serve", "-s", "dist/labseq-frontend/browser", "-l", "80"]