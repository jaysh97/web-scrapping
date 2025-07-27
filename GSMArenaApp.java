import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GSMArenaApp {

    private static final String MAKERS_PAGE_URL = "https://www.gsmarena.com/makers.php3";

    public static void main(String[] args) {
        MongoClient mongoClient = null;
        try {
            // Initialize MongoDB handler
            MongoDbHandler mongoDbHandler = new MongoDbHandler();
            mongoClient = mongoDbHandler.getMongoClient(); // Get the client to ensure it's managed for closing

            System.out.println("Starting GSM Arena scraping...");

            // Step 1: Get all manufacturer links from the makers page
            Elements manufacturerLinks = GSMArenaScraper.getManufacturerLinks(MAKERS_PAGE_URL);
            if (manufacturerLinks == null) {
                System.err.println("Could not retrieve manufacturer links. Exiting.");
                return;
            }

            // Iterate through each manufacturer
            for (Element manufacturerLink : manufacturerLinks) {
                String manufacturerName = manufacturerLink.text().trim();
                String manufacturerUrl = manufacturerLink.absUrl("href");
                System.out.println("\nScraping manufacturer: " + manufacturerName + " (" + manufacturerUrl + ")");

                // Step 2: Get all phone links for the current manufacturer
                Elements phoneLinks = GSMArenaScraper.getPhoneLinks(manufacturerUrl);
                if (phoneLinks == null) {
                    System.err.println("Skipping manufacturer " + manufacturerName + " due to link retrieval error.");
                    continue;
                }

                // Iterate through each phone model
                for (Element phoneLink : phoneLinks) {
                    String phoneName = phoneLink.text().trim();
                    String phoneUrl = phoneLink.absUrl("href");
                    System.out.println("  Scraping phone: " + phoneName + " (" + phoneUrl + ")");

                    // Step 3: Extract phone details
                    Map<String, String> rawPhoneDetails = GSMArenaScraper.getPhoneDetails(phoneUrl);
                    if (rawPhoneDetails != null) {
                        // Create a PhoneDetail object
                        PhoneDetail phoneDetail = new PhoneDetail(
                            manufacturerName,
                            phoneName,
                            phoneUrl,
                            rawPhoneDetails
                        );

                        // Insert into MongoDB
                        mongoDbHandler.insertPhoneDetail(phoneDetail);
                        System.out.println("    Inserted " + phoneName + " into MongoDB.");
                    } else {
                        System.err.println("    Failed to extract details for " + phoneName);
                    }

                    // Implement a delay to avoid overwhelming the server
                    try {
                        TimeUnit.MILLISECONDS.sleep(500); // 0.5 second delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Scraping interrupted.");
                        return;
                    }
                }
            }
            System.out.println("\nScraping complete! 🎉");

        } catch (Exception e) {
            System.err.println("An error occurred during scraping: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("MongoDB connection closed.");
            }
        }
    }
}
