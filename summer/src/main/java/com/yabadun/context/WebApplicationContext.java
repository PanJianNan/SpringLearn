package com.yabadun.context;

/**
 * WebApplicationContext
 *
 * @author panjn
 * @date 2019/2/4
 */
public interface WebApplicationContext {
    /**
     * Context attribute to bind root WebApplicationContext to on successful startup.
     * <p>Note: If the startup of the root context fails, this attribute can contain
     * an exception or error as value. Use WebApplicationContextUtils for convenient
     * lookup of the root WebApplicationContext.
     */
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

    void refresh();

    Object getBean(String name) throws Exception;
}
