FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="TransVoz Team"
LABEL version="1.0.0"
LABEL description="TransVoz Audio Transcription Service"

# Create app user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Create directories
RUN mkdir -p /app/uploads /app/logs && \
    chown -R appuser:appgroup /app

# Set working directory
WORKDIR /app

# Copy jar file
COPY build/libs/transvoz-*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM arguments
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
