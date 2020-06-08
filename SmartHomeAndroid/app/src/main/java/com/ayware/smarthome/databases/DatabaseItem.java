package com.ayware.smarthome.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ayware.smarthome.enums.ItemType;
import com.ayware.smarthome.models.Item;

import java.util.ArrayList;
import java.util.List;

public class DatabaseItem extends SQLiteOpenHelper {

    private static final String DB_NAME = "items";
    private static final int DB_VERSION = 1;

    public DatabaseItem(Context context){
        super(context,DB_NAME,null,DB_VERSION);

    }

    private ContentValues createValues(Object model){

        ContentValues values = new ContentValues();

        if(model instanceof Item){

            Item item = (Item)model;

            if(item.getField() != null)
                values.put(Item.Entity.COLUMN_FIELD,item.getField());
            if(item.getItemType() != null)
                values.put(Item.Entity.COLUMN_ITEM_TYPE,item.getItemType().name());
            if(item.getTitle() != null)
                values.put(Item.Entity.COLUMN_TITLE,item.getTitle());
            if(item.getValue() != -1)
                values.put(Item.Entity.COLUMN_VALUE,item.getValue());

        }

        return values;
    }

    private Object[] createCursor(Cursor cursor,Object... instances){

        Object[] o = new Object[instances.length];

        for(int i=0; i<instances.length; i++){

            Object obj = instances[i];

            if(obj instanceof Item){

                Item item = new Item();

                int iField = cursor.getColumnIndex(Item.Entity.COLUMN_FIELD);
                int iTitle = cursor.getColumnIndex(Item.Entity.COLUMN_TITLE);
                int iValue = cursor.getColumnIndex(Item.Entity.COLUMN_VALUE);
                int iType = cursor.getColumnIndex(Item.Entity.COLUMN_ITEM_TYPE);

                if(iField != -1)
                    item.setField(cursor.getString(iField));
                if(iTitle != -1)
                    item.setTitle(cursor.getString(iTitle));
                if(iValue != -1)
                    item.setValue(cursor.getInt(iValue));
                if(iType != -1)
                    item.setItemType(ItemType.valueOf(cursor.getString(iType)));

                o[i] = item;
            }

        }
        return o;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Item.Entity.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public boolean addItem(Item item){

        System.out.println("addItem:"+item.toString());

        boolean okey;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {

            Cursor cr = db.rawQuery("select count(*) as c from "+Item.Entity.TABLE_NAME+" where "+Item.Entity.COLUMN_FIELD+"="+"'"+item.getField()+"'",null,null);

            int _count = 0;
            if(cr.moveToNext())
                _count = cr.getInt(cr.getColumnIndex("c"));

            if(_count == 0) {

                db.insertOrThrow(Item.Entity.TABLE_NAME, null, createValues(item));
                db.setTransactionSuccessful();
                okey = true;

            }else{
                okey = false;
            }

            cr.close();


        }catch (Exception e){

            okey = false;

        }finally {

            db.endTransaction();
            db.close();
        }

        return okey;

    }

    public boolean updateItem(Item item){

        boolean okey;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{


            db.update(Item.Entity.TABLE_NAME,createValues(item),Item.Entity.COLUMN_FIELD+"="+"'"+item.getField()+"'",null);
            db.setTransactionSuccessful();
            okey = true;

        }catch (Exception e)
        {

            okey = false;

        }finally {
            db.endTransaction();
            db.close();
        }

        return okey;

    }

    public void updateList(List<Item> itemList){

        if(itemList == null)
            return;

        for(Item item : itemList)
            updateItem(item);

    }

    public void updateListValue(List<Item> itemList){

        if(itemList == null)
            return;

        for(Item item : itemList)
        {
            item.setTitle(null);
            item.setItemType(null);
            updateItem(item);
        }
    }

    public List<Item> getItems(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+Item.Entity.TABLE_NAME+" order by "+Item.Entity.COLUMN_FIELD+" asc",null,null);
        List<Item> itemList = new ArrayList<>();

        while (cursor.moveToNext())
            itemList.add((Item)createCursor(cursor,Item.getInstance())[0]);

        cursor.close();
        db.close();

        return itemList;
    }
}
