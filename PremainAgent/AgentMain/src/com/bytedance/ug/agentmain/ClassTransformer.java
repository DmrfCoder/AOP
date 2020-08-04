package com.bytedance.ug.agentmain;


import jdk.internal.org.objectweb.asm.Type;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import sun.lwawt.macosx.CImage;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by wuhongping on 15-11-11.
 */
public class ClassTransformer implements IClassTransformer {

    Log log;

    public ClassTransformer(Log log) {
        this.log = log;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if ("java/lang/ProcessBuilder".equals(className) || "com/android/dx/command/dexer/Main".equals(className)) {
            if ("java/lang/ProcessBuilder".equals(className)) {
                return dealWithProcessBuilder(classfileBuffer);
            } else if ("com/android/dx/command/dexer/Main".equals(className)) {
                return dealWithDexMain(classfileBuffer);
            }
            System.out.println("找到了目标类：" + className);
        } else {
            System.out.println("当前不是目标类：" + className);

        }

        return classfileBuffer;
    }

    @Override
    public boolean transforms(Class<?> klass) {
        return "java/lang/ProcessBuilder".equals(Type.getType(klass).getInternalName()) || "com/android/dx/command/dexer/Main".equals(Type.getType(klass).getInternalName());
    }

    private byte[] dealWithProcessBuilder(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ProcessBuilderClassVisitor classVisitor = new ProcessBuilderClassVisitor(cw);
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();

    }


    private byte[] dealWithDexMain(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        DexMainBuilderClassVisitor classVisitor = new DexMainBuilderClassVisitor(cw, log);
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }
}
