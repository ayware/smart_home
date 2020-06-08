package com.ayware.smarthome.listeners;

import com.ayware.smarthome.models.Item;

import java.util.List;

public interface OnDataListener {

    public void onDataCompleted(List<Item> itemList, Exception err);
}
