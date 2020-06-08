package com.ayware.smarthome;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ayware.smarthome.utils.UrlUtil;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEtChannelId, mEtAPIKey;
    private Button mBtSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mToolbar = findViewById(R.id.settings_act_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Ayarlar");
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this,R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mEtChannelId = findViewById(R.id.settings_act_etChannelId);
        mEtAPIKey = findViewById(R.id.settings_act_etWriteAPIKey);
        mBtSave = findViewById(R.id.settings_act_btSave);

        mBtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(mEtAPIKey.getText())){
                    Toast.makeText(SettingsActivity.this, "API Key boş bırakılamaz", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(mEtChannelId.getText())){

                    Toast.makeText(SettingsActivity.this, "Kanal Id boş bırakılamaz", Toast.LENGTH_SHORT).show();
                    return;
                }

                UrlUtil.saveUrl(getApplicationContext(), mEtAPIKey.getText().toString(),mEtChannelId.getText().toString());
                onBackPressed();
            }
        });

         mEtAPIKey.setText(UrlUtil.getApiKey(this));
         mEtChannelId.setText(UrlUtil.getChannelId(this));

    }
}
