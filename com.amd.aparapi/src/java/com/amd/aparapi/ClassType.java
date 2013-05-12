package com.amd.aparapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/8/13
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassType extends Type {
    ClassType(Class _clazz) {
        super(_clazz, 1);
    }

    @Override
    public String getHSAName() {
        return ("u64");
    }

    static Map<Class, ClassType> map = new HashMap<Class, ClassType>();

    static synchronized ClassType getClassType(Class _clazz) {
        ClassType classType = map.get(_clazz);
        if (classType == null) {
            classType = new ClassType(_clazz);
            map.put(_clazz, classType);

        }
        return (classType);
    }
}
