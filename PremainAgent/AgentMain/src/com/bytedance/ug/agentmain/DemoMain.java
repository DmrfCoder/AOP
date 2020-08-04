package com.bytedance.ug.agentmain;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * @author dmrfcoder
 * @date 2020-07-30
 */
public class DemoMain {

    public static void main(String[] args){
        try {
            Field treeLock = Logger.class.getDeclaredField("treeLock");
            treeLock.setAccessible(true);
            Object o = treeLock.get(null);
            System.out.println("result is:"+o);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
