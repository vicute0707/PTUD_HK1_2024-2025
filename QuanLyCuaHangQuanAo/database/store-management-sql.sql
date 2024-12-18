DROP DATABASE IF EXISTS `store_management`;
CREATE DATABASE `store_management`;
USE `store_management`;
-- Create tables
CREATE TABLE `category` (
  `categoryID` varchar(36) NOT NULL PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `supplier` (
  `supplierID` varchar(36) NOT NULL PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `address` text DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `permission` (
  `permissionID` varchar(36) NOT NULL PRIMARY KEY,
  `name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `userrole` (
  `roleID` varchar(36) NOT NULL PRIMARY KEY,
  `roleName` varchar(50) NOT NULL,
  `permissions` JSON DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `user` (
  `userID` varchar(36) NOT NULL PRIMARY KEY,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `fullName` varchar(100) NOT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `birthDate` date DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` varchar(36) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  FOREIGN KEY (`role`) REFERENCES `userrole` (`roleID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `product` (
  `productID` varchar(36) NOT NULL PRIMARY KEY,
  `name` varchar(200) NOT NULL,
  `category` varchar(36) DEFAULT NULL,
  `stockQuantity` int(11) DEFAULT 0,
  `importPrice` decimal(10,2) DEFAULT NULL,
  `sellPrice` decimal(10,2) DEFAULT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `imagePath` varchar(255) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  FOREIGN KEY (`category`) REFERENCES `category` (`categoryID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `productvariant` (
  `variantID` varchar(36) NOT NULL PRIMARY KEY,
  `productID` varchar(36) DEFAULT NULL,
  `size` varchar(10) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `quantity` int(11) DEFAULT 0,
  `price` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  FOREIGN KEY (`productID`) REFERENCES `product` (`productID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `import` (
  `importID` varchar(36) NOT NULL PRIMARY KEY,
  `importDate` datetime NOT NULL,
  `supplier` varchar(36) DEFAULT NULL,
  `staff` varchar(36) DEFAULT NULL,
  `totalAmount` decimal(10,2) DEFAULT 0.00,
  `details` JSON DEFAULT NULL,
  FOREIGN KEY (`supplier`) REFERENCES `supplier` (`supplierID`),
  FOREIGN KEY (`staff`) REFERENCES `user` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `importdetail` (
  `importID` varchar(36) NOT NULL,
  `variantID` varchar(36) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`importID`, `variantID`),
  FOREIGN KEY (`importID`) REFERENCES `import` (`importID`),
  FOREIGN KEY (`variantID`) REFERENCES `productvariant` (`variantID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `order` (
  `orderID` varchar(36) NOT NULL PRIMARY KEY,
  `orderDate` datetime NOT NULL,
  `staff` varchar(36) DEFAULT NULL,
  `totalAmount` decimal(10,2) DEFAULT 0.00,
  `paymentMethod` varchar(20) DEFAULT NULL,
  `details` JSON DEFAULT NULL,
  FOREIGN KEY (`staff`) REFERENCES `user` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `orderdetail` (
  `orderID` varchar(36) NOT NULL,
  `variantID` varchar(36) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`orderID`, `variantID`),
  FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  FOREIGN KEY (`variantID`) REFERENCES `productvariant` (`variantID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE password_reset_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci,
    reset_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    old_password VARCHAR(255),
    new_password VARCHAR(255),
    reset_by VARCHAR(50),
    status VARCHAR(100) DEFAULT 'SUCCESS',
    ip_address VARCHAR(50),
    FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
-- Insert sample data
INSERT INTO `category` VALUES
('CAT001', 'Áo', 'Các loại áo'),
('CAT002', 'Quần', 'Các loại quần'),
('CAT003', 'Váy', 'Các loại váy');

INSERT INTO `supplier` VALUES
('SUP001', 'Công ty may mặc ABC', '123 Nguyễn Văn Cừ, Q.5, TP.HCM', 'abc@gmail.com', '0987654321'),
('SUP002', 'Xưởng may XYZ', '456 Lê Hồng Phong, Q.10, TP.HCM', 'xyz@gmail.com', '0987654322'),
('SUP003', 'Nhà máy dệt may DEF', '789 Lý Thường Kiệt, Q.11, TP.HCM', 'def@gmail.com', '0987654323');

INSERT INTO `permission` VALUES
('PERM001', 'Quản lý người dùng', 'Quyền quản lý tài khoản người dùng'),
('PERM002', 'Quản lý sản phẩm', 'Quyền quản lý danh mục và sản phẩm'),
('PERM003', 'Quản lý đơn hàng', 'Quyền quản lý đơn hàng và bán hàng');

INSERT INTO `userrole` VALUES
('ROLE001', 'Admin', '["PERM001","PERM002","PERM003"]'),
('ROLE002', 'Nhân viên kho', '["PERM002"]'),
('ROLE003', 'Nhân viên bán hàng', '["PERM003"]');

INSERT INTO `user` VALUES
('USER001', 'admin', 'password', 'Quản trị viên', 'Nam', '1990-01-01', '0123456789', 'admin@gmail.com', 'ROLE001', 'active'),
('USER002', 'kho1', 'kho1', 'Nhân viên kho 1', 'Nam', '1995-02-02', '0123456788', 'kho1@gmail.com', 'ROLE002', 'active'),
('USER003', 'banhang1', 'banhang1', 'Nhân viên bán hàng 1', 'Nữ', '1998-03-03', '0123456787', 'banhang1@gmail.com', 'ROLE003', 'active');

INSERT INTO `product` VALUES
('PRD001', 'Áo thun nam', 'CAT001', 100, 80000.00, 150000.00, 'Nike', '/images/products/ao-thun-nam.jpg', 'active'),
('PRD002', 'Quần jean nữ', 'CAT002', 50, 150000.00, 300000.00, 'Levis', '/images/products/quan-jean-nu.jpg', 'active'),
('PRD003', 'Váy công sở', 'CAT003', 30, 200000.00, 400000.00, 'Zara', '/images/products/vay-cong-so.jpg', 'active');

INSERT INTO `productvariant` VALUES
('VAR001', 'PRD001', 'M', 'Đen', 30, 150000.00, 'active'),
('VAR002', 'PRD001', 'L', 'Đen', 20, 150000.00, 'active'),
('VAR003', 'PRD001', 'M', 'Trắng', 25, 150000.00, 'active'),
('VAR004', 'PRD002', '29', 'Xanh nhạt', 15, 300000.00, 'active'),
('VAR005', 'PRD002', '30', 'Xanh nhạt', 20, 300000.00, 'active'),
('VAR006', 'PRD003', 'S', 'Đen', 10, 400000.00, 'active'),
('VAR007', 'PRD003', 'M', 'Đen', 15, 400000.00, 'active');

-- Create triggers
DELIMITER $$
CREATE TRIGGER `CapNhatTongTienNhapHang` AFTER INSERT ON `importdetail` FOR EACH ROW
BEGIN
    UPDATE Import SET totalAmount = (
        SELECT SUM(quantity * price)
        FROM ImportDetail
        WHERE importID = NEW.importID
    )
    WHERE importID = NEW.importID;
    
    UPDATE ProductVariant SET
        quantity = quantity + NEW.quantity
    WHERE variantID = NEW.variantID;
END$$

CREATE TRIGGER `CapNhatTongTienDonHang` AFTER INSERT ON `orderdetail` FOR EACH ROW
BEGIN
    UPDATE `Order` SET totalAmount = (
        SELECT SUM(quantity * price)
        FROM OrderDetail
        WHERE orderID = NEW.orderID
    )
    WHERE orderID = NEW.orderID;
    
    UPDATE ProductVariant SET
        quantity = quantity - NEW.quantity
    WHERE variantID = NEW.variantID;
END$$

DELIMITER ;

COMMIT;
-- Category CRUD
DELIMITER $$
CREATE PROCEDURE sp_AddCategory(
    IN p_categoryID VARCHAR(36),
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    INSERT INTO category(categoryID, name, description)
    VALUES(p_categoryID, p_name, p_description);
END$$

CREATE PROCEDURE sp_UpdateCategory(
    IN p_categoryID VARCHAR(36),
    IN p_name VARCHAR(100),
    IN p_description TEXT
)
BEGIN
    UPDATE category 
    SET name = p_name, description = p_description
    WHERE categoryID = p_categoryID;
END$$

CREATE PROCEDURE sp_DeleteCategory(IN p_categoryID VARCHAR(36))
BEGIN
    DELETE FROM category WHERE categoryID = p_categoryID;
END$$

-- Product CRUD
CREATE PROCEDURE sp_AddProduct(
    IN p_productID VARCHAR(36),
    IN p_name VARCHAR(200),
    IN p_category VARCHAR(36),
    IN p_importPrice DECIMAL(10,2),
    IN p_sellPrice DECIMAL(10,2),
    IN p_brand VARCHAR(100),
    IN p_imagePath VARCHAR(255)
)
BEGIN
    INSERT INTO product(productID, name, category, importPrice, sellPrice, brand, imagePath, status)
    VALUES(p_productID, p_name, p_category, p_importPrice, p_sellPrice, p_brand, p_imagePath, 'active');
END$$

CREATE PROCEDURE sp_UpdateProduct(
    IN p_productID VARCHAR(36),
    IN p_name VARCHAR(200),
    IN p_category VARCHAR(36),
    IN p_importPrice DECIMAL(10,2),
    IN p_sellPrice DECIMAL(10,2),
    IN p_brand VARCHAR(100),
    IN p_imagePath VARCHAR(255),
    IN p_status VARCHAR(20)
)
BEGIN
    UPDATE product 
    SET name = p_name,
        category = p_category,
        importPrice = p_importPrice,
        sellPrice = p_sellPrice,
        brand = p_brand,
        imagePath = p_imagePath,
        status = p_status
    WHERE productID = p_productID;
END$$

CREATE PROCEDURE sp_DeleteProduct(IN p_productID VARCHAR(36))
BEGIN
    UPDATE product SET status = 'inactive' WHERE productID = p_productID;
END$$

-- Import CRUD
CREATE PROCEDURE sp_CreateImport(
    IN p_importID VARCHAR(36),
    IN p_supplier VARCHAR(36),
    IN p_staff VARCHAR(36),
    IN p_details JSON
)
BEGIN
    INSERT INTO import(importID, importDate, supplier, staff, totalAmount, details)
    VALUES(p_importID, NOW(), p_supplier, p_staff, 0, p_details);
END$$

CREATE PROCEDURE sp_AddImportDetail(
    IN p_importID VARCHAR(36),
    IN p_variantID VARCHAR(36),
    IN p_quantity INT,
    IN p_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO importdetail(importID, variantID, quantity, price)
    VALUES(p_importID, p_variantID, p_quantity, p_price);
END$$

-- Order CRUD
CREATE PROCEDURE sp_CreateOrder(
    IN p_orderID VARCHAR(36),
    IN p_staff VARCHAR(36),
    IN p_paymentMethod VARCHAR(20),
    IN p_details JSON
)
BEGIN
    INSERT INTO `order`(orderID, orderDate, staff, totalAmount, paymentMethod, details)
    VALUES(p_orderID, NOW(), p_staff, 0, p_paymentMethod, p_details);
END$$

CREATE PROCEDURE sp_AddOrderDetail(
    IN p_orderID VARCHAR(36),
    IN p_variantID VARCHAR(36),
    IN p_quantity INT,
    IN p_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO orderdetail(orderID, variantID, quantity, price)
    VALUES(p_orderID, p_variantID, p_quantity, p_price);
END$$

-- User CRUD
CREATE PROCEDURE sp_AddUser(
    IN p_userID VARCHAR(36),
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_fullName VARCHAR(100),
    IN p_gender VARCHAR(10),
    IN p_birthDate DATE,
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    IN p_role VARCHAR(36)
)
BEGIN
    INSERT INTO user(userID, username, password, fullName, gender, birthDate, phone, email, role, status)
    VALUES(p_userID, p_username, p_password, p_fullName, p_gender, p_birthDate, p_phone, p_email, p_role, 'active');
END$$

CREATE PROCEDURE sp_UpdateUser(
    IN p_userID VARCHAR(36),
    IN p_fullName VARCHAR(100),
    IN p_gender VARCHAR(10),
    IN p_birthDate DATE,
    IN p_phone VARCHAR(15),
    IN p_email VARCHAR(100),
    IN p_role VARCHAR(36),
    IN p_status VARCHAR(20)
)
BEGIN
    UPDATE user 
    SET fullName = p_fullName,
        gender = p_gender,
        birthDate = p_birthDate,
        phone = p_phone,
        email = p_email,
        role = p_role,
        status = p_status
    WHERE userID = p_userID;
END$$

CREATE PROCEDURE sp_DeleteUser(IN p_userID VARCHAR(36))
BEGIN
    UPDATE user SET status = 'inactive' WHERE userID = p_userID;
END$$

-- Product Variant CRUD
CREATE PROCEDURE sp_AddVariant(
    IN p_variantID VARCHAR(36),
    IN p_productID VARCHAR(36),
    IN p_size VARCHAR(10),
    IN p_color VARCHAR(50),
    IN p_quantity INT,
    IN p_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO productvariant(variantID, productID, size, color, quantity, price, status)
    VALUES(p_variantID, p_productID, p_size, p_color, p_quantity, p_price, 'active');
END$$

CREATE PROCEDURE sp_UpdateVariant(
    IN p_variantID VARCHAR(36),
    IN p_size VARCHAR(10),
    IN p_color VARCHAR(50),
    IN p_quantity INT,
    IN p_price DECIMAL(10,2),
    IN p_status VARCHAR(20)
)
BEGIN
    UPDATE productvariant 
    SET size = p_size,
        color = p_color,
        quantity = p_quantity,
        price = p_price,
        status = p_status
    WHERE variantID = p_variantID;
END$$

CREATE PROCEDURE sp_DeleteVariant(IN p_variantID VARCHAR(36))
BEGIN
    UPDATE productvariant SET status = 'inactive' WHERE variantID = p_variantID;
END$$

DELIMITER ;
DELIMITER $$
CREATE PROCEDURE sp_AddPermission(
    IN p_permissionID VARCHAR(36),
    IN p_name VARCHAR(50),
    IN p_description TEXT
)
BEGIN
    INSERT INTO permission(permissionID, name, description)
    VALUES(p_permissionID, p_name, p_description);
END$$
DELIMITER ;