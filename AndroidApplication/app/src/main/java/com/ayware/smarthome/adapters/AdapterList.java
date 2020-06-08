package com.ayware.smarthome.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayware.smarthome.R;
import com.ayware.smarthome.SingleItemActivity;
import com.ayware.smarthome.listeners.OnAdapterListener;
import com.ayware.smarthome.models.Item;

import java.util.ArrayList;
import java.util.List;

public class AdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Item> mItemList;
    private OnAdapterListener mOnListener;

    public AdapterList(Context context){
        this.mContext = context;
        this.mItemList = new ArrayList<>();
    }

    public AdapterList(Context context, OnAdapterListener onAdapterListener){
        this(context);
        this.mOnListener = onAdapterListener;
    }

    public AdapterList(Context context,OnAdapterListener adapterListener, List<Item> itemList){
        this(context,adapterListener);

        if(itemList == null)
            itemList = new ArrayList<>();

        this.mItemList = itemList;
    }


    public void updateList(List<Item> itemList){
        if(itemList == null)
            itemList = new ArrayList<>();
        this.mItemList = itemList;
        this.notifyDataSetChanged();
    }

    private class HolderItem extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private ViewGroup rootOnOffType,
                            rootReadType,
                            rootValueType,
                            rootAddMore;
        private ImageButton btOnOff;
        private Button btSendValue;
        private EditText etValue;
        private TextView tvRead;
        private ImageButton mIbAdd;
        private ImageButton ibEdit;

        private HolderItem(View v){
            super(v);

            tvTitle = v.findViewById(R.id.item_tvTitle);
            rootOnOffType = v.findViewById(R.id.item_rootOnOffType);
            rootReadType = v.findViewById(R.id.item_rootReadType);
            rootValueType = v.findViewById(R.id.item_rootValueType);
            btSendValue = v.findViewById(R.id.item_btSendValue);
            btOnOff = v.findViewById(R.id.item_btOnOff);
            etValue = v.findViewById(R.id.item_etValue);
            tvRead = v.findViewById(R.id.item_tvRead);
            rootAddMore = v.findViewById(R.id.item_rootAddMore);
            mIbAdd = v.findViewById(R.id.item_btAdd);
            ibEdit = v.findViewById(R.id.item_ibEdit);


        }

        private void bindItem(final Item item){

            if(item == null){

                rootOnOffType.setVisibility(View.GONE);
                rootReadType.setVisibility(View.GONE);
                rootValueType.setVisibility(View.GONE);
                rootAddMore.setVisibility(View.VISIBLE);
                tvTitle.setText("-- Yeni Alan Ekle --");
                ibEdit.setVisibility(View.GONE);

                mIbAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mContext.startActivity(new Intent(mContext, SingleItemActivity.class));

                    }
                });


                return;
            }

            ibEdit.setVisibility(View.VISIBLE);
            ibEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, SingleItemActivity.class);
                    intent.putExtra("item",item);
                    mContext.startActivity(intent);

                }
            });

            tvTitle.setText(item.getTitle());

            switch (item.getItemType()){

                case ON_OFF:

                    rootOnOffType.setVisibility(View.VISIBLE);
                    rootReadType.setVisibility(View.GONE);
                    rootValueType.setVisibility(View.GONE);
                    rootAddMore.setVisibility(View.GONE);

                    if(item.getValue() == 1)
                        btOnOff.setImageResource(R.drawable.ic_on);
                    else
                        btOnOff.setImageResource(R.drawable.ic_off);

                    btOnOff.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(item.getValue() == 0)
                                item.setValue(1);
                            else
                                item.setValue(0);

                            if(mOnListener != null)
                                mOnListener.onDataChange(item);

                        }
                    });

                    break;

                case READ:

                    rootOnOffType.setVisibility(View.GONE);
                    rootReadType.setVisibility(View.VISIBLE);
                    rootValueType.setVisibility(View.GONE);
                    rootAddMore.setVisibility(View.GONE);

                    tvRead.setText(String.valueOf(item.getValue()));

                    break;

                case VALUE:

                    rootOnOffType.setVisibility(View.GONE);
                    rootReadType.setVisibility(View.GONE);
                    rootValueType.setVisibility(View.VISIBLE);
                    rootAddMore.setVisibility(View.GONE);

                    etValue.setText(String.valueOf(item.getValue()));

                    btSendValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            item.setValue(Integer.valueOf(etValue.getText().toString()));

                            etValue.clearFocus();

                            if(mOnListener != null)
                                mOnListener.onDataChange(item);
                        }
                    });

                    break;

            }

        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderItem(LayoutInflater.from(mContext).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(position >= (mItemList.size())){

            if (holder instanceof HolderItem) {

                ((HolderItem) holder).bindItem(null);

            }


        }else {

            if (holder instanceof HolderItem) {

                ((HolderItem) holder).bindItem(mItemList.get(position));

            }

        }

    }


    @Override
    public int getItemCount() {
        return mItemList.size() + 1;
    }
}
