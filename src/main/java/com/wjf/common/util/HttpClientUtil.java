package com.jd.rainbow.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.jd.official.core.exception.BusinessException;
public class HttpClientUtil {
	
	
	private static final Logger logger = Logger.getLogger(HttpClientUtil.class);

	private  HttpClient httpClient;

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientUtil() {

    }

    /**
     * 以get方式发送http请求
     *
     * @param url
     * @return
     */
    public String httpGet(String url) throws IOException {
        GetMethod getMethod = new GetMethod(url);
        try {
            getMethod.getParams().setContentCharset("UTF-8");
//            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
            int  statusCode=httpClient.executeMethod(getMethod);
            if(statusCode!= HttpStatus.SC_OK){
                throw new BusinessException("服务器返回错误："+getMethod.getStatusLine());
            }
            return getMethod.getResponseBodyAsString();
        }finally{
            getMethod.releaseConnection();
        }
    }


    /**
     * 以post方式发送http请求
     *
     * @param url
     * @return
     */
    public  String httpPost(String url,Map<String,Object> param) throws IOException {
        PostMethod postMethod = new PostMethod(url.trim());
        try {
            postMethod.getParams().setContentCharset("UTF-8");
            if(param!=null&& !param.isEmpty()){
                for(Map.Entry<String,Object>entry:param.entrySet()){
                	String value = String.valueOf(entry.getValue());
                    postMethod.addParameter(entry.getKey(),URLEncoder.encode(value, "utf-8"));
                }
            }
            int statusCode = httpClient.executeMethod(postMethod);
            if(statusCode!= HttpStatus.SC_OK){
            	logger.error("=============statusCode:============"+statusCode);
                throw new BusinessException("服务器返回错误："+postMethod.getStatusLine());
            }
            return new String(postMethod.getResponseBodyAsString().getBytes());
        }catch(Exception e){
        	e.printStackTrace();
        	return "";
        }finally{
            postMethod.releaseConnection();
        }
    }

    /*
    * 发送json数据
    * */
    public  String httpPostJson(String url,String json) throws IOException {
        PostMethod postMethod = new PostMethod(url);
        try {
            postMethod.getParams().setContentCharset("UTF-8");
            postMethod.setRequestEntity(new StringRequestEntity(json,"application/json","UTF-8"));
            int statusCode = httpClient.executeMethod(postMethod);
            if(statusCode!= HttpStatus.SC_OK){
                throw new BusinessException("服务器返回错误："+postMethod.getStatusLine());
            }
            return postMethod.getResponseBodyAsString();
        }finally{
            postMethod.releaseConnection();
        }
    }


    public  String createURI(String host,String url,Map<String,String> paramMap) throws URISyntaxException {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        String param="";
        if(paramMap!=null&& !paramMap.isEmpty()){
            for(Map.Entry<String,String>entry:paramMap.entrySet()){
                params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
            }
            param =URLEncodedUtils.format(params, "UTF-8");
        }
        URI uri =URIUtils.createURI("http",host,80,url,param, null);
        return uri.toString();
    }

	public HttpClient getHttpClient() {
		return httpClient;
	}

}
