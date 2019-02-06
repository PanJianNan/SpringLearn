package com.yabadun.factory;

/**
 * BeanDefinition
 *
 * @author panjn
 * @date 2019/2/5
 */
public interface BeanDefinition {

    String SCOPE_DEFAULT = "";
    /**
     * Scope identifier for the standard singleton scope: "singleton".
     * <p>Note that extended bean factories might support further scopes.
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: "prototype".
     * <p>Note that extended bean factories might support further scopes.
     */
    String SCOPE_PROTOTYPE = "prototype";

    boolean isSingleton();
}
