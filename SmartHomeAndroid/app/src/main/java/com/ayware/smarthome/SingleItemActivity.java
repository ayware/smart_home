package com.ayware.smarthome;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ayware.smarthome.databases.DatabaseItem;
import com.ayware.smarthome.enums.ItemType;
import com.ayware.smarthome.models.Item;

public class SingleItemActivity extends AppCompatActivity {

    private EditText mEtTitle;
    private Spinner mSpType;
    private EditText mEtField;
    private EditText mEtDefValue;
    private Button mBtOkey;
    private Item mItem = null;
    private DatabaseItem mDatabaseItem;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item_activity);

        if(getIntent().getExtras() != null){

            Bundle extr = getIntent().getExtras();
            if(extr.containsKey("item"))
                mItem = (Item)extr.getSerializable("item");

        }

        mToolbar = findViewById(R.id.single_item_act_toolbar);
        mEtTitle = findViewById(R.id.single_item_act_etTitle);
        mEtField = findViewById(R.id.single_item_act_etField);
        mEtDefValue = findViewById(R.id.single_item_act_etValue);
        mSpType = findViewById(R.id.single_item_act_spType);
        mBtOkey = findViewById(R.id.single_item_act_btOkey);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Alan Düzenlemesi");
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this,R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDatabaseItem = new DatabaseItem(this);

        mSpType.setAdapter(new SpinnerAdapter() {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                if(convertView == null)
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

                TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
                tv.setText(getItem(position).title());

                return convertView;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return ItemType.values().length;
            }

            @Override
            public ItemType getItem(int position) {
                return ItemType.values()[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null)
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

                TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
                tv.setText(getItem(position).title());

                return convertView;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });

        mBtOkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = mItem;

                boolean isUpdate;
                if(item == null)
                {
                    item = new Item();
                    isUpdate = false;
                }
                else{
                    isUpdate = true;
                }

                if(TextUtils.isEmpty(mEtTitle.getText())){
                    {
                        Toast.makeText(SingleItemActivity.this, "Başlık kısmını doldurunuz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else
                    item.setTitle(mEtTitle.getText().toString());

                item.setItemType(ItemType.values()[mSpType.getSelectedItemPosition()]);

                if(TextUtils.isEmpty(mEtField.getText()))
                {
                    Toast.makeText(SingleItemActivity.this, "Field kısmını doldurunuz", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    item.setField(mEtField.getText().toString());

                if(TextUtils.isEmpty(mEtDefValue.getText()))
                {
                    Toast.makeText(SingleItemActivity.this, "Değer kısmını doldurunuz", Toast.LENGTH_SHORT).show();
                    return;
                }else
                    item.setValue(Integer.valueOf(mEtDefValue.getText().toString()));

                if(isUpdate) {
                    if (!mDatabaseItem.updateItem(item)) {
                        Toast.makeText(SingleItemActivity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                else {

                        if (!mDatabaseItem.addItem(item)) {
                            Toast.makeText(SingleItemActivity.this, "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                onBackPressed();

            }
        });

        if(mItem != null){

            mEtTitle.setText(mItem.getTitle());
            mEtField.setText(mItem.getField());
            mEtDefValue.setText(String.valueOf(mItem.getValue()));
            mSpType.setSelection(mItem.getItemType().ordinal());

        }

    }


}
