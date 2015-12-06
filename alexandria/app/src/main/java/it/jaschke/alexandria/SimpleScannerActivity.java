package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        mScannerView.setAutoFocus(true);
        mScannerView.setFlash(true);
        //List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        //formats.add(BarcodeFormat.ISBN13);
        mScannerView.setFormats(BarcodeFormat.ALL_FORMATS);

        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("SCANNING", rawResult.getContents()); // Prints scan results
        Log.v("SCANNING", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        //Intent resultIntent = new Intent(SimpleScannerActivity.class, AddBook.class);

        Intent databackIntent = new Intent(this,AddBook.class);
        databackIntent.putExtra("SCAN_RESULT", rawResult.getContents());
        setResult(Activity.RESULT_OK, databackIntent);

        finish();
    }
}