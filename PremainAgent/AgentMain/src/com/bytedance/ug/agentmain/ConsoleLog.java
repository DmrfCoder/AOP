package com.bytedance.ug.agentmain;


/**
 * @author dmrfcoder
 * @date 2020-07-30
 */
public class ConsoleLog implements Log {
    public ConsoleLog() {
    }

    public void v(String log) {
        System.out.println("[openapm.v] " + log);
    }

    public void d(String log) {
        System.out.println("[openapm.d] " + log);
    }

    public void w(String log) {
        System.err.println("[openapm.w] " + log);
    }

    public void w(String log, Throwable throwable) {
        System.err.println("[openapm.w] " + log);
        throwable.printStackTrace(System.err);
    }

    public void e(String log) {
        System.err.println("[openapm.e] " + log);
    }

    public void e(String log, Throwable throwable) {
        System.err.println("[openapm.e] " + log);
        throwable.printStackTrace(System.err);
    }
}
