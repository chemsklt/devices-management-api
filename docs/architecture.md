# Device Management API Architecture

This Document describe the architecture of the Device Rest API.

## Architecture Style

The application follow the following structure
Controller -> Service -> Repository -> Database

## Layers

### Controller 
Handles HTTP requests and responses.

### Service 
Contains business logic and domain validations.

### Repository
Handles database persistence.

### Domain 
Contains core entities and enums.
