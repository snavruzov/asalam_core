package com.dgtz.api.utils;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public final class ConverterUtils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConverterUtils.class);

    public ConverterUtils() {

    }

    public static byte[] parseToByteArray(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return bos.toByteArray();
    }

    public static Object extractByteAttribute(byte[] attr) {

        ObjectInputStream ois = null;
        Object list = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(attr));

            list = ois.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    log.error("ERROR IN MAIN API ", e);
                }
            }
        }

        return list;

    }

    public static String convertAndCurrentDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("d.M.yyyy");
        return date.toString(fmt);
    }

    public static String getCurrentFormattedDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        int day = Integer.valueOf(strDate.replaceAll("\\D", ""));
        switch (day) {
            case 1: {
                strDate = day + "st" + strDate.replaceAll("\\d", "");
                break;
            }
            case 21: {
                strDate = day + "st" + strDate.replaceAll("\\d", "");
                break;
            }
            case 31: {
                strDate = day + "st" + strDate.replaceAll("\\d", "");
                break;
            }

            case 2: {
                strDate = day + "nd" + strDate.replaceAll("\\d", "");
                break;
            }
            case 22: {
                strDate = day + "nd" + strDate.replaceAll("\\d", "");
                break;
            }

            case 3: {
                strDate = day + "rd" + strDate.replaceAll("\\d", "");
                break;
            }
            case 23: {
                strDate = day + "rd" + strDate.replaceAll("\\d", "");
                break;
            }
            default: {
                strDate = day + "th" + strDate.replaceAll("\\d", "");
                break;
            }
        }

        return strDate;
    }
}
