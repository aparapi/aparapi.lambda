package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/8/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Type {
    protected Class clazz;
    protected int javaSlots;

    Class getClazz() {
        return (clazz);
    }

    int getJavaSlots() {
        return (javaSlots);
    }

    String getJavaName() {
        return (clazz.getName());
    }

    Type(Class _clazz, int _javaSlots) {
        clazz = _clazz;
        javaSlots = _javaSlots;

    }

    abstract String getHSAName();

}
