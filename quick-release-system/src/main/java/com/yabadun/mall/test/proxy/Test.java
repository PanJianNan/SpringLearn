package com.yabadun.mall.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;

/**
 * Created by panjiannan on 2018/7/26.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(Test.class.getResource(""));
        System.out.println(Test.class.getResource("/"));
//        URL url = Test.class.getClassLoader().getResource("classpath:com/yabadun/mall/test/proxy/Test.class");
        URL url = Test.class.getClassLoader().getResource("com/yabadun/mall/test/proxy/Test.class");
//        URL url = Test.class.getClassLoader().getResource("Test.class");


        final JieKou jieKou = new ShiXian();
        Class[] cla1 = JieKou.class.getInterfaces();
        Class[] cla2 = jieKou.getClass().getInterfaces();
        Class[] cla3 = ShiXian.class.getInterfaces();
        JieKou jieKouProxy = (JieKou) Proxy.newProxyInstance( JieKou.class.getClassLoader(), jieKou.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("我开始说了");
                return method.invoke(jieKou, args);
            }
        });

        System.out.println(jieKouProxy.say("hello"));

        MyHandler handler = new Test().new MyHandler();
        //证明jdk动态代理可以直接代理接口
        JieKou jieKouProxy2 = (JieKou) Proxy.newProxyInstance(JieKou.class.getClassLoader(), new Class<?>[]{JieKou.class}, handler);
        jieKouProxy2.say("say say");
    }

    class MyHandler implements InvocationHandler {
        public String name;

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("我进来了");
            this.name = (String) args[0];
            return null;
        }
    }

}




