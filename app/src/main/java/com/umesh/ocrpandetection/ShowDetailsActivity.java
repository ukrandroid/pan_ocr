package com.umesh.ocrpandetection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_textview);
        TextView textView = findViewById(R.id.textViewMsg);
        textView.setText(getIntent().getExtras().getString("text"));
    }
}
