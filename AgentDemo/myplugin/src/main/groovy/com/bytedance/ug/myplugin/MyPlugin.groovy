package com.bytedance.ug.myplugin

import com.bytedance.ug.AgentClass
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project> {

    AgentClass aClass;

    void apply(Project project) {
        System.out.println("插件被调用啦~~~~~~~")
        aClass = new AgentClass();
        aClass.excute();


    }
}