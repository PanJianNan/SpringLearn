package com.yabadun.factory;

/**
 * ObjectFactory
 *
 * @author panjn
 * @date 2019/2/5
 */
public interface ObjectFactory<T> {

    /**
     * Return an instance (possibly shared or independent)
     * of the object managed by this factory.
     * @return an instance of the bean (should never be {@code null})
     */
    T getObject() throws Exception;

}
