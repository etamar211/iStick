package com.ed.istick;

import android.app.Application;

/**
 * Created by Admin on 12/04/2016.
 */
public class Globals {
    /*
    * this class will save the global variables
     */
    private static Globals instance;

    private ClientLogic CL;
    private String error;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public ClientLogic getCL(){
        return CL;
    }

    public void setCL(ClientLogic CL){
        this.CL = CL;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
