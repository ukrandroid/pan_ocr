package com.umesh.ocrpandetection;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CameraSource mCameraSource;
    TextAdapter textAdapter;
    ArrayList<String> stringArrayList = new ArrayList<>();
    private SurfaceView surfaceView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        textAdapter = new TextAdapter(stringArrayList, this);
        recyclerView.setAdapter(textAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (textRecognizer.isOperational()) {
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(12890, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();


            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1001);

                            return;
                        }
                        mCameraSource.start(surfaceView.getHolder());


                    } catch (IOException ioe) {

                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
stringArrayList.clear();
                    final SparseArray<TextBlock> item = detections.getDetectedItems();
                    if (item.size() > 0) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < item.size(); i++) {
                                    TextBlock ite = item.valueAt(i);
                                    stringBuilder.append(ite.getValue());

                                    //String pattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
                                    String pattern = "[A-Za-z]{5}\\d{4}[A-Za-z]{1}";
                                    Pattern r = Pattern.compile(pattern);
                                    final Matcher m = r.matcher(stringBuilder.toString());

                                    while (m.find()) {
                                        mCameraSource.stop();
                                        final String panNo = m.group(0);
                                        Log.d("Found value: ", "Found value: " + m);
                                        Constant.ShowAlertDialog(MainActivity.this, "Pan card details found: " + panNo, "Success", "Ok", "", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                    return;
                                                }
                                                try {
                                                    mCameraSource.start(surfaceView.getHolder());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                               /* Intent intent = new Intent(MainActivity.this, ShowDetailsActivity.class);
                                                intent.putExtra("text", panNo);
                                                startActivity(intent);*/
                                            }
                                        }, null);
                                    }
                                    stringArrayList.add(stringBuilder.toString());
                                    textAdapter.notifyDataSetChanged();


                                }
                            }
                        });
                    }
                }
            });
        } else {
            Log.d("Not Woking", "not working");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        mCameraSource.start(surfaceView.getHolder());
                    } catch (IOException ioe) {

                    }

                }
        }
    }

    @Override
    public void onClick(View v) {
        String str = (String) v.getTag();
        Intent intent = new Intent(MainActivity.this, ShowDetailsActivity.class);
        intent.putExtra("text", str);
        startActivity(intent);
    }
}
