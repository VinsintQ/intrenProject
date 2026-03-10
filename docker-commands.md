# Docker Commands for CryptoPeak

## Build and Run

### Using Docker Compose (Recommended)
```bash
# Build and start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down

# Stop and remove volumes (deletes database)
docker-compose down -v

# Rebuild after code changes
docker-compose up -d --build
```

### Using Docker CLI
```bash
# Build the image
docker build -t cryptopeak:latest .

# Run the container
docker run -d \
  --name cryptopeak-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=your-secret-key \
  -v cryptopeak-data:/app/data \
  -v cryptopeak-logs:/app/logs \
  cryptopeak:latest

# View logs
docker logs -f cryptopeak-backend

# Stop container
docker stop cryptopeak-backend

# Remove container
docker rm cryptopeak-backend
```

## Development Commands

### Access container shell
```bash
docker exec -it cryptopeak-backend sh
```

### View application logs
```bash
docker-compose logs -f cryptopeak-app
```

### Restart application
```bash
docker-compose restart cryptopeak-app
```

### Check container health
```bash
docker inspect --format='{{.State.Health.Status}}' cryptopeak-backend
```

## Database Management

### Backup database
```bash
# Copy database from container to host
docker cp cryptopeak-backend:/app/data/cryptopeak.db ./backup-$(date +%Y%m%d).db
```

### Restore database
```bash
# Copy database from host to container
docker cp ./backup.db cryptopeak-backend:/app/data/cryptopeak.db
docker-compose restart cryptopeak-app
```

### Access database file
```bash
# Enter container
docker exec -it cryptopeak-backend sh

# Database is located at /app/data/cryptopeak.db
```

## Cleanup

### Remove all containers and images
```bash
docker-compose down
docker rmi cryptopeak:latest
```

### Remove volumes (WARNING: deletes data)
```bash
docker volume rm cryptopeak-data cryptopeak-logs
```

### Clean up everything
```bash
docker system prune -a --volumes
```

## Production Deployment

### Set environment variables
```bash
# Create .env file from example
cp .env.example .env

# Edit .env with production values
nano .env
```

### Deploy with production settings
```bash
# Build and start
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs cryptopeak-app

# Check container status
docker-compose ps
```

### Port already in use
```bash
# Change port in docker-compose.yml
# ports:
#   - "8081:8080"  # Use 8081 instead of 8080
```

### Out of memory
```bash
# Add memory limits to docker-compose.yml
# deploy:
#   resources:
#     limits:
#       memory: 512M
```

## Testing

### Test the API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Register user
curl -X POST http://localhost:8080/auth/users/register \
  -H "Content-Type: application/json" \
  -d '{"userName":"testuser","emailAddress":"test@example.com","password":"password123"}'
```

## Monitoring

### View resource usage
```bash
docker stats cryptopeak-backend
```

### Check disk usage
```bash
docker system df
```

### Inspect volumes
```bash
docker volume inspect cryptopeak-data
docker volume inspect cryptopeak-logs
```
