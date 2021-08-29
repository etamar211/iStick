package com.ed.istick;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

/**
 * Created by Admin on 03/04/2016.
 */
public class ClientLogic implements Runnable{
    /*
    * this class will handle the connection with the server
     */
    private String IP;
    private String pass;
    private Socket sock;
    private ArrayList<String> messagesToSend;
    private DispatchMessage DM;
    private SendMessage SM;
    private boolean connctionStatus;
    private boolean passStatus;
    private boolean errStatus;
    //private AES aes;
    private ArrayList<ErrorListener> listeners;
    private boolean hadError;

    public ClientLogic(String IP, String pass){
        messagesToSend = new ArrayList<>();
        this.IP = IP;
        this.pass = pass;
        passStatus = false;
        errStatus = false;
        listeners = new ArrayList<>();
        hadError = false;
    }

    public void addToSend(String msg){
        this.messagesToSend.add(msg);
    }

    public String getFirstSend() throws InterruptedException {
        while(this.messagesToSend.isEmpty() && !errStatus){
            Thread.sleep(5);
        };
        if(errStatus){
            return null;
        }
        else{
            return this.messagesToSend.remove(0);
        }
    }

    public boolean processMassage(String msg){
        /*
        * get the code from msg and do a switch case of what to do in a couple of situations
        * mostly when the server toss you out
         */
        int msgCode;
        if(msg.contains("|")){
            msgCode = Integer.parseInt(msg.substring(0, msg.indexOf('|')));
        }
        else{
            msgCode = Integer.parseInt(msg);
        }
        switch(msgCode){
            case 100:
                //connection created successfully
                connctionStatus = true;
                passStatus = true;
                break;

            /*case 101:
                msg = msg.substring(msg.indexOf('|'), msg.length());
                String newMsg = "";
                for(int i = msg.length() - 1; i <= 0; i--){
                    newMsg += msg.charAt(i);
                }
                this.addToSend(newMsg);
                break;*/

            case 102:
                //logout
                this.setErrStatus(true);
                break;

            case 103:
                //R U Alive?
                this.addToSend("3");

            case 200:
                //connection error
                Globals.getInstance().setError("Password is incorrect, try to connect via Ip and Password");
                connctionStatus = false;
                passStatus = true;
            
            case 201:
                //illegal Massage
                onError();
                this.setErrStatus(true);
                break;
        }
        return true;
    }

    public boolean getStatus(){
        return this.connctionStatus;
    }

    public boolean isErrStatus() {
        return errStatus;
    }

    public void setErrStatus(boolean errStatus) {
        this.errStatus = errStatus;
    }

    /*public AES getAes() {
        return aes;
    }

    public void setAes(AES aes) {
        this.aes = aes;
    }*/

    public void addErrorListener(ErrorListener lis){
        listeners.add(lis);
    }

    @Override
    public void run() {
        //this.aes = new AES(pass);
        Globals g = Globals.getInstance();
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            this.sock = new Socket();
            this.sock.connect(new InetSocketAddress(IP, 6580), 10000);
            input = new DataInputStream(sock.getInputStream());
            output = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            connctionStatus = false;
            g.setError(e.toString());
            return;
        }
        DM = new DispatchMessage(input, this);
        Thread dispatchMessageThread = new Thread(DM);
        dispatchMessageThread.start();
        SM = new SendMessage(output, this);
        Thread sendMessageThread = new Thread(SM);
        sendMessageThread.start();
        this.addToSend("1|" + pass);
        while(!passStatus);
    }

    public void close() throws IOException {
        this.sock.close();
    }

    public interface ErrorListener {
        //this interface will stop the activities when there is an error with the connection
        public void onErrStatusChange();
    }

    public void onError(){
        //stop all the open activities except for start activity
        if(!hadError){
            Iterator<ErrorListener> it = listeners.iterator();
            while(it.hasNext()){
                it.next().onErrStatusChange();
            }
        }
        hadError = true;
    }

    public SendMessage getSM() {
        return SM;
    }

    public static boolean validIP (String ip) {
        //check if the string is really an IP
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}







