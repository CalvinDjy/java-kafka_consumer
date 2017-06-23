package com.calvin.service.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 项目正式环境启动文件.
 *
 * @author lijing
 */
public final class Main {

    /**
     * 私有化Main.
     */
    private Main() {
    }

    /**
     * 服务程序入口.
     *
     * @param args 启动参数。
     */
    public static void main(final String[] args) {
        System.out.println("======================");
        new ClassPathXmlApplicationContext("application-context.xml");
        System.out.println("Paramida loopin service start...");
        System.out.println("======================");
    }
}
