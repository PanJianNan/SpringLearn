package com.yabadun.mall.test.singleton;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by panjiannan on 2018/7/27.
 */
public class Person implements Serializable {

    private static class InstanceHolder {
        private static final Person instatnce = new Person("John", 31, Gender.MALE);
    }

    public static Person getInstance() {
        return InstanceHolder.instatnce;
    }

    private String name = null;

    private Integer age = null;

    private Gender gender = null;

    private Person() {
        System.out.println("none-arg constructor");
    }

    private Person(String name, Integer age, Gender gender) {
        System.out.println("arg constructor");
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    private Object readResolve() throws ObjectStreamException {
        return InstanceHolder.instatnce;
    }

}
