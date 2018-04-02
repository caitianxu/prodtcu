package com.cjsz.tech.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class HttpClientUtil {  
    private static PoolingHttpClientConnectionManager cm;  
    private static String EMPTY_STR = "";  
    private static String UTF_8 = "UTF-8";  
      
    private static void init(){  
        if(cm == null){  
            cm = new PoolingHttpClientConnectionManager();  
            cm.setMaxTotal(50);//整个连接池最大连接数  
            cm.setDefaultMaxPerRoute(5);//每路由最大连接数，默认值是2  
        }  
    }  
      
    /** 
     * 通过连接池获取HttpClient 
     * @return 
     */  
    private static CloseableHttpClient getHttpClient(){
        if(cm==null){
            init();
        }
        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");
        return builder.setConnectionManager(cm).build();
    }  
      
    /** 
     *  
     * @param url 
     * @return 
     */  
    public static String httpGetRequest(String url){  
        HttpGet httpGet = new HttpGet(url);  
        return getResult(httpGet);  
    }  
      
    public static String httpGetRequest(String url, Map<String, Object> params) throws Exception{
        URIBuilder ub = new URIBuilder();  
        ub.setPath(url);  
          
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        ub.setParameters(pairs);  
          
        HttpGet httpGet = new HttpGet(ub.build());  
        return getResult(httpGet);  
    }  
      
    public static String httpGetRequest(String url, Map<String, Object> headers,   
            Map<String, Object> params) throws Exception{
        URIBuilder ub = new URIBuilder();  
        ub.setPath(url);  
          
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        ub.setParameters(pairs);  
          
        HttpGet httpGet = new HttpGet(ub.build());  
        for (Map.Entry<String, Object> param: headers.entrySet()) {  
            httpGet.addHeader(param.getKey(), (String) param.getValue());
        }  
        return getResult(httpGet);  
    }  
      
    public static String httpPostRequest(String url){  
        HttpPost httpPost = new HttpPost(url);  
        return getResult(httpPost);  
    }  
      
    public static String httpPostRequest(String url, Map<String, Object> params) throws Exception{
        HttpPost httpPost = new HttpPost(url);  
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));  
        return getResult(httpPost);  
    }  
      
    public static String httpPostRequest(String url, Map<String, Object> headers,   
            Map<String, Object> params) throws Exception{
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, Object> param: headers.entrySet()) {  
            httpPost.addHeader(param.getKey(), (String) param.getValue());
        }  
          
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);  
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));  
          
        return getResult(httpPost);  
    }  
      
    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params){  
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();  
        for (Map.Entry<String, Object> param: params.entrySet()) {  
            pairs.add(new BasicNameValuePair(param.getKey(), (String) param.getValue()));
        }  
          
        return pairs;  
    }  
      
      
    /** 
     * 处理Http请求 
     * @param request 
     * @return 
     */  
    private static String getResult(HttpRequestBase request){  
        CloseableHttpClient httpClient = getHttpClient();

        CloseableHttpResponse response =null;
        try{  
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                String result = EntityUtils.toString(entity);

                return result;
            }  
        }catch(Exception e){
           throw new RuntimeException("请求失败!");
        }finally{
             if(response!=null) {
                 try {
                     response.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }  
          
        return EMPTY_STR;  
    }  
      
}  