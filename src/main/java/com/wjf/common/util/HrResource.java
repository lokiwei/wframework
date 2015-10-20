package com.jd.rainbow.common.util;

import java.util.HashMap;
import java.util.Map;

public class HrResource {

	public static Map<String,String> urlMap = new HashMap<String,String>();

	public void setUrlMap(Map<String, String> urlMap) {
		HrResource.urlMap = urlMap;
	}
}
