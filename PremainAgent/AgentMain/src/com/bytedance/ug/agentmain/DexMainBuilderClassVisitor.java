package com.bytedance.ug.agentmain;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import java.util.Map;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * @author dmrfcoder
 * @date 2020-08-03
 */
public class DexMainBuilderClassVisitor extends ClassVisitor {
    private Log log;

    public DexMainBuilderClassVisitor(ClassVisitor cv, Log log) {
        super(Opcodes.ASM5, cv);
        this.log = log;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("processClass".equals(name) && "(Ljava/lang/String;[B)Z".equals(desc)) {
            return new DexerMainMethodVisitor(mv, access, name, desc);
        }
        return mv;

    }
}
