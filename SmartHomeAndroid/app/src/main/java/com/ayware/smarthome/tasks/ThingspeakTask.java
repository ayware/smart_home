package com.ayware.smarthome.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.ayware.smarthome.databases.DatabaseItem;
import com.ayware.smarthome.enums.ThingspeakRequestType;
import com.ayware.smarthome.listeners.OnDataListener;
import com.ayware.smarthome.models.Item;
import com.ayware.smarthome.utils.UrlUtil;

import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThingspeakTask extends AsyncTask<Void,Void,Void> {

    private Context mContext;
    private Exception mErr;
    private String mPostUrl, mGetUrl;
    private OnDataListener mOnDataListener;
    private ThingspeakRequestType mRequestType;
    private List<Item> mItemList;
    private DatabaseItem mDatabaseItem;

    public ThingspeakTask(Context context, OnDataListener onDataListener,ThingspeakRequestType requestType){
        this.mContext = context;
        this.mOnDataListener = onDataListener;
        this.mRequestType = requestType;
        mGetUrl = UrlUtil.getGetUrl(context);
        mPostUrl = UrlUtil.getPostUrl(context);
        mDatabaseItem = new DatabaseItem(context);
        mItemList = mDatabaseItem.getItems();

    }



    @Override
    protected Void doInBackground(Void... voids) {

        try {

            OkHttpClient client = new OkHttpClient();
            Request request;
            Response response;

            if(mRequestType == ThingspeakRequestType.POST_DATA) {

                for (Item item : mItemList) {

                    mPostUrl += "&" + item.getField() + "=" + item.getValue();

                }


                request = new Request.Builder()
                        .url(mPostUrl)
                        .get()
                        .build();

                response = client.newCall(request)
                        .execute();


                int code = Integer.valueOf(response.body().string());
                if (!response.isSuccessful() || code == 0) {
                    mErr = new Exception("err");
                }
            }

            else if(mRequestType == ThingspeakRequestType.GET_DATA){

                request = new Request.Builder()
                        .url(mGetUrl)
                        .get()
                        .build();

                response = client.newCall(request)
                        .execute();

                JSONObject obj = new JSONObject(response.body().string());
                JSONObject feedObj = obj.optJSONArray("feeds")
                        .getJSONObject(0);

                for(Item item: mItemList){

                    item.setValue(feedObj.optInt(item.getField(),-1));

                }

                mDatabaseItem.updateListValue(mItemList);

            }

        }catch (Exception e){
            this.mErr = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(mOnDataListener != null)
            mOnDataListener.onDataCompleted(mItemList,mErr);

    }
}
