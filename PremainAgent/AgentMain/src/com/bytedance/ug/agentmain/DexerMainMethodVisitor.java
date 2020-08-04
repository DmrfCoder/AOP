package com.bytedance.ug.agentmain;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.logging.Logger;

/**
 * @author dmrfcoder
 * @date 2020-08-03
 */
public class DexerMainMethodVisitor extends AdviceAdapter {

    protected String methodName;
    protected String methodDesc;

    protected DexerMainMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(ASM5, methodVisitor, access, name, desc);
        methodName = name;
        methodDesc = desc;
    }


    @Override
    protected void onMethodEnter() {
        loadInvocationDispatcher(this);
        checkCast(Type.getType("[B"));
        storeArg(1);
    }


    //treeLock是一个Object对象，并且只是用来加锁的，没有别的用途，但treeLock是一个final成员，所以要修改其修饰，去掉final
    //拿到treeLock
    public void loadInvocationDispatcher(AdviceAdapter generatorAdapter) {
        generatorAdapter.visitLdcInsn(Type.getType(Logger.class));//压入操作数栈,visitLdc-->压栈 [Logger.class]
        generatorAdapter.visitLdcInsn("treeLock");//[Logger.class,"treeLock"]
        generatorAdapter.invokeVirtual(Type.getType(Class.class), new Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));//执行getDeclaredField，将结果压入栈顶（结果是field对象）[field]
        generatorAdapter.dup();//复制操作数栈顶的一个操作数，并将该操作数又压入一次栈[field,field]

        generatorAdapter.visitInsn(Opcodes.ICONST_1);//执行操作码为Opcodes.ICONST_1的指令，将常量1(true)加载到操作数栈[field,field,true]
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("setAccessible", "(Z)V"));//在使用invokevirtual之前，我们需要先将method所在的Object ref压入堆栈，然后将方法的参数依次压入堆栈，然后使用invokevirtual，然后该方法的返回值会被存在栈顶
        //[field]

        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);//将NULL压入操作数栈//[field,null]
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        //[InvocationDispatcher]
        loadInvocationDispatcherKey("com/android/dx/command/dexer/Main." + methodName, generatorAdapter);
        //java/lang/ProcessBuilder.start
    }

    public void loadInvocationDispatcherKey(String key, AdviceAdapter generatorAdapter) {
        generatorAdapter.visitLdcInsn(key);//将"java/lang/ProcessBuilder.start"压栈[InvocationDispatcher,"java/lang/ProcessBuilder.start"]
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);//将NULL压栈[InvocationDispatcher,"java/lang/ProcessBuilder.start",null]
        loadArgumentsArray(methodDesc, generatorAdapter);

    }


    public void loadArgumentsArray(String methodDesc, AdviceAdapter generatorAdapter) {
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
        invokeDispatcher(false, generatorAdapter);
    }

    //调用invoke方法
    public void invokeDispatcher(boolean pop, AdviceAdapter generatorAdapter) {
        generatorAdapter.invokeInterface(Type.getType(InvocationHandler.class)
                , new Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
        if (pop) {
            generatorAdapter.pop();
        }
    }


}
