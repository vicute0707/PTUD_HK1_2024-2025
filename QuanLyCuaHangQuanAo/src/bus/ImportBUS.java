package bus;

import dao.ImportDAO;
import dao.ImportDetailDAO;
import dao.ProductVariantDAO;
import dao.SanPhamDAO;
import dao.UserDAO;
import entity.Import;
import entity.ImportDetail;
import entity.ProductVariant;
import entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImportBUS {
	private static final Logger LOGGER = Logger.getLogger(ImportBUS.class.getName());
	private ImportDAO importDAO;
	private ProductVariantDAO variantDAO;
	private ImportDetailDAO importDetailDAO;
	private UserDAO userDAO;

	public ImportBUS() {
		userDAO = new UserDAO();
		importDAO = new ImportDAO();
		variantDAO = new ProductVariantDAO();
		importDetailDAO = new ImportDetailDAO();
	}

	/**
	 * Retrieves detailed information about an import order, including all
	 * associated products and their variants. This method combines data from
	 * multiple sources to provide a complete view of the import details.
	 *
	 * @param importId The unique identifier of the import order
	 * @return List of ImportDetail objects containing complete product information
	 * @throws Exception if the import ID is invalid or if there's a database error
	 */
	public List<ImportDetail> getImportDetailsByImportId(String importId) throws Exception {
		// Validate import ID
		if (importId == null || importId.trim().isEmpty()) {
			throw new Exception("Mã phiếu nhập không được để trống");
		}

		try {
			// First, verify that the import exists
			Import importOrder = importDAO.getImportById(importId);
			if (importOrder == null) {
				throw new Exception("Không tìm thấy phiếu nhập với mã: " + importId);
			}

			// Get the basic import details
			List<ImportDetail> details = importDetailDAO.getImportDetailsByImportID(importId);

			// If no details found, return empty list
			if (details.isEmpty()) {
				LOGGER.warning("No details found for import ID: " + importId);
				return new ArrayList<>();
			}

			// Enhance each detail with product and variant information
			for (ImportDetail detail : details) {
				enrichDetailWithProductInfo(detail);
			}

			LOGGER.info("Successfully retrieved and enriched " + details.size() + " details for import " + importId);
			return details;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error retrieving import details for " + importId, e);
			throw new Exception("Lỗi khi lấy chi tiết phiếu nhập: " + e.getMessage());
		}
	}

	/**
	 * Enriches an ImportDetail object with complete product and variant information
	 * by querying the product variant database.
	 *
	 * @param detail The ImportDetail object to enrich
	 * @throws Exception if there's an error retrieving product information
	 */
	private void enrichDetailWithProductInfo(ImportDetail detail) throws Exception {
		ProductVariant variant = variantDAO.getVariantByID(detail.getVariantID());
		if (variant == null) {
			LOGGER.warning("Variant not found for ID: " + detail.getVariantID());
			// Set placeholder values if variant not found
			detail.setProductName("Unknown Product");
			detail.setVariantName("Unknown Variant");
			return;
		}
		SanPhamDAO sanPhamDAO = new SanPhamDAO();
		// Update detail with product and variant information
		detail.setProductName(sanPhamDAO.getSanPhamByID(variant.getProductID()).getTenSP());
		detail.setVariantID(variant.getVariantID());
		detail.setProductCode(variant.getProductID());
	}

	/**
	 * Calculates the subtotal for a specific import detail
	 *
	 * @param detail The import detail to calculate
	 * @return The subtotal amount
	 */
	public double calculateDetailSubtotal(ImportDetail detail) {
		return detail.getQuantity() * detail.getPrice();
	}

	/**
	 * Updates the quantity for an existing import detail
	 *
	 * @param importId    The ID of the import order
	 * @param variantId   The ID of the product variant
	 * @param newQuantity The new quantity to set
	 * @return true if the update was successful
	 * @throws Exception if the update fails or validation fails
	 */
	public boolean updateImportDetailQuantity(String importId, String variantId, int newQuantity) throws Exception {
		// Validate inputs
		if (newQuantity <= 0) {
			throw new Exception("Số lượng phải lớn hơn 0");
		}

		// Get existing detail
		List<ImportDetail> details = importDetailDAO.getImportDetailsByImportID(importId);
		ImportDetail targetDetail = details.stream().filter(d -> d.getVariantID().equals(variantId)).findFirst()
				.orElseThrow(() -> new Exception("Không tìm thấy chi tiết phiếu nhập"));

		// Update quantity
		targetDetail.setQuantity(newQuantity);

		// Update in database
		return importDetailDAO.updateImportDetail(targetDetail);
	}

	/**
	 * Validates and saves a list of import details
	 *
	 * @param importId The ID of the import order
	 * @param details  The list of details to save
	 * @return true if all details were saved successfully
	 * @throws Exception if validation fails or save operation fails
	 */
	public boolean saveImportDetails(String importId, List<ImportDetail> details) throws Exception {
		// Validate import existence
		Import importOrder = importDAO.getImportById(importId);
		if (importOrder == null) {
			throw new Exception("Không tìm thấy phiếu nhập");
		}

		// Validate details
		for (ImportDetail detail : details) {
			if (detail.getQuantity() <= 0) {
				throw new Exception("Số lượng phải lớn hơn 0");
			}
			if (detail.getPrice() <= 0) {
				throw new Exception("Đơn giá phải lớn hơn 0");
			}
			if (!checkVariantAvailability(detail.getVariantID(), detail.getQuantity())) {
				throw new Exception("Sản phẩm không tồn tại: " + detail.getVariantID());
			}
		}

		// Calculate new total amount
		double totalAmount = calculateTotalAmount(details);

		// Update import order total
		importOrder.setTotalAmount(totalAmount);

		// Save all details
		boolean success = true;
		for (ImportDetail detail : details) {
			success &= importDetailDAO.addImportDetail(detail);
		}

		return success;
	}

	/**
	 * Create a new import order with validation
	 */
	public boolean createImport(Import importOrder) throws Exception {
		validateImportOrder(importOrder);

		// Generate new import ID if not provided
		if (importOrder.getImportID() == null || importOrder.getImportID().trim().isEmpty()) {
			importOrder.setImportID(importDAO.generateNewImportID());
		}

		// Set import date if not provided
		if (importOrder.getImportDate() == null) {
			importOrder.setImportDate(new Date());
		}

		// Update product variants and validate availability
		for (ImportDetail detail : importOrder.getImportDetails()) {
			ProductVariant variant = variantDAO.getVariantByID(detail.getVariantID());
			if (variant == null) {
				throw new Exception("Phân loại sản phẩm không tồn tại: " + detail.getVariantID());
			}
		}

		// Create JSON details
		String detailsJson = generateDetailsJson(importOrder.getImportDetails());
		importOrder.setDetails(detailsJson);

		return importDAO.addImport(importOrder);
	}

	private String generateDetailsJson(List<ImportDetail> details) {
		JSONArray detailsArray = new JSONArray();

		for (ImportDetail detail : details) {
			JSONObject detailObj = new JSONObject();
			detailObj.put("variantID", detail.getVariantID());
			detailObj.put("quantity", detail.getQuantity());
			detailObj.put("price", detail.getPrice());
			detailsArray.put(detailObj);
		}

		return detailsArray.toString();
	}

	public List<User> getAllEmployees() {
		try {
			return userDAO.getAllUsers();
		} catch (Exception e) {
			LOGGER.severe("Error getting employees: " + e.getMessage());
			return new ArrayList<>(); // Return empty list if error
		}
	}

	/**
	 * Get all imports
	 */
	public List<Import> getAllImports() {
		return importDAO.getAllImports();
	}

	/**
	 * Get import by ID
	 */
	public Import getImportById(String importId) throws Exception {
		if (importId == null || importId.trim().isEmpty()) {
			throw new Exception("Mã đơn nhập không được để trống");
		}
		return importDAO.getImportById(importId);
	}

	/**
	 * Calculate total amount for import details
	 */
	public double calculateTotalAmount(List<ImportDetail> details) {
		return details.stream().mapToDouble(detail -> detail.getPrice() * detail.getQuantity()).sum();
	}

	/**
	 * Validate import order data
	 */
	private void validateImportOrder(Import importOrder) throws Exception {
		if (importOrder == null) {
			throw new Exception("Đơn nhập không được null");
		}

		// Validate supplier
		if (importOrder.getSupplier() == null || importOrder.getSupplier().trim().isEmpty()) {
			throw new Exception("Nhà cung cấp không được để trống");
		}

		// Validate staff
		if (importOrder.getStaff() == null || importOrder.getStaff().trim().isEmpty()) {
			throw new Exception("Nhân viên không được để trống");
		}

		// Validate import details
		if (importOrder.getImportDetails() == null || importOrder.getImportDetails().isEmpty()) {
			throw new Exception("Đơn nhập phải có ít nhất một sản phẩm");
		}

		// Validate each import detail
		for (ImportDetail detail : importOrder.getImportDetails()) {
			if (detail.getPrice() <= 0) {
				throw new Exception("Giá nhập phải lớn hơn 0");
			}
			if (detail.getQuantity() <= 0) {
				throw new Exception("Số lượng phải lớn hơn 0");
			}
		}

		// Validate total amount
		if (importOrder.getTotalAmount() <= 0) {
			throw new Exception("Tổng tiền phải lớn hơn 0");
		}
	}

	/**
	 * Generate a summary of import details for the details field
	 */
	private String generateDetailsSummary(List<ImportDetail> details) {
		StringBuilder summary = new StringBuilder();
		for (ImportDetail detail : details) {
			if (summary.length() > 0) {
				summary.append("; ");
			}
			summary.append(String.format("%s - SL: %d", detail.getVariantID(), detail.getQuantity()));
		}
		return summary.toString();
	}

	/**
	 * Check variant availability
	 */
	public boolean checkVariantAvailability(String variantId, int quantity) {
		try {
			ProductVariant variant = variantDAO.getVariantByID(variantId);
			return variant != null;
		} catch (Exception e) {
			LOGGER.severe("Error checking variant availability: " + e.getMessage());
			return false;
		}
	}

	public String generateNewImportID() {
		String lastID = importDAO.generateNewImportID(); // Get last ID from DAO
		return lastID;
	}

	public boolean deleteImport(String importId) throws Exception {
		if (importId == null || importId.trim().isEmpty()) {
			throw new Exception("Mã đơn nhập không được để trống");
		}
		return importDAO.deleteImport(importId);
	}
}