package com.example.qr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    public static final int P_Code=1000;

    private EditText mText;
    private Button mGen,mScan;
    private ImageView mShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText=findViewById(R.id.text);
        mGen=findViewById(R.id.gen);
        mScan=findViewById(R.id.scan);
        mShow=findViewById(R.id.show);

        mGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text= mText.getText().toString().trim();
                if (text!= null && !text.isEmpty()){
                    try {
                        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                        BitMatrix bitMatrix= multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                        Bitmap bitmap= barcodeEncoder.createBitmap(bitMatrix);
                        mShow.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Please enter something first!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            String[] permission = {Manifest.permission.CAMERA};
            requestPermissions(permission,P_Code);
        }
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Scanning..");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        final IntentResult intentResult =IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Scan Result")
                    .setMessage(intentResult.getContents())
                    .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Context context;
                            String name;
                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("result", intentResult.getContents());
                            clipboardManager.setPrimaryClip(clipData);
                        }
                    }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


