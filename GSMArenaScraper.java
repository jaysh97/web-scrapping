import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for scraping data from GSMArena.com.
 * Handles HTTP connections and parsing HTML elements.
 */
public class GSMArenaScraper {

    // Base URL for GSM Arena (already defined in GSMArenaApp, but good to keep here for clarity if methods are standalone)
    private static final String BASE_URL = "https://www.gsmarena.com/";

    /**
     * Connects to a URL and returns the Jsoup Document.
     * Includes basic error handling and retries.
     *
     * @param url The URL to connect to.
     * @return The Jsoup Document, or null if connection fails after retries.
     */
    public static Document connectAndGetDocument(String url) {
        int maxRetries = 3;
        int retryDelayMs = 1000;
        for (int i = 0; i < maxRetries; i++) {
            try {
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .timeout(10000) // 10 seconds timeout
                        .get();
            } catch (IOException e) {
                System.err.println("Error connecting to " + url + " (Attempt " + (i + 1) + "/" + maxRetries + "): " + e.getMessage());
                if (i < maxRetries - 1) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryDelayMs * (i + 1)); // Exponential backoff
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        System.err.println("Connection retry interrupted.");
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts manufacturer links from the GSM Arena makers page.
     *
     * @param url The URL of the makers page.
     * @return A list of Elements (<a> tags) representing manufacturer links, or null if the page cannot be parsed.
     */
    public static Elements getManufacturerLinks(String url) {
        Document doc = connectAndGetDocument(url);
        if (doc == null) {
            return null;
        }
        // The manufacturer links are typically within a div with class 'makers'
        // and are <a> tags within <li> elements.
        return doc.select("div.makers ul li a");
    }

    /**
     * Extracts phone links from a manufacturer's page.
     *
     * @param url The URL of the manufacturer's page.
     * @return A list of Elements (<a> tags) representing phone links, or null if the page cannot be parsed.
     */
    public static Elements getPhoneLinks(String url) {
        Document doc = connectAndGetDocument(url);
        if (doc == null) {
            return null;
        }
        // Phone links on a manufacturer page are usually within a div with class 'section-body'
        // and are <a> tags.
        return doc.select("div.section-body ul li a");
    }

    /**
     * Extracts details from a single phone's detail page.
     *
     * @param url The URL of the phone's detail page.
     * @return A Map containing key-value pairs of phone specifications, or null if the page cannot be parsed.
     */
    public static Map<String, String> getPhoneDetails(String url) {
        Document doc = connectAndGetDocument(url);
        if (doc == null) {
            return null;
        }

        Map<String, String> details = new HashMap<>();

        // Extract phone name (title of the page, often in h1.specs-phone-name)
        Element titleElement = doc.selectFirst("h1.specs-phone-name");
        if (titleElement != null) {
            details.put("phone_name", titleElement.text().trim());
        }

        // Extract key specifications from the 'specs-spotlight' div
        Elements spotlightItems = doc.select("div#specs-list div.specs-spotlight ul li");
        for (Element item : spotlightItems) {
            String label = item.selectFirst("strong") != null ? item.selectFirst("strong").text().replace(":", "").trim() : "";
            String value = item.selectFirst("span") != null ? item.selectFirst("span").text().trim() : "";
            if (!label.isEmpty()) {
                details.put(label.toLowerCase().replace(" ", "_"), value);
            }
        }

        // Extract details from the main specification tables
        Elements specSections = doc.select("div#specs-list table");
        for (Element section : specSections) {
            Elements rows = section.select("tr");
            String currentCategory = "";
            for (Element row : rows) {
                Element th = row.selectFirst("th");
                if (th != null) {
                    currentCategory = th.text().trim();
                } else {
                    Element tdLabel = row.selectFirst("td.nfo"); // Label (e.g., "Display Type")
                    Element tdValue = row.selectFirst("td.vcenter"); // Value (e.g., "AMOLED")

                    if (tdLabel != null && tdValue != null) {
                        String label = tdLabel.text().trim();
                        String value = tdValue.text().trim();
                        // Combine category and label for unique keys
                        String key = (currentCategory.isEmpty() ? "" : currentCategory.toLowerCase().replace(" ", "_") + "_") +
                                     label.toLowerCase().replace(" ", "_");
                        details.put(key, value);
                    }
                }
            }
        }

        return details;
    }
}
```
