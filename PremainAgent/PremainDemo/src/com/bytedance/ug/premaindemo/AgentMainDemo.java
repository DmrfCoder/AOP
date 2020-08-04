package com.bytedance.ug.premaindemo;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

/**
 * @author dmrfcoder
 * @date 2020-07-21
 */
public class AgentMainDemo {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
        for (VirtualMachineDescriptor descriptor : virtualMachineDescriptors) {
            System.out.println("当前处理的是："+descriptor.displayName());
            if (descriptor.displayName().endsWith("AgentTarget")) {
                VirtualMachine virtualMachine = VirtualMachine.attach(descriptor.id());
                virtualMachine.loadAgent("/Users/dmrfcoder/Desktop/AgentMain.jar", "参数1");
                virtualMachine.detach();

            }
        }
    }
}
