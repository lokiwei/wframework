package com.jd.rainbow.common.util;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jd.common.util.StringUtils;
import com.jd.jsf.gd.GenericService;
import com.jd.jsf.gd.config.ConsumerConfig;
import com.jd.jsf.gd.config.RegistryConfig;
import com.jd.official.core.utils.HttpClientUtils;
import com.jd.official.core.utils.JsonUtils;
import com.jd.official.core.utils.SpringContextUtils;
import com.jd.official.modules.manage.org.model.User;
import com.jd.official.omdm.is.attendance.vo.VoModel;
import com.jd.official.omdm.is.values.vo.RecordVo;
import com.jd.official.omdm.utils.MD5Util;
import net.sf.json.JSONArray;
/**
 * JSF调用主数据平台通用客户端
 * 
 * @author : shizhong
 * @version : 1.0
 * @date : 15-3-17 上午11:51
 */
public class JsfClient {
	


    public final  static  String JSF_CLIENT_ALIAS = "alias";
    public final  static  String JSF_REGISTRY_INDEX = "index";
    public final  static  String JSF_CLIENT_PROTOCOL = "clientProtocol";
    public final  static  String JSF_CLIENT_TIMEOUT= "timeout";
	private final static Logger LOGGER = Logger.getLogger(JsfClient.class);
	
	 private final static Map<String,GenericService> JSFCacheMap = new HashMap<String, GenericService>();

	private final static int METHOD_PARAM_SIZE = 6;

	private MD5Util md5Util;

	/**
	 * 主数据平台分配的appcode
	 */
	private String appCode;
	/**
	 * 自定义，不重复即可
	 */
	private String businessId;

	private String key;
	
	  public static Object invoke(Map<String,String>JSFConfMap,String url,String method,String[] paramTypes,Object[] params){
		  LOGGER.info("[动态JSF调用-开始调用JSF接口]url="+url+",method="+method);
	        GenericService genericService;

	        String keyIndex = url +
	                JSF_CLIENT_ALIAS + JSFConfMap.get(JSF_CLIENT_ALIAS) +
	                JSF_REGISTRY_INDEX + JSFConfMap.get(JSF_REGISTRY_INDEX) +
	                JSF_CLIENT_PROTOCOL + JSFConfMap.get(JSF_CLIENT_PROTOCOL) +
	                JSF_CLIENT_TIMEOUT + JSFConfMap.get(JSF_CLIENT_TIMEOUT);

	        if(JSFCacheMap.containsKey(keyIndex)){
	            genericService = JSFCacheMap.get(keyIndex);
	        }else{
	            genericService = createJSFService(JSFConfMap,url);
	            JSFCacheMap.put(keyIndex,genericService);
	        }
	        if(genericService == null){
	        	LOGGER.info("[动态JSF调用-JSF服务获取失败]url="+url);
	            return null;
	        }
	        Object result = genericService.$invoke(method,paramTypes,params);
	        LOGGER.info("[动态JSF调用-调用成功]url="+url+",method="+method+",result="+ JsonUtils.toJsonByGoogle(result));
	        return result;
	    }
	  
	  public static GenericService createJSFService(Map<String,String> JSFConfMap,String url) {
		    LOGGER.info("[动态JSF调用-创建JSF服务]url="+url+",conf="+JsonUtils.toJsonByGoogle(JSFConfMap));
	        RegistryConfig jsfRegistry = new RegistryConfig();
	        jsfRegistry.setIndex(JSFConfMap.get(JSF_REGISTRY_INDEX));
	        ConsumerConfig<com.jd.jsf.gd.GenericService> consumerConfig = new ConsumerConfig<com.jd.jsf.gd.GenericService>();
	        consumerConfig.setInterfaceId(url);
	        consumerConfig.setAlias(JSFConfMap.get(JSF_CLIENT_ALIAS));
	        consumerConfig.setProtocol(JSFConfMap.get(JSF_CLIENT_PROTOCOL));
	        consumerConfig.setRegistry(jsfRegistry);
	        consumerConfig.setGeneric(true);
	        return consumerConfig.refer();
	    }

	/**
	 * 
	 * @param methodName
	 *            主数据平台调用方法
	 * @param param
	 *            调用方法的最后一个参数
	 * @return
	 */
	public String call(String classBeanName, String methodName, String param) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };
		String[] args = new String[6];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, param);
		args[3] = sign;
		args[4] = "";
		args[5] = param;
		String result = "";
		try {
			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
			} else {
				LOGGER.info("调用主数据平台失败");
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		return result;
	}


	/**
	 *不需要传必须参
	 * @param methodName
	 *            主数据平台调用方法
	 * @return
	 */
	public String call(String classBeanName, String methodName) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class };
		String[] args = new String[5];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, "");
		args[3] = sign;
		args[4] = "";
		String result = "";
		try {
			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
			} else {
				LOGGER.info("调用主数据平台失败");
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	/**
	 * 
	 * @param methodName
	 *            主数据平台调用方法
	 * @param param
	 *            调用方法的最后一个参数
	 * @return
	 */
	public String callByTwoParam(String classBeanName, String methodName, String param, String param2) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class };
		String[] args = new String[7];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, param);
		args[3] = sign;
		args[4] = "";
		args[5] = param;
		args[6] = param2;
		String result = "";
		try {
			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
			} else {
				LOGGER.info("调用主数据平台失败:"+result);
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	/**
	 * 描述 对call方法重载，同时取得结果对象和数量
	 * 
	 * @param classBeanName
	 * @param methodName
	 * @param param
	 * @return
	 * @created 2015-5-16 上午11:52:57
	 */
	public Map<String, Object> callDataCount(String classBeanName, String methodName, String param) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, String.class };
		String[] args = new String[6];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, param);
		args[3] = sign;
		args[4] = "";
		args[5] = param;
		String result = "";
		int count = 0;
		try {
			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
				count = jsonObject.getInt("resCount");
			} else {
				LOGGER.info("调用主数据平台失败");
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("RESULTSTR", result);
		rtnMap.put("RESULTCOUNT", count);
		return rtnMap;
	}

	/**
	 * 
	 * @param methodName
	 *            主数据平台调用方法
	 * @param param
	 *            调用方法的最后一个参数
	 * @return
	 */
	public String callForObj(String classBeanName, String methodName, Object param) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, Object.class };
		Object[] args = new Object[6];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		RecordVo rv = (RecordVo) param;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, rv.getScore() + rv.getForUserErp());
		args[3] = sign;
		args[4] = "";
		args[5] = param;
		String result = "";

		try {

			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
			} else {
				LOGGER.info("调用主数据平台失败");
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	/**
	 * 描述 针对 C U D操作 ，R操作已经通过 find...方法实现
	 * 
	 * @param classBeanName
	 * @param methodName
	 * @param param
	 * @return
	 * @created 2015-5-17 上午10:29:41
	 */
	public String callForVoObj(String classBeanName, String methodName, VoModel param) {
		Object bean = SpringContextUtils.getBean(classBeanName);
		Class[] paramTypes = new Class[] { String.class, String.class, String.class, String.class, String.class, VoModel.class };
		Object[] args = new Object[6];
		args[0] = appCode;
		args[1] = businessId;
		Date date = new Date();
		String timestamp = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
		args[2] = timestamp;
		String sign = md5Util.getSign(appCode, businessId, timestamp, key, param.getId() + "");
		args[3] = sign;
		args[4] = "";
		args[5] = param;
		String result = "";
		try {
			Method method = bean.getClass().getMethod(methodName, paramTypes);
			result = (String) method.invoke(bean, args);
			result = URLDecoder.decode(result, "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (StringUtils.isNotBlank(result) && "200".equals(jsonObject.getString("resStatus"))) {
				result = jsonObject.getString("responsebody");
			} else {
				LOGGER.info("调用主数据平台失败");
				result = null;
			}
		} catch (Exception e) {
			LOGGER.error("调用主数据平台失败");
			e.printStackTrace();
			result = null;
		}

		return result;
	}
	
	

	/**
	 * @Description 根据参数通过主数据接口调用返回用户信息
	 * @param query
	 * @return
	 */
	public List getUserByQuery(String query){
		List<User> users = null;
		try {
			String orgUserPrefix = "http://172.16.132.100:2000/service/hrUserService/rest/findUsers/";
			
			String paramsJson = getParamsJson(query);
			Date date = new Date();
			String dateTime = DateUtils.getDateStr(date, "yyyy-MM-dd HH:mm:ss.FFF");
			
			String sign = new  MD5Util().getSign("rainbow","rainbow", dateTime ,"69J6TG1303878PQ2O48H" , paramsJson);
			
			String[] params = null;
			params = new String[] { "appCode=" + "rainbow", "businessId="+"rainbow", "requestTimestamp=" + dateTime, "sign=" + sign };
			
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			if (params != null && params.length > 0) {
				for (String param : params) {
					String[] nameAndValue = param.split("=");
					paramsList.add(new BasicNameValuePair(nameAndValue[0], nameAndValue[1]));
				}
			}
			
			String response = HttpClientUtils.get(orgUserPrefix + paramsJson.replace("{", "%7b").replace("}", "%7d")
					.replace(" ", "%20").replace("\"", "%22"), paramsList);
			response = URLDecoder.decode(response, "utf-8");
			
			JSONObject jsonObject = JSONObject.fromObject(response);
			users = new ArrayList<User>();
			if(StringUtils.isNotBlank(response) && 
					"200".equals(jsonObject.getString("resStatus"))){
				JSONObject responseBody = (JSONObject) jsonObject.get("responsebody");
				JSONArray jsonArray = responseBody.getJSONArray("userVoList");
				
				for(int i=0;i<jsonArray.size();i++){
					users.add(buildUserFromJson(jsonArray.getJSONObject(i)));
				}
			}else{
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	/**
	 * @Description 组织返回的用户信息
	 * @param object
	 * @return
	 */
	public static User buildUserFromJson(JSONObject object){
		User user = new User();
		user.setUserCode(object.getString("userCode"));
		user.setUserName(object.getString("userName"));
		user.setRealName(object.getString("realName"));
		user.setEmail(object.getString("email"));
		return user;
	}
	
	/**
	 * @Description 区分输入的参数是ERP还是中文名称
	 * @param query
	 * @return
	 */
	public static String getParamsJson(String query){
		String paramsJson = null;
		String regex = "^[a-z0-9A-Z]+$";
		String regexChinese = "^[\u4e00-\u9fa5]+$";
		if(query.contains("@")){ //邮箱
			//query = new String(Base64.encodeBase64(query.getBytes("UTF-8")));
			paramsJson = "{\"params\":{\"email\":\""+query+"\"}}";
		}else if(query.matches(regex)){ //字母、数字
			paramsJson = "{\"params\":{\"userName\":\""+query+"\"}}";
		}else if(query.matches(regexChinese)){ //中文
			//query = new String(Base64.encodeBase64(query.getBytes("UTF-8")));
			paramsJson = "{\"params\":{\"realName\":\""+query+"\"}}";
		}
		return paramsJson;
	}
	
	

	public MD5Util getMd5Util() {
		return md5Util;
	}

	public void setMd5Util(MD5Util md5Util) {
		this.md5Util = md5Util;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("/spring-config-jsf-client.xml");
		JsfClient jsfClient = (JsfClient) appContext.getBean("jsfClient");
		// String result =
		// jsfClient.call("omdmHrUserService","getUserBaseInfoByUserName","zhouhuaqi");
		String result = jsfClient.call("omdmValuesService", "valuesSubordinateScoreTotal", "zhouhuaqi");
		System.out.println(result);

	}
}
