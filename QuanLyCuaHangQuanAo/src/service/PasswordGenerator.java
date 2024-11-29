package service;

public class PasswordGenerator {
    // Các ký tự cho từng loại
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    public static String generateRandomPassword() {
        // Đảm bảo mật khẩu có độ dài tối thiểu 8 ký tự
        int length = 10;
        
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.security.SecureRandom(); // Sử dụng SecureRandom cho bảo mật
        
        // Đảm bảo mật khẩu có ít nhất:
        // 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length()))); // 1 chữ thường
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length()))); // 1 chữ hoa
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length()))); // 1 số
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length()))); // 1 ký tự đặc biệt
        
        // Tạo chuỗi bao gồm tất cả các ký tự có thể
        String allChars = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;
        
        // Thêm các ký tự ngẫu nhiên cho đến khi đủ độ dài
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Trộn các ký tự trong mật khẩu
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    // Kiểm tra độ mạnh của mật khẩu
    public static boolean isStrongPassword(String password) {
        return password.length() >= 8 && // Độ dài tối thiểu
               password.matches(".*[A-Z].*") && // Có chữ hoa
               password.matches(".*[a-z].*") && // Có chữ thường
               password.matches(".*\\d.*") && // Có số
               password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"); // Có ký tự đặc biệt
    }
    
    // Tạo mật khẩu dễ nhớ (tùy chọn)
    public static String generateMemorablePassword() {
        String[] adjectives = {"Happy", "Smart", "Bright", "Quick", "Cool"};
        String[] nouns = {"Tiger", "Eagle", "Lion", "Panda", "Bear"};
        String[] numbers = {"123", "456", "789", "321", "654"};
        String[] specials = {"!", "@", "#", "$", "%"};
        
        java.util.Random random = new java.security.SecureRandom();
        
        String password = adjectives[random.nextInt(adjectives.length)] +
                         nouns[random.nextInt(nouns.length)] +
                         numbers[random.nextInt(numbers.length)] +
                         specials[random.nextInt(specials.length)];
        
        return password;
    }
}