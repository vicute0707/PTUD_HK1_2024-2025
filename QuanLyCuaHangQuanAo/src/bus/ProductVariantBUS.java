package bus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dao.ProductVariantDAO;
import entity.ProductVariant;

public class ProductVariantBUS {
	private static final Logger LOGGER = Logger.getLogger(ProductVariantBUS.class.getName());
	private ProductVariantDAO variantDAO;
    private List<ProductVariant> variants;


	public ProductVariantBUS() {
		variantDAO = new ProductVariantDAO();
        variants = new ArrayList<>();

	}

	public List<ProductVariant> getAllVariantsByProductID(String productID) {
		try {
			validateProductID(productID);
			return variantDAO.getAllVariantsByProductID(productID);
		} catch (Exception e) {
			LOGGER.severe("Error retrieving variants: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public boolean addVariant(ProductVariant variant) {
	    try {
	        validateVariant(variant);
	        if (variant.getVariantID() == null || variant.getVariantID().isEmpty()) {
	            variant.setVariantID(variantDAO.generateNewVariantID(variant.getProductID()));
	        }
	        boolean result = variantDAO.addVariant(variant);
	        if (result) {
	            variants.add(variant);
	            LOGGER.info("Added variant successfully: " + variant.getVariantID());
	        }
	        return result;
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Error adding variant: " + e.getMessage(), e);
	        return false;
	    }
	}

	public boolean updateVariant(ProductVariant variant) {
		try {
			validateVariant(variant);
			return variantDAO.updateVariant(variant);
		} catch (Exception e) {
			LOGGER.severe("Error updating variant: " + e.getMessage());
			return false;
		}
	}

	public boolean deleteVariant(String variantID, String productID) {
		try {
			validateVariantID(variantID);
			validateProductID(productID);
			return variantDAO.deleteVariant(variantID, productID);
		} catch (Exception e) {
			LOGGER.severe("Error deleting variant: " + e.getMessage());
			return false;
		}
	}

	private void validateVariant(ProductVariant variant) {
		if (variant == null) {
			throw new IllegalArgumentException("Variant cannot be null");
		}
		validateProductID(variant.getProductID());
		if (variant.getSize() == null || variant.getSize().trim().isEmpty()) {
			throw new IllegalArgumentException("Size cannot be empty");
		}
		if (variant.getColor() == null || variant.getColor().trim().isEmpty()) {
			throw new IllegalArgumentException("Color cannot be empty");
		}
		
	
	}

	private void validateProductID(String productID) {
		if (productID == null || productID.trim().isEmpty()) {
			throw new IllegalArgumentException("Product ID cannot be empty");
		}
	}

	private void validateVariantID(String variantID) {
		if (variantID == null || variantID.trim().isEmpty()) {
			throw new IllegalArgumentException("Variant ID cannot be empty");
		}
	}
}