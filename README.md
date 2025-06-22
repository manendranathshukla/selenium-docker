# Docker for SDET Tutorial

This repository contains a complete setup for running Selenium tests and API tests in Docker containers. It demonstrates how Software Development Engineers in Test (SDETs) can leverage Docker for testing.

## Project Structure

```
docker-for-sdet-tutorial/
│
├── selenium-docker/           # Selenium test project
│   ├── Dockerfile            # Docker image for Selenium tests
│   ├── testng.xml            # TestNG configuration
│   ├── pom.xml               # Maven dependencies
│   └── src/test/java/...     # Test source code
│
├── selenium-grid-compose/    # Selenium Grid setup
│   └── docker-compose.yml    # Docker Compose for Selenium Grid
│
├── api-tests/                # API testing with Postman/Newman
│   ├── MyCollection.json     # Postman collection
│   ├── environment.json      # Postman environment variables
│   └── run-postman.sh        # Script to run Newman tests
│
├── .dockerignore             # Files to exclude from Docker builds
└── README.md                 # This file
```

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop) installed
- [Docker Compose](https://docs.docker.com/compose/install/) installed
- [Maven](https://maven.apache.org/download.cgi) (optional, for local development)
- [Node.js and npm](https://nodejs.org/) (optional, for running API tests locally)
- [Newman](https://www.npmjs.com/package/newman) (optional, for running API tests locally)

## Selenium Tests

### Running Selenium Tests Locally

1. Navigate to the selenium-docker directory:
   ```bash
   cd selenium-docker
   ```

2. Run the tests using Maven:
   ```bash
   mvn clean test
   ```

### Building the Selenium Docker Image

1. Navigate to the selenium-docker directory:
   ```bash
   cd selenium-docker
   ```

2. Build the Docker image:
   ```bash
   docker build -t selenium-tests .
   ```

3. Run the tests in a Docker container:
   ```bash
   docker run --rm selenium-tests
   ```

## Selenium Grid

The Selenium Grid setup allows you to run tests in parallel across multiple browsers.

### Starting Selenium Grid

1. Navigate to the selenium-grid-compose directory:
   ```bash
   cd selenium-grid-compose
   ```

2. Start the Selenium Grid using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the Selenium Grid console at: http://localhost:4444/grid/console

### Running Tests on Selenium Grid

1. Start the Selenium Grid as described above.

2. Run the tests against the grid:
   ```bash
   docker-compose up selenium-tests
   ```

### Stopping Selenium Grid

```bash
cd selenium-grid-compose
docker-compose down
```

## API Tests

### Running API Tests Locally

1. Install Newman (if not already installed):
   ```bash
   npm install -g newman newman-reporter-htmlextra
   ```

2. Navigate to the api-tests directory:
   ```bash
   cd api-tests
   ```

3. Run the tests using Newman:
   ```bash
   newman run MyCollection.json --environment environment.json
   ```

4. Alternatively, use the provided shell script:
   ```bash
   chmod +x run-postman.sh
   ./run-postman.sh
   ```

### Running API Tests in Docker

1. Build and run a Newman Docker container:
   ```bash
   docker run --rm -v "$(pwd)/api-tests:/etc/newman" -t postman/newman:alpine run MyCollection.json --environment environment.json
   ```

## Jenkins CI/CD Integration

Here's an example Jenkinsfile that you can use to set up a CI/CD pipeline for this project:

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Selenium Docker Image') {
            steps {
                dir('selenium-docker') {
                    sh 'docker build -t selenium-tests:${BUILD_NUMBER} .'
                }
            }
        }
        
        stage('Start Selenium Grid') {
            steps {
                dir('selenium-grid-compose') {
                    sh 'docker-compose up -d'
                }
            }
        }
        
        stage('Run Selenium Tests') {
            steps {
                dir('selenium-grid-compose') {
                    sh 'docker-compose up selenium-tests'
                }
            }
        }
        
        stage('Run API Tests') {
            steps {
                sh 'docker run --rm -v "${WORKSPACE}/api-tests:/etc/newman" -t postman/newman:alpine run MyCollection.json --environment environment.json --reporters cli,junit --reporter-junit-export "reports/newman-report.xml"'
            }
        }
    }
    
    post {
        always {
            dir('selenium-grid-compose') {
                sh 'docker-compose down'
            }
            junit '**/test-output/junitreports/*.xml, **/reports/*.xml'
        }
    }
}
```

## Best Practices

1. **Use Docker Compose for complex setups**: Docker Compose makes it easy to define and run multi-container applications.

2. **Optimize Docker images**: Keep your Docker images as small as possible by using multi-stage builds and removing unnecessary dependencies.

3. **Use volume mounts for test reports**: Mount volumes to save test reports from containers to your host machine.

4. **Parameterize your tests**: Use environment variables to configure your tests for different environments.

5. **Use wait scripts**: Ensure services are fully up before running tests against them.

## Troubleshooting

### Common Issues

1. **Connection refused to Selenium Grid**:
   - Ensure the Selenium Grid is running: `docker-compose ps`
   - Check if the hub is accessible: `curl http://localhost:4444/wd/hub/status`

2. **Tests fail with browser-related errors**:
   - Ensure you're using compatible versions of Selenium and browsers
   - Check browser capabilities in your test code

3. **Docker container exits immediately**:
   - Check container logs: `docker logs <container_id>`
   - Ensure your CMD or ENTRYPOINT is correctly configured

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.