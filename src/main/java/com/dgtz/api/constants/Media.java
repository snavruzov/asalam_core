package com.dgtz.api.constants;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/15/13
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Media {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Media.class);

    public static final String vb = "800k";
    public static final String ab = "96k";
    public static final String ch = "2";

    public static final Settings PATH_SETTINGS = getPathInstance();

    private static Settings pathSettings = null;

    public static Settings getPathInstance() {
        if (pathSettings == null) {
            pathSettings = new PathSettings();
        }

        return pathSettings;
    }

    public interface Settings {
        public String getMediaPath();

        public String getCoderPath();

        public String getRootPath();

        public String getVideoUrl();

        public String getThumbUrl();

        public String getPicUrl();

    }


    private static class PathSettings implements Settings {
        private Properties properties = null;

        public PathSettings() {
            properties = new Properties();
            try {
                properties.load(PathSettings.class.getClassLoader().getResourceAsStream("command.properties"));
            } catch (IOException e) {
                log.error("ERROR IN MAIN API ", e);
            }
        }

        @Override
        public String getMediaPath() {
            return properties.getProperty("media.path");
        }

        @Override
        public String getCoderPath() {
            return properties.getProperty("ffmpeg.path");
        }

        @Override
        public String getRootPath() {
            return properties.getProperty("root.path");
        }

        @Override
        public String getVideoUrl() {
            return properties.getProperty("video.url");
        }

        @Override
        public String getThumbUrl() {
            return properties.getProperty("thumb.url");
        }

        @Override
        public String getPicUrl() {
            return properties.getProperty("image.url");
        }

    }
}
