package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhongping on 15-11-18.
 */
public class ProcessBuilderMethodVisitor extends MarkMethodVisitor {

    protected ProcessBuilderMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(methodVisitor, access, name, desc);//name是start
    }

    /**
     * 调用ProcessBuilderInvocationHandler#invoke
     */
    @Override
    protected void onMethodEnter(){

        invocationBuilder.loadInvocationDispatcher()
                .loadInvocationDispatcherKey(TransformAgent.genDispatcherKey("java/lang/ProcessBuilder", methodName))
                .loadArray(new Runnable[] {new Runnable() {
                    @Override
                    public void run() {
                        loadThis();//[this,0,objects ref,objects ref]
                        invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder")
                                , new Method("command", "()Ljava/util/List;"));//
                    }
                }}).invokeDispatcher();


    }

    private void demo(){
        ProcessBuilder processBuilder=new ProcessBuilder();
        List<String> command=new ArrayList<>(1);
        processBuilder.command(command);

    }
}
