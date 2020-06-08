package com.ayware.smarthome.models;

import com.ayware.smarthome.enums.ItemType;

import java.io.Serializable;

public class Item implements Serializable {

    private String title;
    private ItemType itemType;
    private String field;
    private int value;
    private boolean isAvailable;
    private static Item instance;

    public static Item getInstance() {
        if(instance == null)
            instance = new Item();
        return instance;
    }

    public static class Entity{

        public static final String COLUMN_TITLE = "item_title",
        COLUMN_ITEM_TYPE = "item_type",
        COLUMN_FIELD = "item_field",
        COLUMN_VALUE = "item_value";

        public static final String TABLE_NAME = "items";

        public static final String CREATE_TABLE = " create table "+TABLE_NAME+" ( " +
                COLUMN_TITLE+" text," +
                COLUMN_ITEM_TYPE+" text," +
                COLUMN_FIELD+" text primary key," +
                COLUMN_VALUE+" integer" +
                " ) ";

    }


    public Item() {
    }

    public Item(String title, ItemType itemType, String field, int value) {
        this.title = title;
        this.itemType = itemType;
        this.field = field;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", itemType=" + itemType +
                ", field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
