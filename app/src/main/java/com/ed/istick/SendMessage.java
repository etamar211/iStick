package com.ed.istick;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by Admin on 11/04/2016.
 */
public class SendMessage implements Runnable{
    /*
    * this class will send the messages to the server
     */
    private DataOutputStream output;
    private ClientLogic CL;

    public SendMessage(DataOutputStream  out, ClientLogic CL){
        this.output = out;
        this.CL = CL;
    }

    @Override
    public void run() {
        String msg;
        while(!CL.isErrStatus()){
            try {
                msg = CL.getFirstSend();
                if(msg != null){
                    //CL.getAes().encrypt(msg)
                    output.writeBytes(msg);
                }
                else{
                    CL.onError();
                    return;
                }
            } catch (IOException e) {
                //kill connection
                CL.onError();
                CL.setErrStatus(true);
                return;
            } catch (Exception e) {
                CL.onError();
                CL.setErrStatus(true);
                return;
            }
        }
    }
}