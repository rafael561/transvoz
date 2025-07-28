# TransVoz - Audio Transcription Service

TransVoz is a comprehensive audio transcription service with speaker diarization capabilities, built with Java 24, Spring Boot 3.4, and PostgreSQL 15.

## Features

- **Audio Upload**: Support for multiple audio formats (MP3, WAV, M4A, FLAC, AAC, OGG)
- **Speaker Diarization**: Automatic separation of up to 6 speakers
- **External API Integration**: Seamless integration with transcription services
- **Secure Authentication**: JWT-based authentication with refresh tokens
- **File Storage**: Local and S3-compatible storage options
- **Real-time Updates**: Webhook support for transcription status updates
- **Comprehensive API**: RESTful API with OpenAPI documentation

## Technology Stack

- **Backend**: Java 21, Spring Boot 3.4
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL 15 with Flyway migrations
- **Security**: JWT tokens, Argon2id password hashing, AES-256 file encryption
- **Storage**: Local filesystem (dev) and S3/MinIO (production)
- **Documentation**: OpenAPI 3.1 with Swagger UI
- **Testing**: JUnit 5, Mockito, Testcontainers
- **CI/CD**: GitHub Actions
- **Containerization**: Docker and Docker Compose

## Quick Start

### Prerequisites

- Java 21+
- Docker and Docker Compose
- PostgreSQL 15 (if running locally)

### Development Setup

1. **Clone the repository**
