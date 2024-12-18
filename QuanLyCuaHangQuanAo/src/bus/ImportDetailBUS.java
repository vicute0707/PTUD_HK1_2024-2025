package bus;

import dao.ImportDetailDAO;
import entity.ImportDetail;
import java.util.List;
import java.util.logging.Logger;

public class ImportDetailBUS {
	private static final Logger LOGGER = Logger.getLogger(ImportDetailBUS.class.getName());
	private ImportDetailDAO importDetailDAO;

	public ImportDetailBUS() {
		importDetailDAO = new ImportDetailDAO();
	}

	public List<ImportDetail> getImportDetailsByImportId(String importId) {
		try {
			if (importId == null || importId.trim().isEmpty()) {
				throw new IllegalArgumentException("Import ID cannot be empty");
			}

			List<ImportDetail> details = importDetailDAO.getImportDetailsByImportID(importId);

			// Validate the data
			if (details == null) {
				throw new Exception("Failed to retrieve import details");
			}

			// Log success
			LOGGER.info(String.format("Successfully retrieved %d details for import %s", details.size(), importId));

			return details;

		} catch (Exception e) {
			LOGGER.severe("Error retrieving import details: " + e.getMessage());
			throw new RuntimeException("Error retrieving import details", e);
		}
	}

	// Additional helper method to calculate total for a set of details
	public double calculateTotal(List<ImportDetail> details) {
		return details.stream().mapToDouble(detail -> detail.getQuantity() * detail.getPrice()).sum();
	}

	// Method to validate import details
	public boolean validateDetails(List<ImportDetail> details) {
		if (details == null || details.isEmpty()) {
			return false;
		}

		for (ImportDetail detail : details) {
			if (detail.getQuantity() <= 0 || detail.getPrice() <= 0) {
				return false;
			}
			if (detail.getVariantID() == null || detail.getVariantID().trim().isEmpty()) {
				return false;
			}
		}

		return true;
	}
	
	
	
}