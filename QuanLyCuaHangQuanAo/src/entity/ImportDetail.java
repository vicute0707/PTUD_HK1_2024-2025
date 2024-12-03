package entity;

/**
 * Represents a detail record in an import order, including product information
 * for better historical tracking and data consistency.
 */
public class ImportDetail {
    // Primary identifiers
    private String importID;      // Reference to the parent import order
    private String variantID;     // Reference to the product variant
    
    // Transaction details
    private int quantity;         // Number of units imported
    private double price;         // Price per unit at time of import
    
    // Product information (stored for historical purposes)
    private String productName;   // Name of the product at time of import
    private String variantName;   // Combined size/color info of the variant
    private String productCode;   // Product's identifying code
    
    /**
     * Default constructor
     */
    public ImportDetail() {
    }

    /**
     * Full constructor with all fields
     */
    public ImportDetail(String importID, String variantID, int quantity, double price,
                       String productName, String variantName, String productCode) {
        this.importID = importID;
        this.variantID = variantID;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.variantName = variantName;
        this.productCode = productCode;
    }

    // Basic transaction getters and setters
    public String getImportID() {
        return importID;
    }

    public void setImportID(String importID) {
        this.importID = importID;
    }

    public String getVariantID() {
        return variantID;
    }

    public void setVariantID(String variantID) {
        this.variantID = variantID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Product information getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * Calculates the total amount for this detail line
     * @return The total cost (quantity * price)
     */
    public double getTotal() {
        return quantity * price;
    }

    /**
     * Returns a formatted string representation of the variant information
     * @return A string combining product name and variant details
     */
    public String getFullProductInfo() {
        return String.format("%s - %s", productName, variantName);
    }

    @Override
    public String toString() {
        return "ImportDetail{" +
               "productName='" + productName + '\'' +
               ", variantName='" + variantName + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               ", total=" + getTotal() +
               '}';
    }
}