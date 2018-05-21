package compa.services;

import com.google.common.base.Charsets;
import compa.app.Container;
import compa.app.Service;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.Exception;
import java.util.Date;


public class CipherSecurity extends Service {

    private final Logger log = LoggerFactory.getLogger(CipherSecurity.class);

    public static final String CIPHER_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM = "AES";
    public static final byte[] SECRET_KEY = "16BYTESSECRETKEY".getBytes(Charsets.UTF_8); // exactly 16 bytes to not use JCE (Java Cryptography Extension)

    public CipherSecurity(Container container){
        super(container);
    }

    public String decrypt(String encryptedInput) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            return new String(cipher.doFinal(Base64.decodeBase64(encryptedInput)), Charsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY, KEY_ALGORITHM));
            return Base64.encodeBase64URLSafeString(cipher.doFinal(str.getBytes(Charsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /* public static void main(String[] args) {
        CipherSecurity cipherUtil = new CipherSecurity();
        // Encryption
        String encryptedString = cipherUtil.encrypt("password" + String.valueOf(new Date().getTime()));
        // Before Decryption
        System.out.println("Avant déchiffrement : " + encryptedString);
        String s = cipherUtil.decrypt(encryptedString);
        System.out.println("Après déchiffrement : " + s);
    }*/
}
