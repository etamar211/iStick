package com.ed.istick;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Templates extends AppCompatActivity implements ClientLogic.ErrorListener {

    /*
    * this activity will handle the template view
     */

    private ClientLogic CL;
    private ProgressBar LS;
    private List<MyButton> butts;
    private int isUsingMouse = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_templates);
        LS = (ProgressBar)findViewById(R.id.LodingSymbol);
        LS.setVisibility(View.VISIBLE);
        String fileName = getIntent().getStringExtra("FileName");
        InputStream template = null;
        try {
            template = getAssets().open(fileName);
        } catch (IOException e) {
            //print file not found
            Toast.makeText(this, "File Not Found, Please try again", Toast.LENGTH_SHORT).show();
            finish();
        }
        CL = Globals.getInstance().getCL();
        //parse the xml file and set the View
        new parseXml().execute(template);

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        //press ESC
        CL.addToSend("10|0 " + 1 + "|");
        CL.addToSend("10|1 " + 1 + "|");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //press ESC
        CL.addToSend("10|0 " + 1 + "|");
        CL.addToSend("10|1 " + 1 + "|");
        //check if the connection is still right
        ClientLogic CL = Globals.getInstance().getCL();
        if(CL.isErrStatus()){
            finish();
        }
        CL.addErrorListener(this);
    }

    @Override
    public void onErrStatusChange() {
        finish();
    }

    private class parseXml extends AsyncTask<InputStream, Integer, Long> {
        /*
        * this Async Task will parse the Xml and handle the UI
         */
        Template tl;
        protected Long doInBackground(InputStream... data) {
            long result = 1;
            try {
                tl = new Template(data[0]);
            } catch (XmlPullParserException e) {
                result = 0;
            }
            catch (IOException e) {
                result = 0;
            }
            return result;
        }

        protected void onPostExecute(Long result) {
            //set view
            if(result == 0){
                Toast.makeText(Templates.this, "There was an error with the file, Please try again \nOr choose another file", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                LS.setVisibility(View.GONE);
                butts = tl.getButtons();
                if(butts.size() == 0){
                    Toast.makeText(Templates.this, "There was an error with the file, Please try again \nOr choose another file", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    //set the View with the buttons from the Template file
                    MyButton curr;
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.RLayout);
                    Iterator<MyButton> it = butts.iterator();
                    while(it.hasNext()){
                        curr = it.next();
                        //check for errors
                        if((curr.getLayoutWidth() == -1) && (curr.getLayoutHeight() == -1) && (curr.getText() == null) && (curr.getTextSize() == -1) && (curr.getTextColor() == null) && (curr.getId() == -1)){
                            Toast.makeText(Templates.this, "There was an error with the file, Please try again \nOr choose another file", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        final Button butt = new Button(Templates.this);
                        RelativeLayout.LayoutParams LP = new RelativeLayout.LayoutParams(curr.getLayoutWidth() * 3, curr.getLayoutHeight() * 3);
                        if(curr.getAlignParent() != null) {
                            if (curr.getAlignParent().contains("right")) {
                                LP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            }
                            if (curr.getAlignParent().contains("left")) {
                                LP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            }
                            if (curr.getAlignParent().contains("top")) {
                                LP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            }
                            if (curr.getAlignParent().contains("bottom")) {
                                LP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            }
                        }
                        LP.setMargins(curr.getLayout_marginLeft() * 3, curr.getLayout_marginTop() * 3, curr.getLayout_marginRight() * 3, curr.getLayout_marginBottom() * 3);
                        butt.setLayoutParams(LP);
                        butt.setText(curr.getText());
                        butt.setTextColor(Color.parseColor(curr.getTextColor()));
                        butt.setTextSize(curr.getTextSize());
                        butt.setId(curr.getId());
                        butt.setBackgroundResource(R.drawable.circleshape);
                        if(curr.getActionType() == 0){
                            Thread t = new Thread(curr);
                            curr.setT(t);
                            butt.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    MyButton MB = butts.get(v.getId());
                                    if(event.getAction() == MotionEvent.ACTION_DOWN && !MB.isRunning()){
                                        MB.getT().start();
                                        MB.setRunning(true);
                                    }
                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        MB.setRunning(false);
                                        Thread t = new Thread(MB);
                                        MB.setT(t);
                                    }
                                    return true;
                                }
                            });
                        }
                        else if(curr.getActionType() == 1){
                            //if the action uses EyeSight
                            butt.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    MyButton MB = butts.get(v.getId());
                                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                                        if((event.getX() >=0) && (event.getX() >=0)){
                                            if((MB.getXMotion() != -1) && (MB.getYMotion() != -1)){
                                                CL.addToSend("12|" + (int) (event.getX() + MB.getXMotion()) + " " + (int) (event.getY() + MB.getYMotion())  + '|');
                                            }
                                            /*MB.setXMotion(event.getX());
                                            MB.setYMotion(event.getY());*/
                                            /*try {
                                                Thread.sleep(5);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }*/
                                        }
                                        else{
                                            MB.setXMotion(-1);
                                            MB.setYMotion(-1);
                                            return false;
                                        }
                                    }
                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        MB.setXMotion(event.getX());
                                        MB.setYMotion(event.getY());
                                    }
                                    return true;
                                }
                            });
                        }
                        else if(curr.getActionType() == 2){
                            //if the action uses EyeSight && mouse action
                            butt.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    MyButton MB = butts.get(v.getId());
                                    if(event.getAction() == MotionEvent.ACTION_DOWN && !MB.isRunning()){
                                        CL.addToSend("11|" + MB.getAction() + "|");
                                        MB.setRunning(true);
                                        if(isUsingMouse == -1){
                                            isUsingMouse = v.getId();
                                        }
                                    }
                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        MB.setRunning(false);
                                        CL.addToSend("11|" + (MB.getAction()+3) + "|");
                                        if(isUsingMouse == v.getId()){
                                            isUsingMouse = -1;
                                        }
										MB.setXMotion(-1);
										MB.setYMotion(-1);
                                    }
                                    if((event.getAction() == MotionEvent.ACTION_MOVE) && (isUsingMouse == v.getId())){
                                        if((event.getX() >=0) && (event.getX() >=0)){
                                            if((MB.getXMotion() != -1) && ((((event.getX() - MB.getXMotion()) > 2) || ((event.getX() - MB.getXMotion()) < -2)) ||
                                                    (((event.getY() - MB.getYMotion()) > 2) || ((event.getY() - MB.getYMotion()) < -2)))){
                                                CL.addToSend("12|" + (int) (event.getX() - MB.getXMotion()) + " " + (int) (event.getY() - MB.getYMotion())  + '|');
                                            }
                                            MB.setXMotion(event.getX());
                                            MB.setYMotion(event.getY());
                                            try {
                                                Thread.sleep(20);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else{
                                            MB.setRunning(false);
                                            CL.addToSend("11|" + (MB.getAction()+3) + "|");
                                            if(isUsingMouse == v.getId()){
                                                isUsingMouse = -1;
                                            }
											MB.setXMotion(-1);
											MB.setYMotion(-1);
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        else if(curr.getActionType() == 3){
                            //if the action is move action
                            butt.setOnTouchListener(new View.OnTouchListener() {
                                boolean APressed = false, WPressed = false, SPressed = false, DPressed = false;

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    MyButton MB = butts.get(v.getId());
                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        if(WPressed){
                                            CL.addToSend("10|1 " + 0x11 + "|");
                                            WPressed = false;
                                        }
                                        if(APressed){
                                            CL.addToSend("10|1 " + 0x1E + "|");
                                            APressed = false;
                                        }
                                        if(SPressed){
                                            CL.addToSend("10|1 " + 0x1F + "|");
                                            SPressed = false;
                                        }
                                        if(DPressed){
                                            CL.addToSend("10|1 " + 0x20 + "|");
                                            DPressed = false;
                                        }
                                    }
                                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                                        int x = (int) event.getX() - ((MB.getLayoutWidth() / 2) * 3);
                                        int y = (int) event.getY() - ((MB.getLayoutHeight() / 2) * 3);
                                        int send1 = -1, send2 = -1;
                                        double angle;
                                        int i = check(x, y);
                                        if (x == 0 || y == 0)
                                        {
                                            if (x == 0 && y == 0)
                                            {

                                            }
                                            else if (x == 0 && y != 0)
                                            {
                                                if (y > 0)
                                                    send1 = (int) 'S';
                                                else
                                                    send1 = (int) 'W';
                                            }
                                            else if (x != 0 && y == 0)
                                            {
                                                if (x > 0)
                                                    send1 = (int) 'D';
                                                else
                                                    send1 = (int) 'A';
                                            }
                                        }
                                        else
                                        {
                                            switch (i)
                                            {
                                                case 1:
                                                    angle =  Math.toDegrees(Math.atan(y / x)) / 30;
                                                    break;
                                                case 2:
                                                    angle = (Math.toDegrees(Math.atan(y / x)) + 360) / 30;
                                                    break;
                                                case 3:
                                                    angle = (Math.toDegrees(Math.atan(y / x)) + 180) / 30;
                                                    break;
                                                case 4:
                                                    angle = (Math.toDegrees(Math.atan(y / x)) + 180) / 30;
                                                    break;
                                                default:
                                                    angle = -1;
                                                    break;
                                            }
                                            if((angle >= 8.11 && angle <= 9.89)){
                                                send1 = (int) 'W';
                                            }
                                            if(angle >= 9.9 && angle <= 10.8){
                                                send1 = (int) 'W';
                                                send2 = (int) 'D';
                                            }
                                            if(angle >= 10.81 || (angle >= 0 && angle <= 1.19)){
                                                send1 = (int) 'D';
                                            }
                                            if(angle >= 1.2 && angle <= 2.1){
                                                send1 = (int) 'D';
                                                send2 = (int) 'S';
                                            }
                                            if(angle >= 2.11 && angle <= 3.9){
                                                send1 = (int) 'S';
                                            }
                                            if(angle >= 3.91 && angle <= 5.1){
                                                send1 = (int) 'S';
                                                send2 = (int) 'A';
                                            }
                                            if(angle >= 5.11 && angle <= 6.9){
                                                send1 = (int) 'A';
                                            }
                                            if(angle >= 6.91 && angle <= 8.1){
                                                send1 = (int) 'A';
                                                send2 = (int) 'W';
                                            }
                                        }
                                        // The angle is not really an angle, its the hour at the clock, 0 is at the x positive way and it continues clockwise.. like, where was 3 its now 0 and it continues clockwise from there.
                                        // it can work with the mouse and with the wasd pads.
                                        //
                                        if(send1 != 'W' && send2 != 'W' && WPressed){
                                            CL.addToSend("10|1 " + 0x11 + "|");
                                            WPressed = false;
                                        }
                                        if(send1 != 'A' && send2 != 'A' && APressed){
                                            CL.addToSend("10|1 " + 0x1E + "|");
                                            APressed = false;
                                        }
                                        if(send1 != 'S' && send2 != 'S' && SPressed){
                                            CL.addToSend("10|1 " + 0x1F + "|");
                                            SPressed = false;
                                        }
                                        if(send1 != 'D' && send2 != 'D' && DPressed){
                                            CL.addToSend("10|1 " + 0x20 + "|");
                                            DPressed = false;
                                        }

                                        if(send1 == 'W' || send2 == 'W'){
                                            CL.addToSend("10|0 " + 0x11 + "|");
                                            WPressed = true;
                                        }
                                        if(send1 == 'A' || send2 == 'A'){
                                            CL.addToSend("10|0 " + 0x1E + "|");
                                            APressed = true;
                                        }
                                        if(send1 == 'S' || send2 == 'S'){
                                            CL.addToSend("10|0 " + 0x1F + "|");
                                            SPressed = true;
                                        }
                                        if(send1 == 'D' || send2 == 'D'){
                                            CL.addToSend("10|0 " + 0x20 + "|");
                                            DPressed = true;
                                        }
                                    }
                                    return true;
                                }
                            });

                        }
                        else if(curr.getActionType() == 4){
                            //if the action uses EyeSight && mouse action
                            butt.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    MyButton MB = butts.get(v.getId());
                                    if(event.getAction() == MotionEvent.ACTION_DOWN && !MB.isRunning()){
                                        CL.addToSend("10|0 " + MB.getAction() + "|");
                                        MB.setRunning(true);

                                    }
                                    if(event.getAction() == MotionEvent.ACTION_UP){
                                        MB.setRunning(false);
                                        CL.addToSend("10|1 " + (MB.getAction()) + "|");
                                        MB.setXMotion(-1);
                                        MB.setYMotion(-1);
                                    }
                                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                                        if((event.getX() >=0) && (event.getX() >=0)){
                                            if((MB.getXMotion() != -1) && ((((event.getX() - MB.getXMotion()) > 2) || ((event.getX() - MB.getXMotion()) < -2)) ||
                                                    (((event.getY() - MB.getYMotion()) > 2) || ((event.getY() - MB.getYMotion()) < -2)))){
                                                CL.addToSend("12|" + (int) (event.getX() - MB.getXMotion()) + " " + (int) (event.getY() - MB.getYMotion())  + '|');
                                            }
                                            MB.setXMotion(event.getX());
                                            MB.setYMotion(event.getY());
                                            try {
                                                Thread.sleep(20);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else{
                                            MB.setRunning(false);
                                            CL.addToSend("10|1 " + (MB.getAction()) + "|");
                                            MB.setXMotion(-1);
                                            MB.setYMotion(-1);
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        layout.addView(butt);
                    }
                }
            }
        }
    }

    private static int check(int x, int y) {
        if (x > 0 && y > 0)
            return 1;
        if (x > 0 && y < 0)
            return 2;
        if (x < 0 && y > 0)
            return 4;
        if (x < 0 && y < 0)
            return 3;
        return 0;
    }
}