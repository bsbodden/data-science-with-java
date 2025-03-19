# Redis AI Java Resources

A self-contained Docker Application that provides a set of Jupyter Notebooks and associated utilities to work with Java and Java AI Libraries

## Overview

This repository provides a Docker-based environment with Jupyter notebooks demonstrating how to create your own Data Science/ML/GenAI learning platform

## Getting Started

### Clone the Repository

```bash
git clone git@github.com:bsbodden/data-science-with-java.git
cd data-science-with-java.git
```

## Start the Docker Containers

This will start two containers:
- **jupyter**: A Jupyter notebook server with Java and Python kernels
- **redis-java**: A Redis instance bundled with RedisInsight GUI

```bash
docker compose up
```

### Launch Jupyter Notebook

1. Monitor the Docker logs for the Jupyter server startup message
2. Look for a URL like `http://127.0.0.1:8888/lab?token=<your_token>`
3. Open this URL in your browser
4. Navigate to the notebooks directory and open one of the example notebooks

### Adding Java Libraries

Java dependencies can be added directly in the notebooks using the `%maven` magic command:

```java
%maven group:artifact:version
```

For example:
```java
%maven org.apache.commons:commons-math3:3.6.1
```

### Creating New Notebooks

1. Simply create new `.ipynb` files in the `notebooks` directory
2. They will automatically be available in the Jupyter interface
3. No configuration changes needed

### Using RedisInsight

RedisInsight is a visual tool for Redis that provides an intuitive interface to:
- Browse and interact with your Redis data
- Run queries and commands
- Analyze memory usage
- Monitor performance metrics

To access RedisInsight:
1. Once the containers are running, open your browser to [http://localhost:8001](http://localhost:8001)
2. On first launch, you'll need to connect to your Redis instance
3. Use the following connection details:
   - Host: redis
   - Port: 6379
   - Username: default
   - Password: redis123 (if you kept the default from docker-compose.yml)

RedisInsight makes it easy to visualize and explore the vector data you'll be working with in the notebooks.

## Technical Details

- Uses Java 21 for all Java code execution
- Built on the Jupyter Docker Stacks images
- Includes both Java and Python kernels
- Mounts local directories to persist your work