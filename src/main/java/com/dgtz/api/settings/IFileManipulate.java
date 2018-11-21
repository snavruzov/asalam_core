package com.dgtz.api.settings;

import com.dgtz.api.beans.MediaInfo;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.enums.EnumFileType;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IFileManipulate {

    EnumErrors save(byte[] source, File out);

    EnumErrors remove();

    void preProcess(String path);

    void preConcatProcess(String apath, String vpath, String fileOut);

    EnumFileType check(String fullPath);

    MediaInfo info(String filename, EnumFileType fType);

    //public MediaInfo thumb(String filename);
    EnumErrors hash();

    EnumErrors mkdir();
}
