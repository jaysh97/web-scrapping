# GSM Arena Phone Scraper

This Java application uses Jsoup to scrape mobile phone details from GSM Arena and stores them in a MongoDB database. The project is structured into multiple classes for improved organization and maintainability.

---

## Features

* **Manufacturer Scraping**: Automatically fetches links to all phone manufacturers listed on `https://www.gsmarena.com/makers.php3`.
* **Model Discovery**: Navigates to each manufacturer's page to discover and collect links for individual phone models.
* **Detailed Specification Extraction**: For every phone model, it extracts comprehensive specifications, including `phone_name`, `display_size`, `camera`, `battery`, and other key technical details.
* **MongoDB Integration**: Stores all extracted phone data as structured documents within a MongoDB collection.

---

## Project Structure

The application is divided into the following logical components:

* **`GSMArenaApp.java`**:
    * This is the main entry point of the application.
    * It orchestrates the overall scraping workflow, from fetching manufacturer links to initiating the detailed phone data extraction and storage.
* **`GSMArenaScraper.java`**:
    * A utility class dedicated to web scraping operations using Jsoup.
    * It handles HTTP connections, retrieves HTML documents, and provides methods to parse specific elements (manufacturer links, phone links, and detailed specifications) from the GSM Arena website.
* **`PhoneDetail.java`**:
    * A Plain Old Java Object (POJO) that defines the data structure for a single mobile phone's extracted details.
    * It encapsulates the manufacturer, model, URL, and a map of various specifications.
    * Includes a convenient method (`toDocument()`) to convert the `PhoneDetail` object into a `org.bson.Document` suitable for MongoDB insertion.
* **`MongoDbHandler.java`**:
    * Responsible for all MongoDB database interactions.
    * Manages the MongoDB client connection, selects the database and collection, and provides a method to insert `PhoneDetail` objects into the database.

---

## Prerequisites

Before running this application, ensure you have the following installed and configured:

* **Java Development Kit (JDK)**: Version 8 or higher.
* **Apache Maven** or **Gradle**: For managing project dependencies and building the application.
* **MongoDB Instance**: A running MongoDB server. By default, the application attempts to connect to a local instance at `mongodb://localhost:27017`. If your MongoDB is hosted elsewhere (e.g., MongoDB Atlas) or requires authentication, you will need to update the `MONGODB_CONNECTION_STRING` in `MongoDbHandler.java`.

---

## Setup and Running

Follow these steps to set up and run the scraper:

1.  **Clone the Repository**:
    If you haven't already, clone this repository to your local machine:
    ```bash
    git clone [Your Repository URL]
    cd [your-repo-name]
    ```
    (Replace `[Your Repository URL]` and `[your-repo-name]` with your actual repository details.)

2.  **Add Dependencies**:
    If you're using Maven, open your `pom.xml` file and add the following dependencies within the `<dependencies>` section:
    ```xml
    <dependencies>
        <!-- Jsoup for HTML parsing -->
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.17.2</version> <!-- It's good practice to check for the latest stable version -->
        </dependency>
        <!-- MongoDB Java Driver (Sync) -->
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>4.11.1</version> <!-- It's good practice to check for the latest stable version -->
        </dependency>
    </dependencies>
    ```
    If you're using Gradle, add similar dependencies to your `build.gradle` file.

3.  **Configure MongoDB Connection (if necessary)**:
    Open `src/main/java/MongoDbHandler.java` and adjust the `MONGODB_CONNECTION_STRING` if your MongoDB instance is not running locally on the default port.

4.  **Run the Scraper**:
    You can run the application directly from your IDE (e.g., IntelliJ IDEA, Eclipse) by executing the `main` method in `GSMArenaApp.java`.

    Alternatively, you can run it from the command line using Maven:
    ```bash
    # Compile the project
    mvn clean install

    # Run the main application class
    mvn exec:java -Dexec.mainClass="GSMArenaApp"
    ```

---

## Data Storage

Upon successful execution, the scraped phone details will be stored in your MongoDB instance:

* **Database Name**: `gsmarena`
* **Collection Name**: `phones`

Each document in the `phones` collection will represent a single phone, containing fields for `manufacturer`, `model`, `url`, and an embedded sub-document named `specifications` that holds all the extracted key-value pairs of phone details.

---

## Important Considerations

* **Rate Limiting**: The scraper intentionally includes a `0.5-second (500 milliseconds)` delay between requests for individual phone pages. This is a crucial measure to avoid overwhelming the GSM Arena server and to prevent your IP address from being blocked. You may need to adjust this delay based on your network conditions and the website's policies.
* **Website Structure Changes**: Web scraping solutions are inherently dependent on the HTML structure of the target website. If GSM Arena updates its page layouts or element IDs/classes, the CSS selectors used in `GSMArenaScraper.java` (e.g., `div.makers ul li a`, `div.section-body ul li a`, `h1.specs-phone-name`, `div#specs-list table`, etc.) may need to be updated to accurately extract data.
* **Error Handling**: Basic error handling for network connection issues (`IOException`) and retry mechanisms are implemented to make the scraping process more resilient.
* **Ethical Scraping**: Always respect the website's `robots.txt` file and its terms of service. Avoid aggressive scraping that could negatively impact the website's performance or violate their policies. This scraper is provided for educational purposes and responsible use is encouraged.

---

Feel free to contribute, report issues, or suggest improvements!
