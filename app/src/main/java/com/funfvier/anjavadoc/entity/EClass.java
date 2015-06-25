package com.funfvier.anjavadoc.entity;

/**
 * Created by lshi on 25.06.2015.
 */
public class EClass {
    private int id;
    private String name;
    private String descriptionShort;
    private String descriptionLong;
    private int packageId;

    public EClass() {
    }

    public EClass(int id, String name, String descriptionShort, String descriptionLong, int packageId) {
        this.id = id;
        this.name = name;
        this.descriptionShort = descriptionShort;
        this.descriptionLong = descriptionLong;
        this.packageId = packageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionLong() {
        return descriptionLong;
    }

    public void setDescriptionLong(String descriptionLong) {
        this.descriptionLong = descriptionLong;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }
}
