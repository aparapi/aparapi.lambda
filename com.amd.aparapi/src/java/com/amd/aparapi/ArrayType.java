package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/8/13
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayType extends Type {
    int dimensions;
    Type elementType;

    ArrayType(Class _clazz, Type _elementType, int _dimensions) {
        super(_clazz, 1);
        elementType = _elementType;
        dimensions = _dimensions;

    }

    public int getDimensions() {

        return (dimensions);
    }

    public Type getElementType() {
        return (elementType);
    }

    @Override
    public String getHSAName() {
        return ("u64");
    }
}
