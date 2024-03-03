package club.asyncraft.asyncauth.server.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    private final static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodePassword(String password,String salt) {
        digest.reset();
        String encodedPasswordString = encodeString(password);
        String encodedAuthmePassword = encodeString(encodedPasswordString + salt);
        return "$SHA$" + salt + "$" + encodedAuthmePassword;
    }

    private static String encodeString(String str) {
        digest.reset();
        digest.update(str.getBytes());
        byte[] bytes = digest.digest();
        return String.format("%0" + (bytes.length << 1) + "x", new BigInteger(1, bytes));
    }

}
