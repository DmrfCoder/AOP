package com.bytedance.ug.agentmain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author dmrfcoder
 * @date 2020-07-21
 */
public class AgentMain {

    public static final Set<String> dx = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[]{"dx", "dx.bat"})));
    public static final Set<String> java = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[]{"java", "java.exe"})));
    public static String attachParams;

    private static Instrumentation instrumentation;

    public static void agentmain(String agentArgs, Instrumentation ins) {
        instrumentation = ins;
        System.out.println("AgentMain.agentmain方法被执行，传入的参数为：" + agentArgs);
        premain(agentArgs, ins);
    }


    public static void premain(String args, Instrumentation inst) {
        Log log = new ConsoleLog();

        IClassTransformer modifier = new ClassTransformer(log);

        try {
            Field treeLock = Logger.class.getDeclaredField("treeLock");
            treeLock.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(treeLock, treeLock.getModifiers() & 0xFFFFFFEF);//去掉final
            if (!(treeLock.get(null) instanceof InvocationDispatcher)) {
                treeLock.set(null, new InvocationDispatcher(log));
            }

            inst.addTransformer(modifier);
            Class[] classes = inst.getAllLoadedClasses();
            ArrayList<Class> classesToBeTransform = new ArrayList<>();
            for (Class cls : classes) {
                if (modifier.transforms(cls)) {
                    classesToBeTransform.add(cls);
                }
            }
            if (!classesToBeTransform.isEmpty()) {
                if (inst.isRetransformClassesSupported()) {
                    log.d("retransform classes: " + classesToBeTransform);
                    inst.retransformClasses(classesToBeTransform.toArray(new Class[classesToBeTransform.size()]));//对于已经加载的类重新进行转换处理，即会触发重新加载类定义
                } else {
                    log.e("unable to transform classes: " + classesToBeTransform);
                }
            }

            //重定义ProcessBuilder
            redefineClass(inst, modifier, ProcessBuilder.class);


        } catch (Exception e) {

        }


    }

    private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer transformer, Class<?> klass)
            throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException {
        String internalName = klass.getName().replace('.', '/');
        String fullName = internalName + ".class";
        ClassLoader classLoader = klass.getClassLoader() == null ? AgentMain.class.getClassLoader() : klass.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fullName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamUtil.copy(inputStream, outputStream);
        inputStream.close();
        //走ClassTransformer.transform去重定义类
        byte[] arrayOfByte = transformer.transform(klass.getClassLoader(), internalName, klass
                , null, outputStream.toByteArray());

        ClassDefinition classDefinition = new ClassDefinition(klass, arrayOfByte);
        instrumentation.redefineClasses(classDefinition);
    }

    public static String getAgentPath() throws URISyntaxException {
        return new File(AgentMain.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
    }

}
