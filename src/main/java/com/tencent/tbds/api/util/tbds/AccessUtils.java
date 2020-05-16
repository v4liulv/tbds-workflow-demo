package com.tencent.tbds.api.util.tbds;

import com.tencent.tbds.api.util.props.PropertiesUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

/**
 * @author liulv
 */
public class AccessUtils {

    public static String getAccessAuthHeader() throws Exception {
        String secureId = PropertiesUtils.getProperties().getProperty("tbds_user_secure_id");
        String secureKey = PropertiesUtils.getProperties().getProperty("tbds_user_secure_key");
        long timestamp = new Date().getTime();
        int nonce = new Random().nextInt(10 * 8) + 1;
        String signature = AccessUtils.generateSignature(secureId, timestamp, nonce, secureKey);
        String accessHeader = "TBDS " + secureId + " " + timestamp + " " + nonce + " " + signature;
        System.out.println("access encode=" + accessHeader);
        return accessHeader;
    }

    private static String generateSignature(String secureId, long timestamp, int randomValue, String secureKey) {
        Base64 base64 = new Base64();
        byte[] baseStr = base64.encode(HmacUtils.hmacSha1(secureKey, secureId + timestamp + randomValue));
        String result = "";
        try {
            result = URLEncoder.encode(new String(baseStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
       getAccessAuthHeader();
    }

}
