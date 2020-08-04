package com.bytedance.ug.agentmain;

import java.lang.instrument.ClassFileTransformer;

/**
 * Created by wuhongping on 15-11-19.
 */
public interface IClassTransformer extends ClassFileTransformer {
    boolean transforms(Class<?> klass);
}
