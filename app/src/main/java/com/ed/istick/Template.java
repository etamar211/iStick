package com.ed.istick;

import android.util.Xml;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 25/04/2016.
 */
public class Template {
    /*
    *this class is used for parsing the
     */
    private List<MyButton> buttons;

    public Template(InputStream in) throws XmlPullParserException, IOException{
        //parse xml
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            buttons = readFeed(parser);
        } finally {
            in.close();
        }
    }

    private static List<MyButton> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<MyButton> buttons = new ArrayList();

        parser.require(XmlPullParser.START_TAG, null, "template");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the button tag
            if (name.equals("Button")) {
                buttons.add(readButton(parser));
            } else {
                skip(parser);
            }
        }
        return buttons;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private static MyButton readButton(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "Button");
        MyButton butt = new MyButton();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("text")) {
                butt.setText(myReadText(parser, name));
            }
            else if (name.equals("textSize")) {
                butt.setTextSize(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("textColor")) {
                butt.setTextColor(myReadText(parser, name));
            }
            else if (name.equals("layout_marginRight")) {
                butt.setLayout_marginRight(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("layout_marginTop")) {
                butt.setLayout_marginTop(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("layout_marginBottom")) {
                butt.setLayout_marginBottom(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("layout_marginLeft")) {
                butt.setLayout_marginLeft(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("alignParent")) {
                butt.setAlignParent(myReadText(parser, name));
            }
            else if (name.equals("ID")) {
                butt.setId(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("sleepTime")) {
                butt.setSleepTime(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("action")) {
                butt.setAction(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("actionType")) {
                butt.setActionType(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("layout_width")) {
                butt.setLayoutWidth(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("layout_height")) {
                butt.setLayoutHeight(Integer.parseInt(myReadText(parser, name)));
            }
            else if (name.equals("background")) {
                butt.setBackground(myReadText(parser, name));
            }
            else {
                skip(parser);
            }
        }
        return butt;
    }

    private static String myReadText(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return text;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public List<MyButton> getButtons() {
        return buttons;
    }

}
