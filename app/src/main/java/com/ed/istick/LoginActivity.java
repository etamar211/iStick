package com.ed.istick;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    /*
    * this activity will let the user insert manually the IP and Pass for connection
     */
    private Button connectButt;
    private EditText IP_ET;
    private EditText passET;
    private ProgressBar LS;
    private TextView IPText;
    private TextView passText;
    private TextView connectingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //set the view variables
        LS = (ProgressBar)findViewById(R.id.LodingSymbol);
        connectButt = (Button) findViewById(R.id.ConnectButt);
        IP_ET = (EditText) findViewById(R.id.IP);
        passET = (EditText) findViewById(R.id.Pass);
        IPText = (TextView) findViewById(R.id.IPText);
        passText = (TextView) findViewById(R.id.PassText);
        connectingText = (TextView) findViewById(R.id.ConnectingText);

        //when the user want to connect
        connectButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String IP = IP_ET.getText().toString();
                final String pass = passET.getText().toString();
                //check if the data is usable
                //check if the IP part is really IP and the Pass is in the right length
                if(ClientLogic.validIP(IP)){
                    //The data seems right, start the ConnectToServer AsyncTask
                    new ConnectToServer().execute(IP, pass);
                }
                else{
                    Toast.makeText(LoginActivity.this, "The IP you've entered is not an IP", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private class ConnectToServer extends AsyncTask<String, Integer, Long> {
        /* this async task will create the connection to server (with the createConnection function
        * in ClientLogic
        * Also, the Task will handle the UI
         */
        protected void onPreExecute(){
            //set the UI
            IP_ET.setVisibility(View.INVISIBLE);
            passET.setVisibility(View.INVISIBLE);
            connectButt.setVisibility(View.INVISIBLE);
            IPText.setVisibility(View.INVISIBLE);
            passText.setVisibility(View.INVISIBLE);
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
            if(result == 1){
                //connection created susecfully, open the template activity
                Intent templateChoose = new Intent(LoginActivity.this, TemplateChooseOptions.class);
                startActivity(templateChoose);
                finish();
            }
            else{
                //connection failed, return the view to how it was
                LS.setVisibility(View.GONE);
                connectingText.setVisibility(View.GONE);
                IP_ET.setVisibility(View.VISIBLE);
                passET.setVisibility(View.VISIBLE);
                connectButt.setVisibility(View.VISIBLE);
                IPText.setVisibility(View.VISIBLE);
                passText.setVisibility(View.VISIBLE);
                Globals g = Globals.getInstance();
                Toast.makeText(LoginActivity.this, "the connection didn't go as plan\n" + g.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
