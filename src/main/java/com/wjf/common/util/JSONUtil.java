package com.jd.rainbow.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.gson.Gson;

/**
 * fxm on 14-8-14.
 */
public class JSONUtil {

	private static ObjectMapper MAPPER;
	private static Gson GSON;

	static {
		MAPPER = generateMapper(JsonSerialize.Inclusion.ALWAYS);
		GSON = new Gson();
	}

	private JSONUtil() {
	}

	/**
	 * 通过Inclusion创建ObjectMapper对象
	 * <p/>
	 * {@link org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
	 * Inclusion 对象枚举}
	 * <ul>
	 * <li>{@link org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
	 * Inclusion.ALWAYS 全部列入}</li>
	 * <li>{@link org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
	 * Inclusion.NON_DEFAULT 字段和对象默认值相同的时候不会列入}</li>
	 * <li>{@link org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
	 * Inclusion.NON_EMPTY 字段为NULL或者""的时候不会列入}</li>
	 * <li>{@link org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
	 * Inclusion.NON_NULL 字段为NULL时候不会列入}</li>
	 * </ul>
	 *
	 * @param inclusion
	 *            传入一个枚举值, 设置输出属性
	 * @return 返回ObjectMapper对象
	 */
	@Deprecated
	private static ObjectMapper generateMapper(JsonSerialize.Inclusion inclusion) {

		ObjectMapper customMapper = new ObjectMapper();

		// 设置输出时包含属性的风格
		customMapper.setSerializationInclusion(inclusion);

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		customMapper
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);

		// 禁止使用int代表Enum的order()來反序列化Enum,非常危險
		customMapper.configure(
				DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		// 所有日期格式都统一为以下样式
		customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return customMapper;
	}

	/**
	 * 转化成json数据
	 */
	public static String bean2Json(Object obj) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
		mapper.writeValue(gen, obj);
		gen.close();
		return sw.toString();
	}

	/*
	 * json数据转换成对象
	 */
	public static <T> T json2Bean(String jsonStr, Class<T> objClass)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, objClass);
	}

	// /*
	// * json数组数据转换成对象
	// * */
	public static <T> List<T> jsonArray2Bean(String jsonStr, Class<T> objClass)
			throws IOException {
		List<T> list = new ArrayList<T>();
		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createJsonParser(jsonStr);
		jp.nextToken();
	 
		while (jp.nextToken() == JsonToken.START_OBJECT) {
			T obj = MAPPER.readValue(jp, objClass); // process
			list.add(obj);
		}
		return list;
	}

	public static Map<String, Object> json2Map(String json) {
		Map<String, Object> productMap = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			productMap = mapper.readValue(json, Map.class);// 转成map
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productMap;
	}
}
