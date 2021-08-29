package com.ed.istick;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Admin on 11/04/2016.
 */
public class DispatchMessage implements Runnable{
    /*
    * this class will get the messages from the server and call for the process function
     */
    private DataInputStream input;
    private ClientLogic CL;

    public DispatchMessage(DataInputStream in, ClientLogic CL){
        this.CL = CL;
        this.input = in;
    }

    @Override
    public void run() {
        byte[] msg = new byte[16];
        String msgString = "";
        int retValue, i;
        while(!CL.isErrStatus()){
            try{
                retValue = input.read(msg);
                if(retValue != -1){
                    i = 0;
                    while(msg[i] != 0 && i < msg.length - 1){
                        msgString += (char) msg[i];
                        i++;
                    }
                    if(msg[i] != 0){
                        msgString += (char) msg[i];

                    }
                    //CL.getAes().decrypt(msg)
                    CL.processMassage(msgString);
                }
                else{
                    //Kill CL
                    CL.onError();
                    CL.setErrStatus(true);
                    return;
                }
            }
            catch (IOException e) {
                //kill CL
                CL.onError();
                CL.setErrStatus(true);
                return;
            } catch (Exception e) {
                //kill CL
                CL.onError();
                CL.setErrStatus(true);
                return;
            }
            msgString = "";
        }
    }
}
