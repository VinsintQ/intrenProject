# CryptoPeak Deployment Guide (SQLite)

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Sufficient disk space for database growth

## Production Deployment Steps

### 1. Build the Application
```bash
mvnw clean package -DskipTests
```

### 2. Create Required Directories
```bash
# Linux/Mac
sudo mkdir -p /var/lib/cryptopeak
sudo mkdir -p /var/log/cryptopeak

# Windows
mkdir C:\cryptopeak\data
mkdir C:\cryptopeak\logs
```

### 3. Set Environment Variables

#### Linux/Mac
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_PATH=/var/lib/cryptopeak/cryptopeak.db
export JWT_SECRET=your-secure-random-secret-key-min-256-bits
export JWT_EXPIRATION_MS=86400000
export LOG_PATH=/var/log/cryptopeak/application.log
```

#### Windows
```cmd
set SPRING_PROFILES_ACTIVE=prod
set DB_PATH=C:\cryptopeak\data\cryptopeak.db
set JWT_SECRET=your-secure-random-secret-key-min-256-bits
set JWT_EXPIRATION_MS=86400000
set LOG_PATH=C:\cryptopeak\logs\application.log
```

### 4. Run the Application
```bash
java -jar target/CryptoPeak-0.0.1-SNAPSHOT.jar
```

## Production Considerations

### Database Location
- Use absolute paths for production database
- Ensure write permissions for the application user
- Store database outside the application directory

### Database Backups
SQLite databases are single files, making backups simple:

```bash
# Stop the application first, then:
cp /var/lib/cryptopeak/cryptopeak.db /backup/cryptopeak-$(date +%Y%m%d).db

# Or use SQLite backup command while running:
sqlite3 /var/lib/cryptopeak/cryptopeak.db ".backup /backup/cryptopeak-$(date +%Y%m%d).db"
```

### Security Best Practices
1. **JWT Secret**: Generate a strong random secret (256+ bits)
   ```bash
   openssl rand -base64 32
   ```

2. **File Permissions**: Restrict database file access
   ```bash
   chmod 600 /var/lib/cryptopeak/cryptopeak.db
   chown appuser:appuser /var/lib/cryptopeak/cryptopeak.db
   ```

3. **HTTPS**: Always use HTTPS in production (configure reverse proxy)

### Performance Tuning

#### SQLite Pragmas (Optional)
For better performance, you can add these to your database URL:
```properties
spring.datasource.url=jdbc:sqlite:/var/lib/cryptopeak/cryptopeak.db?journal_mode=WAL&synchronous=NORMAL&cache_size=10000
```

- `journal_mode=WAL`: Write-Ahead Logging for better concurrency
- `synchronous=NORMAL`: Balance between safety and speed
- `cache_size=10000`: Larger cache for better performance

### Monitoring

#### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

#### Monitor Database Size
```bash
ls -lh /var/lib/cryptopeak/cryptopeak.db
```

#### View Logs
```bash
tail -f /var/log/cryptopeak/application.log
```

### Systemd Service (Linux)

Create `/etc/systemd/system/cryptopeak.service`:
```ini
[Unit]
Description=CryptoPeak Application
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/cryptopeak
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_PATH=/var/lib/cryptopeak/cryptopeak.db"
Environment="JWT_SECRET=your-secret-here"
Environment="LOG_PATH=/var/log/cryptopeak/application.log"
ExecStart=/usr/bin/java -jar /opt/cryptopeak/CryptoPeak-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable cryptopeak
sudo systemctl start cryptopeak
sudo systemctl status cryptopeak
```

## Scaling Limitations

SQLite is suitable for:
- Single-server deployments
- Up to ~100k requests/day
- Read-heavy workloads

If you need to scale beyond this, consider migrating to PostgreSQL or MySQL.

## Troubleshooting

### Database Locked Error
- SQLite allows only one writer at a time
- Ensure `maximum-pool-size=1` in configuration
- Consider WAL mode for better concurrency

### Permission Denied
- Check file permissions on database file and directory
- Ensure application user has write access

### Database Corruption
- Restore from backup
- Run integrity check: `sqlite3 cryptopeak.db "PRAGMA integrity_check;"`
