package com.jd.rainbow.common.util;

public class JsonAdResultUtil{
	
	private JsonResultUtil jsonResultUtil;

	private Long id;
	
	public JsonAdResultUtil(JsonResultUtil jsonResultUtil) {
		this.jsonResultUtil = jsonResultUtil;
	}
	
	public JsonAdResultUtil(Long id,JsonResultUtil jsonResultUtil){
		this.id = id;
		this.jsonResultUtil = jsonResultUtil;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JsonResultUtil getJsonResultUtil() {
		return jsonResultUtil;
	}

	public void setJsonResultUtil(JsonResultUtil jsonResultUtil) {
		this.jsonResultUtil = jsonResultUtil;
	}
	
	public String getJsonResult(Long id){
		return "{"+jsonResultUtil.getStringResult()+",\"id\":\""+id+"\"}";
	}
	

}
