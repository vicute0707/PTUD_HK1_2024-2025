package component;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class ImageUploadUtil {
	private static final Logger LOGGER = Logger.getLogger(ImageUploadUtil.class.getName());
	private static final String IMAGE_DIRECTORY = "src/img_product";
	private static final String DEFAULT_IMAGE = "default-product.jpg";
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
	private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png" };

	/**
	 * Uploads a product image from a file path and returns the filename for
	 * storage. Creates a copy in the image directory with the product ID as the
	 * name.
	 */
	public static String uploadProductImage(String sourcePath, String productId) throws IOException {
		// Handle null or empty path
		if (sourcePath == null || sourcePath.trim().isEmpty()) {
			LOGGER.warning("No image path provided for product " + productId);
			return DEFAULT_IMAGE;
		}

		try {
			File sourceFile = new File(sourcePath);
			validateImageFile(sourceFile);

			// Create the image directory if needed
			Files.createDirectories(Paths.get(IMAGE_DIRECTORY));

			// Generate new filename with original extension
			String extension = getFileExtension(sourceFile.getName());
			String newFileName = productId + extension;
			Path targetPath = Paths.get(IMAGE_DIRECTORY, newFileName);

			// Copy file to our image directory
			Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Successfully uploaded image for product " + productId);

			return newFileName;

		} catch (Exception e) {
			LOGGER.warning("Failed to upload image for product " + productId + ": " + e.getMessage());
			return DEFAULT_IMAGE;
		}
	}

	/**
	 * Updates a product's image, handling the old image cleanup. Returns the new
	 * filename or keeps the old one if update fails.
	 */
	public static String updateProductImage(String newImagePath, String productId, String oldFileName) {
		try {
			// If no new image provided, keep the existing one
			if (newImagePath == null || newImagePath.trim().isEmpty()) {
				return oldFileName != null ? oldFileName : DEFAULT_IMAGE;
			}

			// Delete old image if it exists and isn't the default
			if (oldFileName != null && !oldFileName.equals(DEFAULT_IMAGE)) {
				deleteProductImage(oldFileName);
			}

			// Upload new image
			return uploadProductImage(newImagePath, productId);

		} catch (Exception e) {
			LOGGER.severe("Error updating image for product " + productId + ": " + e.getMessage());
			return oldFileName != null ? oldFileName : DEFAULT_IMAGE;
		}
	}

	/**
	 * Validates that the image file meets size and format requirements.
	 */
	private static void validateImageFile(File file) throws IllegalArgumentException {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("File does not exist");
		}

		if (file.length() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds 5MB limit");
		}

		String extension = getFileExtension(file.getName()).toLowerCase();
		boolean isValidExtension = false;
		for (String allowedExt : ALLOWED_EXTENSIONS) {
			if (allowedExt.equalsIgnoreCase(extension)) {
				isValidExtension = true;
				break;
			}
		}

		if (!isValidExtension) {
			throw new IllegalArgumentException("Invalid file format. Allowed: JPG, JPEG, PNG");
		}
	}

	/**
	 * Safely loads and scales a product image for display in the UI.
	 */
	public static ImageIcon loadProductImage(String imageName, int width, int height) {
		if (imageName == null || imageName.trim().isEmpty()) {
			imageName = DEFAULT_IMAGE;
		}

		try {
			Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
			if (!Files.exists(imagePath)) {
				imagePath = Paths.get(IMAGE_DIRECTORY, DEFAULT_IMAGE);
			}

			ImageIcon imageIcon = new ImageIcon(imagePath.toString());
			Image image = imageIcon.getImage();
			Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(scaledImage);

		} catch (Exception e) {
			LOGGER.warning("Error loading image " + imageName + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Safely deletes a product image file.
	 */
	public static boolean deleteProductImage(String fileName) {
		try {
			if (fileName == null || fileName.trim().isEmpty() || fileName.equals(DEFAULT_IMAGE)) {
				return false;
			}

			Path imagePath = Paths.get(IMAGE_DIRECTORY, fileName);
			return Files.deleteIfExists(imagePath);

		} catch (IOException e) {
			LOGGER.warning("Failed to delete image " + fileName + ": " + e.getMessage());
			return false;
		}
	}

	private static String getFileExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		return lastDot > 0 ? fileName.substring(lastDot) : "";
	}

	/**
	 * Checks if an image exists in the product image directory.
	 */
	public static boolean imageExists(String imageName) {
		if (imageName == null || imageName.trim().isEmpty()) {
			return false;
		}
		return Files.exists(Paths.get(IMAGE_DIRECTORY, imageName));
	}
}