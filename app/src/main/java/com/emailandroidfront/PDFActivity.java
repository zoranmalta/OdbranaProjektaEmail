package com.emailandroidfront;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        PDFView pdfView=findViewById(R.id.pdfView);


        Intent intent=getIntent();
        String fileName=intent.getStringExtra("fileName");
        if(fileName.endsWith(".pdf")) {
            pdfView.fromAsset(fileName).load();
        }
    }
}
