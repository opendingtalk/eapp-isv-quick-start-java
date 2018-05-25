package com.sdk;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


public class DingTalkSignatureUtil {
    /**
     *  The default encoding.
     *
     *
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     *Signature method.
     *
     */
    private static final String ALGORITHM = "HmacSHA256";

    private static final String NEW_LINE = "\n";

    public static String getCanonicalString(long timestamp, String suiteTicket) {
        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(timestamp).append(NEW_LINE).append(suiteTicket);

        return canonicalString.toString();
    }

    public static String computeSignature(String key, String data) {
        try {
            byte[] signData = sign(key.getBytes(DEFAULT_ENCODING), data.getBytes(DEFAULT_ENCODING));
            return new String(Base64.encodeBase64(signData));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported algorithm: " + DEFAULT_ENCODING, ex);
        }
    }

    private static byte[] sign(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unsupported algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeyException ex) {
            throw new RuntimeException("Invalid key: " + key, ex);
        }
    }

    public static String paramToQueryString(Map<String, String> params, String charset) {

        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            // Urlencode each request parameter
            paramString.append(urlEncode(key, charset));
            if (value != null) {
                paramString.append("=").append(DingTalkSignatureUtil.urlEncode(value, charset));
            }

            first = false;
        }

        return paramString.toString();
    }

    /**
     * Encode a URL segment with special chars replaced.
     */
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }

        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

    /**
     * @return
     */
    public static String getRandomStr(int count) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
