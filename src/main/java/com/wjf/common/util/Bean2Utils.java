package com.jd.rainbow.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 实体装换工具类
 * 目前支持对象转成map
 * @author wangchanghui
 *
 */
public class Bean2Utils {
	
/**
 * 将对象转成hashmap
 * @param obj
 * @return
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 */
public static HashMap<String, Object> objToHash(Object obj)throws IllegalArgumentException,IllegalAccessException {
		
		    HashMap<String, Object> hashMap = new HashMap<String, Object>();
		    Class clazz = obj.getClass();
		    List<Class> clazzs = new ArrayList<Class>();
		    do {
		
		        clazzs.add(clazz);
		
		        clazz = clazz.getSuperclass();
		
		    } while (!clazz.equals(Object.class));
		
		     
		    for (Class iClazz : clazzs) {
		
		        Field[] fields = iClazz.getDeclaredFields();
		
		        for (Field field : fields) {
		
		            Object objVal = null;
		
		            field.setAccessible(true);
		
		            objVal = field.get(obj);
		
		            hashMap.put(field.getName(), objVal);
		
		        }
		
		    }
		     
	 return hashMap;
}


}
