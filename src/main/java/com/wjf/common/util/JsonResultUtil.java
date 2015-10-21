package com.wjf.common.util;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * @author wangchanghui
 *
 * @date 2015年3月19日
 */
public class JsonResultUtil {
	
	/**
	 * 操作结果
	 */
	private boolean operator;
	
	/**
	 * 操作提示信息
	 */
	private String message;
	
	/**
	 * 操作简码
	 */
	private String operationCode;
	
	/**
	 * 自定义内容
	 */
	private String customMessage;
	
	
	public JsonResultUtil(boolean operator){
		this.operator = operator;
	}
	
	/**
	 * 匹配操作码
	 * 拼出返回信息
	 * @param operator
	 * @param operationCode
	 */
	public JsonResultUtil(boolean operator,String operationCode){
		if(StringUtils.isNotBlank(operationCode)){
//			OperationResultEnum[] array = OperationResultEnum.values();
//			for(OperationResultEnum enu:array){
//				if(enu.getCode().equals(operationCode.trim().toLowerCase())){
//					if(operator)
//						this.message = enu.getSuccess();
//					else
//						this.message = enu.getFailure();
//					break;
//				}
//				
//			}
			if(StringUtils.isBlank(message))
				this.message = operationCode;
			this.operationCode = operationCode;
		}
		this.operator = operator;
	}
	
	
	/**
	 * 返回json字符串
	 * @author wangchanghui
	 * @return
	 * String
	 * @date 2015年2月5日
	 */
	public String getJsonResult(){
		return "{\"operator\":"+this.operator+",\"message\":\""+this.message+"\"}";
	}
	
	/**
	 * 返回json格式的字符串
	 * 方便与其他jons串拼装
	 * @author wangchanghui
	 * @return
	 * String
	 * @date 2015年2月5日
	 */
	public String getStringResult(){
		return "\"operator\":"+this.operator+",\"message\":\""+this.message+"\"";
	}
	
	/**
	 * 添加自定义内容
	 * @author wangchanghui
	 * @param operator
	 * @param message
	 * @param custom
	 * @return
	 * String
	 * @date 2014年10月17日
	 */
	public String getJsonResult(String custom){
		if(custom == null)
			custom = "";
		return "{\"operator\":"+this.operator+",\"message\":\""+custom+"\"}";
	}

	public boolean isOperator() {
		return operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
}
