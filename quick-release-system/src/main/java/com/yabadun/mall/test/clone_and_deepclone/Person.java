package com.yabadun.mall.test.clone_and_deepclone;

import java.io.*;

/**
 * Person
 *
 * @author panjn
 * @date 2016/4/12
 */
public class Person implements Cloneable, Serializable{
    private String name;
    private int age;
    private Person father;
    private Person monther;

    public Person(String name, int age, Person father, Person monther) {
        this.name = name;
        this.age = age;
        this.father = father;
        this.monther = monther;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMonther() {
        return monther;
    }

    public void setMonther(Person monther) {
        this.monther = monther;
    }

    @Override
    public Object clone() {
        Person person = null;
        try {
            person = (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return person;
    }

    public Object deepClone() throws IOException, ClassNotFoundException {
        /* 写入当前对象的二进制流 */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//ByteArrayOutputStream具体组件
        ObjectOutputStream oos = new ObjectOutputStream(baos);//ObjectOutputStream具体装饰，OutputStream相当于抽象组件和装饰
        oos.writeObject(this);

        /* 读出二进制流产生的新对象 */
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
