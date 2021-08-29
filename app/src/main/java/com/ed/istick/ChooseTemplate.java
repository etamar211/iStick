package com.ed.istick;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.IOException;
import java.util.ArrayList;

public class ChooseTemplate extends ListActivity implements ClientLogic.ErrorListener {
    /*
    * this activity will let the user choose template
     */

    ArrayList<String> templatesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);
    }

    public void onListItemClick(ListView LV, View v, int position, long id) {
        //start the templates activity and put the fileName as parameter
        Intent T = new Intent(ChooseTemplate.this, Templates.class);
        T.putExtra("FileName", "template_" + templatesName.get(position) + ".xml");
        startActivity(T);
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //set the templates view
        try {
            setListAdapter(getAdapterForFilesName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientLogic CL = Globals.getInstance().getCL();
        if(CL.isErrStatus()){
            finish();
        }
        CL.addErrorListener(this);
    }

    public ListAdapter getAdapterForFilesName() throws IOException {
        //get all the file names that are templates
        templatesName = new ArrayList<String>();
        String[] fileNames = getAssets().list("");
        String SS;
        for(int i = 0; i < fileNames.length; i++){
            if(fileNames[i].length() > 8) {
                SS = fileNames[i].substring(0, 9);
                if (SS.equals("template_")) {
                    templatesName.add(fileNames[i].substring(9, fileNames[i].length() - 4));
                }
            }
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, templatesName);
        return adapter;
    }

    @Override
    public void onErrStatusChange() {
        finish();
    }

}
