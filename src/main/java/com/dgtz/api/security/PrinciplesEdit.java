package com.dgtz.api.security;

import com.brocast.riak.api.beans.DcNotificationTypes;
import com.brocast.riak.api.beans.DcUserNotificationSettings;
import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakQueryFactory;
import com.brocast.riak.api.factory.RiakQueryFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.DBConnector;
import com.dgtz.api.utils.ImageConverter;
import com.dgtz.api.utils.LanguageDetector;
import com.dgtz.api.utils.MD5;
import com.dgtz.db.api.enums.EnumSQLErrors;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by sardor on 1/12/14.
 */
public class PrinciplesEdit extends DBConnector {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PrinciplesEdit.class);
    private String lang;

    public PrinciplesEdit() {
    }

    public PrinciplesEdit(String lang) {
        this.lang = LanguageDetector.detectLang(lang);
    }


    public EnumErrors editProfilePicture(InputStream source, String destination, int w, int h) {
        return ImageConverter.convertAvaToJpg(source, new File(destination), w, h);
    }

    public EnumErrors unsubscribeFromEmailNotification(String hash, String email, int type) {

        String rHash = MD5.hash(email + MD5.SALT);
        EnumSQLErrors sqlErrors = EnumSQLErrors.OK;

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            if (rHash.equals(hash)) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakQueryFactory queryFactory = new RiakQueryFactory(transport);
                DcUsersEntity user = queryFactory.queryUserDataByIDEmail(email);

                if (user != null) {
                    DcUserNotificationSettings settings = queryFactory.queryUserNotificationSettings(user.idUser, "email");

                    switch (type) {
                        case 1: {
                            settings.live = false;
                            break;
                        }
                        case 2: {
                            settings.channel = false;
                            break;
                        }
                        case 3: {
                            settings.comment = false;
                            break;
                        }
                        case 4: {
                            settings.follower = false;
                            break;
                        }
                        case 5: {
                            settings.promo = false;
                            break;
                        }
                        case 6: {
                            settings.inbox = false;
                            break;
                        }
                    }

                    RiakSaveFactory saveFactory = new RiakSaveFactory(transport);
                    saveFactory.updUserNotificationSettings(user.idUser + "", settings, "email");

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            errors = EnumErrors.EMAIL_SUBSRB_OFF_ERROR;
        }

        return errors;
    }

    public EnumSQLErrors setUserInfoByHash(DcUsersEntity entity) {

        return getDbForUpd().updateUserInfo(entity, lang);
    }

    public EnumSQLErrors setUserLocal(String local, Long idUser) {
        return getDbForUpd().setPersonalLocalization(local, idUser);
    }

    public EnumSQLErrors setUserAvaByHash(DcUsersEntity entity) {
        return getDbForUpd().updateUserInfo(entity, lang);
    }

    public void setUserWallByHash(DcUsersEntity entity) {
        getDbForUpd().updateUserInfo(entity, lang);
    }

    public EnumErrors updateUserInfoByEntity(DcUsersEntity entity) {
        EnumErrors errors = EnumErrors.NO_ERRORS;
        String username = org.apache.commons.lang3.StringUtils.normalizeSpace(entity.username);
        entity.setUsername(username);

        boolean isEmailExist = getDbForQuery()
                .getUserInfoByEmail(entity.email) != null;

        DcUsersEntity user = getDbForQuery().getUserProfileInfoById(entity.idUser);

        if (isEmailExist && !user.email.equals(entity.email)) {
            errors = EnumErrors.EMAIL_EXISTED;
        }

        if (errors == EnumErrors.NO_ERRORS) {
            EnumSQLErrors sqlErrors = getDbForUpd().updateUserInfo(entity, lang);
            if (sqlErrors != EnumSQLErrors.OK) {
                errors = EnumErrors.UNKNOWN_ERROR;
            }
        }

        log.debug("ERROR CODE {}", errors);
        return errors;
    }

    public String changeProfPass(String hash, String oldP, String newP) {
        String newHash = "";
        DcUsersEntity entity = getDbForQuery().getUserInfoByHash(hash);

        if (entity != null && entity.idUser != 0) {
            newP = MD5.hash(newP + MD5.SALT);
            newHash = getDbForUpd().changeAccountProfile(entity.idUser, newP, hash);
        }
        return newHash;
    }

    public void updSocialURLLinks(String links, String type, Long idUser, boolean status) {

        switch (type) {
            case "instagram": {
                DcUsersEntity entity = getDbForQuery().getUserInfoByID(idUser);
                Set<String> slinks = entity.social_links;
                slinks.forEach(idl -> {
                    if (idl.contains("instagram")) {
                        slinks.remove(idl);
                        if(status) {
                            slinks.add(links);
                        }
                    }
                });
                entity.social_links = slinks;
                getDbForUpd().updateUserInfo(entity, "en");
                break;
            }
            case "youtube": {
                DcUsersEntity entity = getDbForQuery().getUserInfoByID(idUser);
                Set<String> slinks = entity.social_links;
                slinks.forEach(idl -> {
                    if (idl.contains("youtube")) {
                        slinks.remove(idl);
                        if(status) {
                            slinks.add(links);
                        }
                    }
                });
                entity.social_links = slinks;
                getDbForUpd().updateUserInfo(entity, "en");
                break;
            }
        }
    }

    public EnumSQLErrors changeNotifications(DcNotificationTypes json, long idUser) {
        return getDbForUpd().updateNotificationParams(json, idUser);
    }

    public DcNotificationTypes getUserNotificationSettings(long idUser) {
        return getDbForQuery().getUserNotificationSettings(idUser);
    }


}
