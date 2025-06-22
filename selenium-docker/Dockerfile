FROM maven:3.8.4-openjdk-11-slim

# Set working directory
WORKDIR /app

# Set environment variable to indicate Docker environment
ENV IS_RUNNING_IN_DOCKER=true

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && apt-get clean

# Install ChromeDriver
RUN wget -q "https://storage.googleapis.com/chrome-for-testing-public/137.0.7151.70/linux64/chromedriver-linux64.zip" \
    && unzip chromedriver-linux64.zip \
    && mv chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver \
    && rm chromedriver-linux64.zip

# Copy project files
COPY pom.xml .
COPY testng.xml .
COPY src/ ./src/

# Run tests when container starts
CMD ["mvn", "clean", "test", "-Dheadless=true"]