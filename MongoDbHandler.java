import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Handles MongoDB database operations for storing phone details.
 */
public class MongoDbHandler {

    private static final String MONGODB_CONNECTION_STRING = "mongodb://localhost:27017"; // Adjust if your MongoDB is elsewhere
    private static final String DATABASE_NAME = "gsmarena";
    private static final String COLLECTION_NAME = "phones";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDbHandler() {
        try {
            this.mongoClient = MongoClients.create(MONGODB_CONNECTION_STRING);
            this.database = mongoClient.getDatabase(DATABASE_NAME);
            this.collection = database.getCollection(COLLECTION_NAME);
            System.out.println("Connected to MongoDB: " + DATABASE_NAME + "." + COLLECTION_NAME);
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
            // Re-throw or handle more gracefully in a real application
            throw new RuntimeException("Could not connect to MongoDB", e);
        }
    }

    /**
     * Inserts a PhoneDetail object into the MongoDB collection.
     *
     * @param phoneDetail The PhoneDetail object to insert.
     */
    public void insertPhoneDetail(PhoneDetail phoneDetail) {
        try {
            collection.insertOne(phoneDetail.toDocument());
        } catch (Exception e) {
            System.err.println("Error inserting phone detail for " + phoneDetail.getModel() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the MongoClient instance. Useful for ensuring the client is properly closed.
     * @return The MongoClient instance.
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    /**
     * Closes the MongoDB client connection.
     */
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}
```
```markdown:README.md
# GSM Arena Phone Scraper

This Java application uses Jsoup to scrape mobile phone details from GSM Arena and stores them in a MongoDB database. This version of the application has been refactored into multiple classes for better organization and readability.

## Features

* Scrapes manufacturer links from `https://www.gsmarena.com/makers.php3`.
* Navigates to each manufacturer's page to find all phone models.
* For each phone model, it extracts detailed specifications.
* Stores the extracted data into a MongoDB collection.

## Project Structure

* `GSMArenaApp.java`: The main entry point for the application. Orchestrates the scraping process.
* `GSMArenaScraper.java`: Contains static methods for all web scraping logic using Jsoup, including connecting to URLs and parsing HTML elements.
* `PhoneDetail.java`: A Plain Old Java Object (POJO) representing the data structure for a single phone's details. Includes a method to convert itself into a MongoDB `Document`.
* `MongoDbHandler.java`: Manages the connection to MongoDB and handles operations related to inserting `PhoneDetail` objects into the database.

## Prerequisites

* Java Development Kit (JDK) 8 or higher.
* Apache Maven or Gradle for dependency management.
* A running MongoDB instance (default connection string is `mongodb://localhost:27017`).

## Setup and Running

1.  **Clone the repository (if applicable) or create a new Maven/Gradle project.**

2.  **Add Dependencies:**
    If using Maven, add the following to your `pom.xml`:
    ```xml
    <dependencies>
        <!-- Jsoup for HTML parsing -->
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.17.2</version> <!-- Use the latest stable version -->
        </dependency>
        <!-- MongoDB Java Driver (Sync) -->
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>4.11.1</version> <!-- Use the latest stable version -->
        </dependency>
    </dependencies>
    ```

3.  **Configure MongoDB:**
    Ensure your MongoDB instance is running. By default, the application tries to connect to `mongodb://localhost:27017`. You can modify `MONGODB_CONNECTION_STRING` in `MongoDbHandler.java` if your MongoDB is hosted elsewhere or requires authentication.

4.  **Run the Scraper:**
    You can run the `main` method of the `GSMArenaApp` class from your IDE (e.g., IntelliJ IDEA, Eclipse) or from the command line using Maven:
    ```bash
    mvn clean install
    mvn exec:java -Dexec.mainClass="GSMArenaApp"
    ```

## Data Storage

The scraped phone details will be stored in a MongoDB database named `gsmarena` and a collection named `phones`. Each document in the `phones` collection will represent a single phone with various specifications extracted from GSM Arena.

## Important Notes

* **Rate Limiting:** The scraper includes a `TimeUnit.MILLISECONDS.sleep(500)` call to introduce a 0.5-second delay between requests for individual phone pages. This is crucial to avoid being blocked by the website. You might need to adjust this delay based on your network and the website's policies.
* **Website Structure Changes:** Web scraping relies on the HTML structure of the target website. If GSM Arena changes its page structure, the CSS selectors used in `GSMArenaScraper` methods might need to be updated.
* **Error Handling:** Basic error handling for network issues (e.g., `IOException`) and connection retries are included.
* **Ethical Considerations:** Always be mindful of the website's `robots.txt` file and terms of service when scraping. Excessive or aggressive scraping can lead to your IP being blocked.

Feel free to modify and extend these classes to add more features or adapt to specific requirements.
