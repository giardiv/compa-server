package main.compa.app;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.lang.Exception;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class CipherUtilSecret {

    private final Logger log = LoggerFactory.getLogger(CipherUtilSecret.class);

    public static final String CIPHER_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM = "AES";
    public static final byte[] SECRET_KEY = "16BYTESSECRETKEY".getBytes(Charsets.UTF_8); // exactly 16 bytes to not use JCE (Java Cryptography Extension)

    public String decrypt(String encryptedInput) {
        Cipher cipher = null;
        String mdpDecrypt = null;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            mdpDecrypt = new String(cipher.doFinal(Base64.decodeBase64(encryptedInput)), Charsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mdpDecrypt;
    }

    public String encrypt(String str) {
        String mdpEncrypt = "";
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            mdpEncrypt = Base64.encodeBase64URLSafeString(cipher.doFinal(str.getBytes(Charsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mdpEncrypt;
    }

    public static void main(String[] args) {
        CipherUtilSecret cipherUtil = new CipherUtilSecret();
        // Encryption
        String encryptedString = cipherUtil.encrypt("password" + String.valueOf(new Date().getTime()));
        // Before Decryption
        System.out.println("Avant déchiffrement : " + encryptedString);
        String s = cipherUtil.decrypt(encryptedString);
        System.out.println("Après déchiffrement : " + s);
    }
}
