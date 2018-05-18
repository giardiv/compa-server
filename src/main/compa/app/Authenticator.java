package main.compa.app;

import com.google.gson.annotations.Expose;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authenticator {
    public String encrypt(String rawPassword)  {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] passBytes = rawPassword.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        }catch (Exception e){

        }
        return null;
    }
}
