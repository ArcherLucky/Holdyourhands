package com.stock.holdyourhands.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by 7heaven on 2016/10/18.
 */

public class GSLog {
    private static final String DEFAULT_MESSAGE = "execute";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String NULL_TIPS = "Log with null object";
    private static final String PARAM = "Param";
    private static final String NULL = "null";

    public static final int JSON_INDENT = 4;

    private static final boolean NEED_LOG = true;

    public enum L{
        V(0),
        D(1),
        I(2),
        W(3),
        E(4),
        A(5),
        JSON(6),
        XML(7);

        int value;

        L(int value){
            this.value = value;
        }
    }

    private GSLog() {

    }

    public static void v() {
        printLog(L.V, null, DEFAULT_MESSAGE);
    }

    public static void v(Object msg) {
        printLog(L.V, null, msg);
    }

    public static void v(String name, boolean value){
        printLog(L.V, null, name + ":" + (value ? "true" : "false"));
    }

    public static void v(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.V, null, tag);
        } else {
            printLog(L.V, tag, objects);
        }
    }

    public static void d() {
        printLog(L.D, null, DEFAULT_MESSAGE);
    }

    public static void d(Object msg) {
        printLog(L.D, null, msg);
    }

    public static void d(String name, boolean value){
        printLog(L.D, null, name + ":" + (value ? "true" : "false"));
    }

    public static void d(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.D, null, tag);
        } else {
            printLog(L.D, tag, objects);
        }
    }

    public static void i() {
        printLog(L.I, null, DEFAULT_MESSAGE);
    }

    public static void i(Object msg) {
        printLog(L.I, null, msg);
    }

    public static void i(String name, boolean value){
        printLog(L.I, null, name + ":" + (value ? "true" : "false"));
    }

    public static void i(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.I, null, tag);
        } else {
            printLog(L.I, tag, objects);
        }
    }

    public static void w() {
        printLog(L.W, null, DEFAULT_MESSAGE);
    }

    public static void w(Object msg) {
        printLog(L.W, null, msg);
    }

    public static void w(String name, boolean value){
        printLog(L.W, null, name + ":" + (value ? "true" : "false"));
    }

    public static void w(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.W, null, tag);
        } else {
            printLog(L.W, tag, objects);
        }
    }

    public static void e() {
        printLog(L.E, null, DEFAULT_MESSAGE);
    }

    public static void e(Object msg) {
        printLog(L.E, null, msg);
    }

    public static void e(String name, boolean value){
        printLog(L.E, null, name + ":" + (value ? "true" : "false"));
    }

    public static void e(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.E, null, tag);
        } else {
            printLog(L.E, tag, objects);
        }
    }

    public static void a() {
        printLog(L.A, null, DEFAULT_MESSAGE);
    }

    public static void a(Object msg) {
        printLog(L.A, null, msg);
    }

    public static void a(String name, boolean value){
        printLog(L.A, null, name + ":" + (value ? "true" : "false"));
    }

    public static void a(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(L.A, null, tag);
        } else {
            printLog(L.A, tag, objects);
        }
    }

    public static void json(String jsonFormat) {
        printLog(L.JSON, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(L.JSON, tag, jsonFormat);
    }

    public static void xml(String xml) {
        printLog(L.XML, null, xml);
    }

    public static void xml(String tag, String xml) {
        printLog(L.XML, tag, xml);
    }

    public static void file(File targetDirectory, Object msg) {
        printFile(null, targetDirectory, null, msg);
    }

    public static void file(String tag, File targetDirectory, Object msg) {
        printFile(tag, targetDirectory, null, msg);
    }

    public static void file(String tag, File targetDirectory, String fileName, Object msg) {
        printFile(tag, targetDirectory, fileName, msg);
    }

    @SuppressLint("SwitchIntDef")
    private static void printLog(L type, String tagStr, Object... objects) {

        if (!NEED_LOG) {
            return;
        }

        String[] contents = wrapperContent(tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        switch (type) {
            case JSON:
                printJson(tag, msg, headString);
                break;
            default:
                printDefault(type, tag, headString + msg);
                break;
        }
    }


    private static void printFile(String tagStr, File targetDirectory, String fileName, Object objectMsg) {

        String[] contents = wrapperContent(tagStr, objectMsg);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        printFile(tag, targetDirectory, fileName, headString, msg);
    }

    private static String[] wrapperContent(String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 5;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(className).append(":").append(lineNumber).append(")#").append(methodNameShort).append(" :");

        String tag = (tagStr == null ? className : tagStr);
        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = stringBuilder.toString();

        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(NULL).append("\n");
                } else {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? NULL : object.toString();
        }
    }

    public static void printDefault(L type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(L type, String tag, String sub) {
        switch (type) {
            case V:
                Log.v(tag, sub);
                break;
            case D:
                Log.d(tag, sub);
                break;
            case I:
                Log.i(tag, sub);
                break;
            case W:
                Log.w(tag, sub);
                break;
            case E:
                Log.e(tag, sub);
                break;
            case A:
                Log.wtf(tag, sub);
                break;
        }
    }

    public static void printJson(String tag, String msg, String headString) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }

        printLine(tag, false);
    }

    public static void printFile(String tag, File targetDirectory, String fileName, String headString, String msg) {

        fileName = (fileName == null) ? getFileName() : fileName;
        if (save(targetDirectory, fileName, msg)) {
            Log.d(tag, headString + " save log success ! location is >>>" + targetDirectory.getAbsolutePath() + "/" + fileName);
        } else {
            Log.e(tag, headString + "save log fails !");
        }
    }

    private static boolean save(File dic, String fileName, String msg) {

        File file = new File(dic, fileName);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStreamWriter.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static String getFileName() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder("GSLog_");
        stringBuilder.append(Long.toString(System.currentTimeMillis() + random.nextInt(10000)).substring(4));
        stringBuilder.append(".txt");
        return stringBuilder.toString();
    }

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
}
