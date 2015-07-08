package com.funfvier.anjavadoc.crawler;

/**
 * Created by lshi on 08.07.2015.
 */
public enum ClassType {
    INTERFACE(0){
        @Override
        public boolean isInterface() {
            return true;
        }
    },
    CLASS(1) {
        @Override
        public boolean isClass() {
            return true;
        }
    },
    ENUM(2){
        @Override
        public boolean isEnum() {
            return true;
        }
    },
    EXCEPTION(3){
        @Override
        public boolean isException() {
            return true;
        }
    },
    ANNOTATION(4) {
        @Override
        public boolean isAnnotation() {
            return true;
        }
    },
    ERROR(5) {
        @Override
        public boolean isError() {
            return true;
        }
    };

    private int type;

    ClassType(int type) {
        this.type = type;
    }

    public boolean isInterface() {return false;}
    public boolean isClass() {return false;}
    public boolean isEnum() {return false;}
    public boolean isException() {return false;}
    public boolean isAnnotation() {return false;}
    public boolean isError() {return false;}
 }
