package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by wuhongping on 15-11-18.
 */
public class InvocationBuilder {
    private final GeneratorAdapter generatorAdapter;

    public InvocationBuilder(GeneratorAdapter adapter) {
        generatorAdapter = adapter;
    }

    public InvocationBuilder loadNull() {
        this.generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        return this;
    }



    public InvocationBuilder command(List<String> command){
        return this;
    }
    //取InvocationDispatcher
    public InvocationBuilder loadInvocationDispatcher() {
        generatorAdapter.visitLdcInsn(Type.getType(TransformAgent.LOGGER));
        generatorAdapter.visitLdcInsn("treeLock");
        generatorAdapter.invokeVirtual(Type.getType(Class.class), new Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
        generatorAdapter.dup();
        generatorAdapter.visitInsn(Opcodes.ICONST_1);
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("setAccessible", "(Z)V"));
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));//[InvocationDispatcher]
        return this;
    }

    public InvocationBuilder loadInvocationDispatcherKey(String key) {//key is java/lang/ProcessBuilder.start
        //[InvocationDispatcher,"java/lang/ProcessBuilder.start",null]
        generatorAdapter.visitLdcInsn(key);
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        return this;
    }



    public InvocationBuilder loadArray(Runnable[] runnables) {

        generatorAdapter.push(runnables.length);//[1]
        Type localType = Type.getObjectType("java/lang/Object");
        generatorAdapter.newArray(localType);//new Object[1],[objects ref]
        //[InvocationDispatcher,"java/lang/ProcessBuilder.start",null,1,objects ref]

        for (int i = 0; i < runnables.length; i++) {
            generatorAdapter.dup();//[objects ref,objects ref]
            generatorAdapter.push(i);//[0,objects ref,objects ref]
            //[InvocationDispatcher,"java/lang/ProcessBuilder.start",null,1,objects ref,objects ref,0]

            /**
             * loadThis();//run方法的堆栈[this]
             * invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder"), new Method("command", ""));////run方法的堆栈[ List<String> ref]
             */
            runnables[i].run();
            generatorAdapter.arrayStore(localType);//将List<String> 放到临时变量区
        }
        return this;
    }



    public InvocationBuilder invokeDispatcher() {
        return invokeDispatcher(true);
    }

    public InvocationBuilder invokeDispatcher(boolean pop) {
        //[InvocationDispatcher,"java/lang/ProcessBuilder.start",null,1,objects ref]

        generatorAdapter.invokeInterface(Type.getType(InvocationHandler.class)
                , new Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
        if (pop)
            generatorAdapter.pop();
        return this;
    }




    public InvocationBuilder loadArgumentsArray(String methodDesc) {
        Method localMethod = new Method("dummy", methodDesc);
        generatorAdapter.push(localMethod.getArgumentTypes().length);
        Type localType = Type.getType(Object.class);
        generatorAdapter.newArray(localType);
        for (int i = 0; i < localMethod.getArgumentTypes().length; i++) {
            generatorAdapter.dup();
            generatorAdapter.push(i);
            generatorAdapter.loadArg(i);
            generatorAdapter.arrayStore(localType);
        }
        return this;
    }

    public InvocationBuilder printToInfoLogFromBytecode(final String paramString) {
        loadInvocationDispatcher();
        generatorAdapter.visitLdcInsn("PRINT_TO_INFO_LOG");
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        loadArray(new Runnable[]{new Runnable() {
            @Override
            public void run() {
                generatorAdapter.visitLdcInsn(paramString);
            }
        }});
        invokeDispatcher();
        return this;
    }
}
