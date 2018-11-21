package com.dgtz.api.feature;

import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.settings.Thumbnail;
import com.dgtz.api.utils.ImageConverter;
import com.dgtz.api.utils.ShellCommands;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/24/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThumbnailBuilder implements Thumbnail{
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ThumbnailBuilder.class);

    private String path;
    private String outDir;
    private Long idMedia;
    private int rotation;

    public ThumbnailBuilder() {
    }

    public ThumbnailBuilder(String path, String outDir, Long idMedia, int rotation) {
        this.path = path;
        this.outDir = outDir;
        this.idMedia = idMedia;
        this.rotation = rotation;
    }

    @Override
    public String build() {
        return null;
    }

    @Override
    public void copy() {

    }

    public Boolean thumbnailExtractor(String path, String outDir, Long idMedia, int rotation) {

        ShellCommands.generateVideoThumbnails(path, outDir, rotation);
        boolean isExt = Files.exists(Paths.get(outDir + "00000001.jpg"));
        if (isExt) {
            ImageConverter.convertToThumb(outDir + "00000001.jpg", outDir + idMedia);
        }

        return isExt;
    }

    public String thumbnailOrigExtractor(String path, String outDir, int rotation) {

        String imgPath = "";
        ShellCommands.generateVideoThumbnails(path, outDir, rotation);
        boolean isExt = Files.exists(Paths.get(outDir + "00000001.jpg"));
        if (isExt) {
            imgPath = outDir + "00000001.jpg";
        }

        return imgPath;
    }


    public static void thumbnailCopy(long idMedia, long idUser) throws Exception {

        String tmp = "/opt/dump/000000";

        try {

            String link = Constants.encryptAmazonURL(idUser, idMedia, "", "p", "").trim();
            String thumbJ = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "").trim();
            boolean isLinkValid = AmazonS3Module.isValidImageFile(link);

            AmazonS3Module s3Module = new AmazonS3Module();
            if (isLinkValid) {
                URL url = new URL(Constants.STATIC_URL + link);
                log.debug("URL::::::: {}", Constants.STATIC_URL + link);
                InputStream file = url.openStream();
                tmp = "/opt/dump/" + RMemoryAPI.getInstance().currentTimeMillis();
                ImageConverter.convertToThumb(file, tmp);


                boolean isRefresh = AmazonS3Module.isValidImageFile(thumbJ);

                String link_jpg = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "", isRefresh);
                String link_webp = Constants.encryptAmazonURL(idUser, idMedia, "webp", "thumb", "", isRefresh);

                s3Module.uploadImageFile(link_jpg, tmp + ".jpg");
                s3Module.uploadImageFile(link_webp, tmp + ".webp");
            } else {
                link = Constants.encryptAmazonURL(idUser, idMedia, "jpg", "thumb", "");
                s3Module.copyImageFile("defaults/media.jpg", link);
                link = Constants.encryptAmazonURL(idUser, idMedia, "webp", "thumb", "");
                s3Module.copyImageFile("defaults/media.jpg", link);

            }
        } catch (Exception e) {
            log.error("ERROR IN THUMBNAILER ", e);
        } finally {
            Files.deleteIfExists(Paths.get(tmp + ".jpg"));
            Files.deleteIfExists(Paths.get(tmp + ".webp"));
        }
    }


}
