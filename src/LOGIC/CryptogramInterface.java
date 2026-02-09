package LOGIC;

public interface CryptogramInterface {
    default String caesarEncrypt(String text, int shift) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 32 && c <= 126) {
                encrypted.append((char) (32 + (c - 32 + shift) % 95));
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }
    default String caesarDecrypt(String text, int shift) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 32 && c <= 126) {
                decrypted.append((char) (32 + (c - 32 - shift + 95) % 95));
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
    }
}
