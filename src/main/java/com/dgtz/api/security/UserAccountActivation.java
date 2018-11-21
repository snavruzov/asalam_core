package com.dgtz.api.security;

import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.AmazonS3Module;
import com.dgtz.api.utils.ImageConverter;
import com.dgtz.api.utils.gravatar.Gravatar;
import com.dgtz.api.utils.gravatar.GravatarDefaultImage;
import com.dgtz.api.utils.gravatar.GravatarRating;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 8/7/14
 */
public class UserAccountActivation {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserAccountActivation.class);

    public UserAccountActivation() {
    }

    public EnumErrors activate(Long idUser) {

        EnumErrors errors = EnumErrors.NO_ERRORS;

        if (idUser != null && !idUser.equals(-1L)) {
            String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
            String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "wallpic");
            log.debug("AVATAR:WALL:: {}, {}", avatar, wallpic);

            if (avatar != null) {

                AmazonS3Module s3Module = new AmazonS3Module();
                {
                    String originalAva = generateAva(240, avatar);
                    s3Module.uploadImageFile(idUser + "/image" + avatar + ".jpg", originalAva);

                    String ava = ImageConverter.avaResizer(originalAva, 100);
                    s3Module.uploadImageFile(idUser + "/image" + avatar + "M.jpg", ava);

                    ava = ImageConverter.avaResizer(originalAva, 50);
                    s3Module.uploadImageFile(idUser + "/image" + avatar + "S.jpg", ava);

                    s3Module.copyImageFile("defaults/profile-cover.jpg", idUser + "/image" + wallpic + ".jpg");

                    clearTempAvatars(avatar);
                }


            }

        } else {
            errors = EnumErrors.INVALID_HASH;
        }


        log.debug("ERROR IN ACTIVATION {} {}", errors, idUser);


        return errors;

    }

    protected EnumErrors socialActivate(Long idUser) {

        PrinciplesCreation creation = new PrinciplesCreation();

        EnumErrors errors = EnumErrors.NO_ERRORS;

        if (idUser != null && !idUser.equals(-1L)) {
            String avatar = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "avatar");
            String wallpic = RMemoryAPI.getInstance().pullHashFromMemory(Constants.USER_KEY + idUser, "wallpic");
            log.debug("AVATAR:WALL:: {}, {}", avatar, wallpic);

            if (avatar != null) {
                AmazonS3Module s3Module = new AmazonS3Module();
                {
                    s3Module.copyImageFile("defaults/user.jpg", idUser + "/image" + avatar + ".jpg");
                    s3Module.copyImageFile("defaults/user.jpg", idUser + "/image" + avatar + "M.jpg");
                    s3Module.copyImageFile("defaults/user.jpg", idUser + "/image" + avatar + "S.jpg");
                    s3Module.copyImageFile("defaults/profile-cover.jpg", idUser + "/image" + wallpic + ".jpg");


                }
            }

        } else {
            errors = EnumErrors.INVALID_HASH;
        }


        log.debug("ERROR IN ACTIVATION {} {}", errors, idUser);


        return errors;

    }

    private String generateAva(Integer size, String avatar){
        Gravatar gravatar = new Gravatar();
        gravatar.setSize(size);
        gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
        gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);

        byte[] jpg = gravatar.download(avatar.replace("/","")+"@digitizen.com");

        FileOutputStream fos = null;
        try {
            if(jpg!=null) {
                fos = new FileOutputStream("/opt/dump"+avatar);
                fos.write(jpg);
            }
        } catch (Exception e){
            log.error("Error in generating gravatar", e);
        } finally {
            if(fos!=null) try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "/opt/dump"+avatar;
    }

    private void clearTempAvatars(String avatar){
        try {
            Files.delete(Paths.get("/opt/dump" + avatar + "S.jpg"));
            Files.delete(Paths.get("/opt/dump" + avatar + "M.jpg"));
            Files.delete(Paths.get("/opt/dump" + avatar));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
