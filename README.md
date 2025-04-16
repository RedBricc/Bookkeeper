# Bookkeeper
[Vallterra Wiki](https://vallterra.wiki) is a wiki for a homebrew Dungeons & Dragons campaign setting.
**Bookkeeper** is an admin interface for managing content for the wiki.
It simplifies administrative tasks by providing a clean, intuitive interface for updating and managing wiki content.
It also provides tools for Dungeon Masters to manage their campaigns.

## Key Features
- **User administration** - Manage users, their roles, and access to content
- **Content management** - Create, update, and delete wiki content
- **Overview** - Get an overview of all content related to specific topics
- **DM tools** - Tools for Dungeon Masters to manage their campaigns

## Prerequisites
The project was created in IntelliJ IDEA but can be imported into the IDE of your choice.
To run the project you will need:
- **Java 21:** Required to run the application
- **Docker:** Needed to initialize the development database

## Setup
1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/bookkeeper.git
    cd bookkeeper
    ```
2. **Initialize the development database:**
    ```bash
    docker-compose up
    ```
3. **Start the application:**
    ```bash
    ./gradlew bootRun
    ```
