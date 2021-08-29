package com.ed.istick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class TemplateChooseOptions extends AppCompatActivity implements ClientLogic.ErrorListener{
    /*
    * this activity will handle the options of templates
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_choose_options);

        Button ChooseTemplates = (Button) findViewById(R.id.ChooseTemplate);

        ChooseTemplates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //open the template choose option
               Intent CT = new Intent(TemplateChooseOptions.this, ChooseTemplate.class);
               startActivity(CT);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Always call the superclass method first
        //logout from the server
        ClientLogic CL = Globals.getInstance().getCL();
        CL.addToSend("2");
        CL.setErrStatus(true);
        try {
            CL.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //check if the connection still on
        ClientLogic CL = Globals.getInstance().getCL();
        if(CL.isErrStatus()){
            finish();
        }
        CL.addErrorListener(this);
    }

    @Override
    public void onErrStatusChange() {
        //when error occurred
        finish();
    }
}
