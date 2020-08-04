package com.bytedance.ug.premaindemo;

/**
 * @author dmrfcoder
 * @date 2020-07-21
 */
public class AgentTarget {

    public void sayHello(String name) {
        System.out.println(String.format("%s say hello!", name));
    }

    public static void main(String[] args) throws Exception {
        AgentTarget sample = new AgentTarget();
        for (; ; ) {
            Thread.sleep(1000);
            sample.sayHello(Thread.currentThread().getName());
        }
    }
}
