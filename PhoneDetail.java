import java.util.HashMap;
import java.util.Map;

/**
 * Represents the detailed specifications of a mobile phone.
 */
public class PhoneDetail {
    private String manufacturer;
    private String model;
    private String url;
    private Map<String, String> specifications; // To store various specs as key-value pairs

    public PhoneDetail(String manufacturer, String model, String url, Map<String, String> specifications) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.url = url;
        // Create a new HashMap to ensure independence from the original map
        this.specifications = new HashMap<>(specifications);
    }

    // Getters
    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getSpecifications() {
        // Return a copy to prevent external modification of the internal map
        return new HashMap<>(specifications);
    }

    /**
     * Converts the PhoneDetail object into a MongoDB Document.
     *
     * @return A org.bson.Document representation of the phone details.
     */
    public org.bson.Document toDocument() {
        org.bson.Document doc = new org.bson.Document();
        doc.append("manufacturer", this.manufacturer);
        doc.append("model", this.model);
        doc.append("url", this.url);
        doc.append("specifications", new org.bson.Document(this.specifications)); // Embed specs as a sub-document
        return doc;
    }

    @Override
    public String toString() {
        return "PhoneDetail{" +
                "manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", url='" + url + '\'' +
                ", specifications=" + specifications +
                '}';
    }
}
