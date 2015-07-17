package com.funfvier.anjavadoc.crawler.entity;

/**
 * Created by lshi on 08.07.2015.
 */
public enum MemberType {
    METHOD(1),
    FIELD(2),
    CONSTRUCTOR(3),
    ENUM_CONSTANT(4),
    ELEMENT(5);

    private final int type;

    public int getId() {
        return type;
    }

    MemberType(int type) {
        this.type = type;
    }
}
