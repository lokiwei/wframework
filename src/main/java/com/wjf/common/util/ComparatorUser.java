package com.jd.rainbow.common.util;


import java.util.Comparator;

import com.jd.rainbow.model.RainbowUser;

/**
 * @author : dbzhangxiang
 * @version : 1.0
 * @date : 15-3-26 下午8:35
 */
public class ComparatorUser implements Comparator {

    public int compare(Object arg0, Object arg1) {
        RainbowUser user0=(RainbowUser)arg0;
        RainbowUser user1=(RainbowUser)arg1;

       Long code0=Long.valueOf(user0.getUserCode());
       Long code1=Long.valueOf(user1.getUserCode());
       int flag=code0.compareTo(code1);
       return flag;
    }

}
