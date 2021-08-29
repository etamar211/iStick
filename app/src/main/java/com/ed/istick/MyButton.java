package com.ed.istick;

/**
 * Created by Admin on 25/04/2016.
 */
public class MyButton implements Runnable {
    /*
    *this class will save the data of every button in the chosen template and will handle the normal action only
     */
    private int layoutWidth;
    private int layoutHeight;
    private String text;
    private float textSize;
    private String textColor;
    private int layout_marginRight;
    private int layout_marginTop;
    private int layout_marginBottom;
    private int layout_marginLeft;
    private String alignParent;
    private String background;
    private int ID;
    private int action;
    private int actionType;
    private Thread t;
    private boolean running;
    private long sleepTime;
    private float XMotion;
    private float YMotion;


    public MyButton() {
        layoutWidth = -1;
        layoutHeight = -1;
        text = null;
        textSize = -1;
        textColor = null;
        layout_marginRight = 0;
        layout_marginTop = 0;
        layout_marginBottom = 0;
        layout_marginLeft = 0;
        background = null;
        alignParent = null;
        ID = -1;
        action = -1;
        running = false;
        sleepTime = 10;
        XMotion = -1;
        YMotion = -1;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public void setLayoutWidth(int layoutWidth) {
        this.layoutWidth = layoutWidth;
    }

    public int getLayoutHeight() {
        return layoutHeight;
    }

    public void setLayoutHeight(int layoutHeight) {
        this.layoutHeight = layoutHeight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return ID;
    }

    public void setId(int id) {
        ID = id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getLayout_marginRight() {
        return layout_marginRight;
    }

    public void setLayout_marginRight(int layout_marginRight) {
        this.layout_marginRight = layout_marginRight;
    }

    public int getLayout_marginBottom() {
        return layout_marginBottom;
    }

    public void setLayout_marginBottom(int layout_marginBottom) {
        this.layout_marginBottom = layout_marginBottom;
    }

    public int getLayout_marginTop() {
        return layout_marginTop;
    }

    public void setLayout_marginTop(int layout_marginTop) {
        this.layout_marginTop = layout_marginTop;
    }

    public int getLayout_marginLeft() {
        return layout_marginLeft;
    }

    public void setLayout_marginLeft(int layout_marginLeft) {
        this.layout_marginLeft = layout_marginLeft;
    }

    public String getAlignParent() {
        return alignParent;
    }

    public void setAlignParent(String alignParent) {
        this.alignParent = alignParent;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public float getXMotion() {
        return XMotion;
    }

    public void setXMotion(float XMotion) {
        this.XMotion = XMotion;
    }

    public float getYMotion() {
        return YMotion;
    }

    public void setYMotion(float YMotion) {
        this.YMotion = YMotion;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        ClientLogic CL = Globals.getInstance().getCL();
        while(running){
            CL.addToSend("10|0 " + action + "|");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                running = false;
            }
        }
        CL.addToSend("10|1 " + action + "|");
    }
}
