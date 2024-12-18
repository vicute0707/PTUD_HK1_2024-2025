package entity;

public class ProductVariant {
	private String variantID;
	private String productID;
	private String size;
	private String color;
	private int quantity;

	// Getters and setters
	public String getVariantID() {
		return variantID;
	}

	public void setVariantID(String variantID) {
		this.variantID = variantID;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


}
