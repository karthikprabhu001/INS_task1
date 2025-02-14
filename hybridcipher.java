import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class hybridcipher {
    private static final int RAILS = 3;
    // AES Encryption
    public static String aesEncrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES Decryption
    public static String aesDecrypt(String ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes);
    }

    // Rail Fence Cipher Encryption
    public static String railFenceEncrypt(String text, int rails) {
        if (rails <= 1)
            return text;

        StringBuilder[] rail = new StringBuilder[rails];
        for (int i = 0; i < rails; i++)
            rail[i] = new StringBuilder();

        int dir = 1, row = 0;
        for (char c : text.toCharArray()) {
            rail[row].append(c);
            row += dir;
            if (row == 0 || row == rails - 1)
                dir *= -1;
        }

        StringBuilder cipherText = new StringBuilder();
        for (StringBuilder sb : rail)
            cipherText.append(sb);
        return cipherText.toString();
    }

    // Rail Fence Cipher Decryption
    public static String railFenceDecrypt(String text, int rails) {
        if (rails <= 1)
            return text;

        int[] railLengths = new int[rails];
        int dir = 1, row = 0;

        for (char c : text.toCharArray()) {
            railLengths[row]++;
            row += dir;
            if (row == 0 || row == rails - 1)
                dir *= -1;
        }

        String[] railsData = new String[rails];
        int index = 0;
        for (int i = 0; i < rails; i++) {
            railsData[i] = text.substring(index, index + railLengths[i]);
            index += railLengths[i];
        }

        StringBuilder plainText = new StringBuilder();
        row = 0;
        dir = 1;
        int[] railIndices = new int[rails];

        for (int i = 0; i < text.length(); i++) {
            plainText.append(railsData[row].charAt(railIndices[row]++));
            row += dir;
            if (row == 0 || row == rails - 1)
                dir *= -1;
        }

        return plainText.toString();
    }

    public static void main(String[] args) throws Exception {
        // Generate a 128-bit AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();

        String plaintext = "HELLOHYBRIDENCRYPTION";
        System.out.println("Original: " + plaintext);

        // Step 1: AES Encryption
        String aesEncrypted = aesEncrypt(plaintext, aesKey);
        System.out.println("After AES Encryption: " + aesEncrypted);

        // Step 2: Rail Fence Transposition
        String encryptedText = railFenceEncrypt(aesEncrypted, RAILS);
        System.out.println("Encrypted Text: " + encryptedText);

        // Decryption
        String transpositionDecrypted = railFenceDecrypt(encryptedText, RAILS);
        String decryptedText = aesDecrypt(transpositionDecrypted, aesKey);

        System.out.println("Decrypted Text: " + decryptedText);
    }
}
