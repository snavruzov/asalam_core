package com.dgtz.api.enums;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public enum EnumDimension {

    HD("720x480", 720, 700),
    SD("640x360", 640, 300),
    SD_L("640x480", 640, 256);
    //LD("512x288",512,128);

    public String size;
    public int diff;
    public int bitrate;

    EnumDimension(String size, int diff, int bitrate) {
        this.size = size;
        this.diff = diff;
        this.bitrate = bitrate;
    }


}
