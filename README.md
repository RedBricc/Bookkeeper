# Bookkeeper
An admin interface for managing content for the [Vallterra Wiki](https://vallterra.wiki). 

## Prerequisites
The project was created in IntelliJ IDEA but can be imported into the IDE of your choice.
To run the project you will need:
- Java 21
- Docker

## Setup
1. Clone the repository
2. Initialize the development database
```bash
docker-compose up
```
3. Start the application `gradlew bootRun`

## Project Structure
The project is following the standard [Maven project layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).