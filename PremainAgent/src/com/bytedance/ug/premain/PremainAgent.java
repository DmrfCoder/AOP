package com.bytedance.ug.premain;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author dmrfcoder
 * @date 2020-07-21
 */
public class PremainAgent {
    private static Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation ins) {
        instrumentation = ins;
        excute();
    }

    private static void excute() {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                System.out.println("通过ClassFileTransformer.transform方法处理类的字节码，当前处理的类是：" + className);
                return classfileBuffer;
            }
        });
    }
}
