package com.funfvier.anjavadoc.entity;

/**
 * Created by lshi on 25.06.2015.
 */
public class EPackage {
    private String name;
    private String description;

    public EPackage() {
    }

    public EPackage(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
