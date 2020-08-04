package com.bytedance.ug.agentmain;

/**
 * @author dmrfcoder
 * @date 2020-07-30
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class InvocationDispatcher implements InvocationHandler {
    private final Log log;
    HashMap<String, InvocationHandler> invocationHandlerFactory = new HashMap();

    public InvocationDispatcher(Log log) throws ClassNotFoundException {
        this.log = log;
        this.invocationHandlerFactory.put("java/lang/ProcessBuilder.start", new ProcessBuilderInvocationHandler(this, log));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationHandler invocationHandler = (InvocationHandler) this.invocationHandlerFactory.get(proxy);
        if (invocationHandler == null) {
            this.log.e("Unsupported transform target: " + proxy);
            return null;
        } else {
            try {
                return invocationHandler.invoke(proxy, method, args);
            } catch (Exception var6) {
                this.log.e("Error:" + var6.getMessage(), var6);
                return null;
            }
        }
    }
}
