package com.example.aquaculture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.pm.PackageManager;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SCAN QR CODE");
        scannerView = new ZXingScannerView(QRScannerActivity.this);
        setContentView(scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(QRScannerActivity.this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(QRScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
