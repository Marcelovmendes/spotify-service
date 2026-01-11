# 🎵 PlaySwap 




# Architecture

![Architecture Overview](docs/architecture.png)


## Flow

1. User selects Spotify playlist
2. Job created in Redis queue
3. Worker fetches tracks (Spotify API)
4. Worker matches with YouTube (5 concurrent searches)
5. Worker creates YouTube playlist
6. History saved to PostgreSQL

# About services

### [Spotify Service Documentation](#Spotify-service)
### [youtube-service](#youtube-service)
### [convert-service](#convert-service)




## Spotify Service Documentation

### Overview

The Spotify Service is a microservice that integrates with Spotify's Web API, providing authentication and resource access for user's music data. It handles OAuth 2.0 authorization flow with PKCE (Proof Key for Code Exchange) and allows retrieval of user playlists, albums, and tracks.

### Getting Started

#### Prerequisites

- Java 21
- Maven
- Redis instance on Docker
- Spotify Developer Account with registered application
- Postgres

### Authentication Flow

 - User initiates authentication via /auth/spotify endpoint
 - Service generates PKCE code verifier/challenge and state parameter
 - User is redirected to Spotify authorization page
 - After permission grant, Spotify redirects to /auth/callback
 - Service validates state, exchanges code for tokens
 - Tokens are stored in session for subsequent API calls

### Security Features

- PKCE implementation prevents authorization code interception
- State parameter prevents CSRF attacks
- Short-lived state tokens (10 min timeout)
- Secure token storage in Redis

### Configuration

#### Required Properties

```
spring.application.name=spotify-service
# Spotify API Configuration
spotify.client-id=[your-spotify-client-id]
spotify.client-secret=[your-spotify-client-secret]
spotify.redirect-uri=http://localhost:8080/auth/callback
spotify.auth.scopes= "user-read-private user-read-email playlist-read-private playlist-read-collaborative user-library-read"


#PSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/playswap
spring.datasource.username=postgres
spring.data.jdbc.dialect=postgresql
spring.sql.init.platform=postgresql

#Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
```

### Running Locally

```
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd spotify-service

# Build the project
mvn clean package

# Run the application
java -jar target/spotify-service.jar

```

### Run Redis

``` 
# On Docker
docker run --name redis -p 6379:6379 -d redis

# Check image
docker ps

```




