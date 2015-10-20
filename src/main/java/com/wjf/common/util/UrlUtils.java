package com.jd.rainbow.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.jd.official.core.exception.BusinessException;

public class UrlUtils {

	private static final Logger logger = Logger.getLogger(UrlUtils.class);
	
	public static String parseName(String docFileName,HttpServletRequest request){
		 try {
	            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0 ||
	                    request.getHeader("User-Agent").toUpperCase().indexOf("TRIDENT") > 0) {
	                return URLEncoder.encode(docFileName, "UTF-8");
	            } else {
	                String enableFileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(docFileName.getBytes("UTF-8")))) + "?=";
	                return enableFileName;
	            }
	        } catch (UnsupportedEncodingException e) {
	            logger.error("封装姓名出错",e);
	            throw new BusinessException(e);
	        }

	}
}
