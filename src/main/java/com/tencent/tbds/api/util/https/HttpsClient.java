package com.tencent.tbds.api.util.https;

import com.tencent.tbds.api.util.json.JsonTools;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liulv
 */
public class HttpsClient {

    public static String doPost(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        return doPost(url, headers, params, null, false);
    }

    public static String doPost(String url, Map<String, String> headers, String jsonFile, boolean isJsonStr) throws Exception {
        return doPost(url, headers, null, jsonFile, isJsonStr);
    }

    private static String doPost(String url, Map<String, String> headers, Map<String, String> params, String jsonFileOrJsonStr, boolean paramIsJsonStr) throws Exception {
        //获取绕过安全检查的httpClient，以便发送https请求
        CloseableHttpClient httpClient = SSLClient.getSingleSSLConnection();
        CloseableHttpResponse response = null;
        try {
            //创建httppost方法
            HttpPost httpPost = new HttpPost(url);

            //组装请求参数，key-value形式的
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            }

            if (headers != null) for (String headerKey : headers.keySet()) {
                httpPost.setHeader(headerKey, headers.get(headerKey));
            }

            if(jsonFileOrJsonStr != null){
                httpPost.setEntity(jsonFileToStringEntity(jsonFileOrJsonStr, paramIsJsonStr));
            }

            //执行post方法
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {//出现链接异常，抛出
                httpPost.abort();
                throw new Exception("HttpClient,error status code :" + statusCode);
            }
            return responseDispose(response);
        } catch (Exception e) {
            throw e;
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * HTTP Get 获取内容
     *
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        //获取绕过安全检查的httpClient，以便发送https请求
        CloseableHttpClient httpClient = SSLClient.getSingleSSLConnection();
        CloseableHttpResponse response;

        if (params != null && !params.isEmpty()) {

            List<NameValuePair> pairs = new ArrayList<>(params.size());

            for (String key : params.keySet()) {
                pairs.add(new BasicNameValuePair(key, params.get(key)));
            }
            url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs), "UTF-8");
        }

        HttpGet httpGet = new HttpGet(url);
        if (headers != null) for (String headerKey : headers.keySet()) {
            httpGet.setHeader(headerKey, headers.get(headerKey));
        }

        response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 200) {
            httpGet.abort();
            throw new RuntimeException("HttpClient,error status code :" + statusCode);
        }else {
            return responseDispose(response);
        }
    }

    private static StringEntity jsonFileToStringEntity(String filePathOrStr, boolean isJsonStr){
        String qqbw;

        if(isJsonStr)
            qqbw = filePathOrStr;
        else
            qqbw = JsonTools.readToString(filePathOrStr);

        assert qqbw != null;
        StringEntity se = new StringEntity(qqbw, "utf-8");
        se.setContentType("application/json");
        se.setContentEncoding("UTF-8");

        return se;
    }

    private static String responseDispose(CloseableHttpResponse response) throws UnsupportedEncodingException {
        String responseStr = "";
            Header[] responseHeader = response.getAllHeaders();
            System.out.println("--------------------------------------");
            System.out.println("响应状态 :" + response.getStatusLine());
            System.out.println("--------------------------------------");
            System.out.println("响应头：");
            for(Header header : responseHeader){
                String headerName = header.getName();
                String headerValue = header.toString();
                if(headerName.equals("status_message")) headerValue = URLDecoder.decode(headerValue, "UTF-8");
                System.out.println(headerName + ":" + headerValue);
            }
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseStr = EntityUtils.toString(entity, "UTF-8");
                    System.out.println("--------------------------------------");
                    System.out.println("响应报文: \n" + (JsonTools.formatJson(responseStr)));
                    System.out.println("--------------------------------------");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
          return  responseStr;
    }
}
