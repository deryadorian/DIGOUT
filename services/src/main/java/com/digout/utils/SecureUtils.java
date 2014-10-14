package com.digout.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import static javax.crypto.Cipher.ENCRYPT_MODE;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;

public final class SecureUtils {

    /**
     * Private function to turn md5 result to 32 hex-digit string
     */
    public static String asHex(final byte hash[]) {
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;
        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String asHex(final CharSequence charSequence) {
        StringBuilder stringBuilder = new StringBuilder(charSequence.length() * 2);

        for (int i = 0; i < charSequence.length(); i++) {
            if ((charSequence.charAt(i) & 0xff) < 0x10) {
                stringBuilder.append("0");
            }

            stringBuilder.append(Long.toString(charSequence.charAt(i) & 0xff, 16));
        }

        return stringBuilder.toString();
    }

    public static String decodeBase64(final String data) {
        return new String(Base64.decodeBase64(data.getBytes()));
    }

    public static String decodeBase64(final String data, final String encoding) {
        try {
            return new String(Base64.decodeBase64(data.getBytes()), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static StringBuilder decodeHex(final String data) {
        StringBuilder stringBuilder = new StringBuilder(data.length());

        for (int i = 0; i < data.length(); i += 2) {
            char c = (char) Integer.valueOf("" + data.charAt(i) + data.charAt(i + 1), 16).intValue();
            stringBuilder.append(c);
        }

        return stringBuilder;
    }

    public static String decryptRSA(final byte[] text, final PrivateKey key) {
        byte[] decryptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(decryptedText);
    }

    public static String encodeBase64(final String data) {
        return new String(Base64.encodeBase64(data.getBytes()));
    }

    public static String encodeBase64(final String data, final String encoding) {
        try {
            return new String(Base64.encodeBase64(data.getBytes()), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeXOR(final String source, final String key) {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char charCode = source.charAt(i);
            for (int j = 0; j < key.length(); j++) {
                charCode ^= (key.charAt(j));
            }
            output.append(charCode);
        }
        return output.toString();
    }

    public static String encryptAES(final String data, final String key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(ENCRYPT_MODE, skeySpec);
            return asHex(cipher.doFinal(data.getBytes()));
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptRSA(final String text, final PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static String generateRandomSequence(final int length) {
        String sequence = "";

        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            for (int i = 0; i < length; i++) {
                char ch = 0;

                int j = random.nextInt(3);

                switch (j) {
                case 0:
                    ch = (char) (97 + random.nextInt(26));
                    break;
                case 1:
                    ch = (char) (65 + random.nextInt(26));
                    break;
                case 2:
                    ch = (char) (48 + random.nextInt(10));
                    break;
                }
                sequence += ch;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return sequence;
    }

    public static String generateSecureId() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String hash(final String value) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes());
            result = asHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private SecureUtils() {
    }
}
