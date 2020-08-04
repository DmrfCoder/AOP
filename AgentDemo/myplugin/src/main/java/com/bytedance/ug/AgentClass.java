package com.bytedance.ug;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.management.ManagementFactory;


/**
 * @author dmrfcoder
 * @date 2020-07-21
 */

public class AgentClass {
    public void excute() {
        //java.lang.ProcessBuilder


        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        System.out.println("nameOfRunningVM:" + nameOfRunningVM + " ");
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(pid);
            vm.loadAgent("/Users/dmrfcoder/Desktop/openapmagent.jar", System.getProperty("openapm.agentArgs"));
            vm.detach();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }


       /* try {
            List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
            for (VirtualMachineDescriptor descriptor : virtualMachineDescriptors) {
                System.out.println("当前处理的类是："+descriptor.displayName());

                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                virtualMachine.loadAgent("/Users/dmrfcoder/Desktop/AgentMain.jar", "参数1");
                virtualMachine.detach();
//                if (descriptor.displayName().endsWith("ProcessBuilder")) {
//                    VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
//                    virtualMachine.loadAgent("/Users/dmrfcoder/Desktop/AgentMain.jar", "参数1");
//                    virtualMachine.detach();
//
//                }
            }
        } catch (Exception e) {
            System.out.println("出现了异常~~~~");
            e.printStackTrace();
        }*/
    }
}

