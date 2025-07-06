# Stock Manager Application

A Spring Boot application for managing stock items from Excel files and generating PDF reports.

## Features

- Upload Excel files containing stock information
- Process and display stock items sorted by quantity
- Generate PDF reports from processed data
- Batch processing of multiple Excel files

## Technical Stack

- Java 17
- Spring Boot 3.2.0
- Thymeleaf for server-side templating
- Apache POI for Excel processing
- iText for PDF generation
- Bootstrap 5 for UI

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

4. Access the application at http://localhost:12000

## Excel File Format

The application expects Excel files with the following columns:

- Item name*
- Current stock quantity
- Base Unit (x)
- Secondary Unit (y)
- Conversion Rate (n)

## Usage

### Single File Processing

1. Go to the home page
2. Upload an Excel file using the file upload form
3. View the processed stock items
4. Generate a PDF report

### Batch Processing

1. Go to the home page
2. Enter a directory path containing Excel files
3. Click "Process Batch"
4. View the batch processing results

## Configuration

The application can be configured using the `application.properties` file:

- `server.port`: The port the server runs on (default: 12000)
- `app.upload.dir`: Directory for uploaded files
- `app.output.dir`: Directory for generated PDF files

## License

This project is licensed under the MIT License - see the LICENSE file for details.