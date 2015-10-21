package com.wjf.common.util;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil<T> {

	public String toJson(T t){
		Gson gson = new Gson();
		return gson.toJson(t);
	}
	
	
	public T fromJson(String json){
		if(StringUtils.isNotBlank(json) && !json.equalsIgnoreCase("[]")){
			Gson gson = new Gson();
			return (T)gson.fromJson(json, new TypeToken<T>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 7869336740925537900L;}.getType());
			
		}
		return null;
	}
}
