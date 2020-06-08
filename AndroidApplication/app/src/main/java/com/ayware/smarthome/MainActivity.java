package com.ayware.smarthome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayware.smarthome.adapters.AdapterList;
import com.ayware.smarthome.databases.DatabaseItem;
import com.ayware.smarthome.enums.ThingspeakRequestType;
import com.ayware.smarthome.listeners.OnAdapterListener;
import com.ayware.smarthome.listeners.OnDataListener;
import com.ayware.smarthome.models.Item;
import com.ayware.smarthome.tasks.ThingspeakTask;
import com.ayware.smarthome.utils.UrlUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdapterList mAdapterList;
    private RecyclerView mRv;
    private ProgressBar mPb;
    private TextView mTvTime;
    private DatabaseItem mDatabaseItem;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mRv = findViewById(R.id.main_activity_rvList);
        mPb = findViewById(R.id.main_activity_pb);
        mTvTime = findViewById(R.id.main_activity_tvTime);
        mToolbar = findViewById(R.id.main_activity_toolbar);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.app_name));

        mDatabaseItem = new DatabaseItem(this);


        mAdapterList = new AdapterList(this, new OnAdapterListener() {

            @Override
            public void onDataChange(Item item) {

                mDatabaseItem.updateItem(item);
                sendData();

            }


        });
        mAdapterList.updateList(mDatabaseItem.getItems());

        mRv.setLayoutManager(new GridLayoutManager(this,2));

        mRv.setAdapter(mAdapterList);

        getData();


    }

    private void sendData(){


        if(UrlUtil.isEmpty(this)){

            Toast.makeText(this, "Lütfen ayarları yapınız", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));


        }else {

            mPb.setVisibility(View.VISIBLE);

            new ThingspeakTask(this, new OnDataListener() {
                @Override
                public void onDataCompleted(List<Item> itemList, Exception err) {
                    mPb.setVisibility(View.GONE);

                    if (err != null) {

                        Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    getData();


                }
            }, ThingspeakRequestType.POST_DATA)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.add("Ayarlar")
                .setIcon(ContextCompat.getDrawable(this,R.drawable.ic_more_vert_black_24dp))
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));

                        return true;

                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }


    private void getData(){

        if(UrlUtil.isEmpty(this)){

            Toast.makeText(this, "Lütfen ayarları yapınız", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));


        }else {

            new ThingspeakTask(this, new OnDataListener() {
                @Override
                public void onDataCompleted(List<Item> itemList, Exception err) {

                    if (err != null) {

                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    mTvTime.setText(sdf.format(Calendar.getInstance().getTime()));
                    mAdapterList.updateList(mDatabaseItem.getItems());

                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    getData();

                                }
                            }, 1000);

                }
            }, ThingspeakRequestType.GET_DATA)
                    .execute();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try{

            mAdapterList.updateList(mDatabaseItem.getItems());

        }catch (Exception e){

        }
    }
}
