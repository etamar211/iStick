package com.ed.istick;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;


public class StartActivity extends AppCompatActivity{
    /*
    *this activity will handle the option of connection
     */
    private ProgressBar LS;
    private Button connectButt;
    private Button scanButt;
    private TextView connectingText;
    private final int MIN_SCAN_DATA_LEN = 16;
    private final int MAX_SCAN_DATA_LEN = 24;
    private final int PASS_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //setting the view variables
        LS = (ProgressBar)findViewById(R.id.LodingSymbol);
        connectButt = (Button) findViewById(R.id.ConnectButt);
        scanButt = (Button) findViewById(R.id.ScanButton);
        connectingText = (TextView) findViewById(R.id.ConnectingText);

        //setting the capture intent and modes
        final IntentIntegrator barCodeCaptureIntent = new IntentIntegrator(this); // `this` is the current Activity
        barCodeCaptureIntent.setCaptureActivity(CaptureActivityAnyOrientation.class);
        barCodeCaptureIntent.setOrientationLocked(false);

        connectButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //open the connect by Ip & pass screen
                Intent loginScreen = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginScreen);
            }
        });

        scanButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //initiate the scan
                barCodeCaptureIntent.initiateScan();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take the scan data
        String contents = null;
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");
                //check if the data is usable
                if((contents.contains("|")) && (contents.length() >= MIN_SCAN_DATA_LEN) && (contents.length() <= MAX_SCAN_DATA_LEN)){
                    String IP = contents.substring(0, contents.indexOf('|'));
                    String pass = contents.substring(contents.indexOf('|') + 1, contents.length());
                    //check if the IP part is really IP and the Pass is in the right length
                    if((ClientLogic.validIP(IP)) && (pass.length() == PASS_LENGTH)){
                        //the data seems right, start the ConnectToServer AsyncTask
                        new ConnectToServer().execute(IP, pass);

                    }
                    else{
                        Toast.makeText(this, "The Data is not iStick's\nDoes the Barcode is really our's?", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(this, "The Data is not iStick's\nDoes the Barcode is really our's?", Toast.LENGTH_LONG).show();
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this, "the scan didn't go as plan\ntry to use the \"By IP & Pass option", Toast.LENGTH_LONG).show();
            }

    }

    private class ConnectToServer extends AsyncTask<String, Integer, Long> {
        /* this async task will create the connection to server (with the createConnection function
        * in ClientLogic)
        * Also, the Task will handle the UI (progress bar icon)
         */
        protected void onPreExecute(){
            //set the UI
            scanButt.setVisibility(View.INVISIBLE);
            connectButt.setVisibility(View.INVISIBLE);
            LS.setVisibility(View.VISIBLE);
            connectingText.setVisibility(View.VISIBLE);
        }

        protected Long doInBackground(String... data) {
            //create the connection with the server
            long result = 0;
            try {
                Globals g = Globals.getInstance();
                ClientLogic CL = new ClientLogic(data[0], data[1]);
                Thread createClientLogic = new Thread(CL);
                createClientLogic.start();
                createClientLogic.join();
                if(CL.getStatus()){
                    g.setCL(CL);
                    result = 1;
                }
                else{
                    //connection didn't sucssesfull
                    result = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Long result) {
            //set the UI
            LS.setVisibility(View.GONE);
            connectingText.setVisibility(View.GONE);
            connectButt.setVisibility(View.VISIBLE);
            scanButt.setVisibility(View.VISIBLE);
            if(result == 1){
                //connection created susecfully, open the template activity
                Intent templateChoose = new Intent(StartActivity.this, TemplateChooseOptions.class);
                startActivity(templateChoose);
            }
            else{
                //connection failed, return the view to how it was
                Globals g = Globals.getInstance();
                Toast.makeText(StartActivity.this, "the connection didn't go as plan\n" + g.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(Globals.getInstance().getCL() != null){
            if(Globals.getInstance().getCL().isErrStatus()){
                Toast.makeText(StartActivity.this, "The connection with the server ended", Toast.LENGTH_LONG).show();
            }
        }
    }


}

class CaptureActivityAnyOrientation extends CaptureActivity {
    //this class is making the Barcode Capture start in portrait mode
}


