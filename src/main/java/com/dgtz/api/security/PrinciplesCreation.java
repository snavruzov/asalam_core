package com.dgtz.api.security;

import com.brocast.riak.api.beans.DcUsersEntity;
import com.brocast.riak.api.beans.PrivateInfo;
import com.brocast.riak.api.dao.RiakAPI;
import com.brocast.riak.api.dao.RiakTP;
import com.brocast.riak.api.factory.IRiakSaveFactory;
import com.brocast.riak.api.factory.RiakSaveFactory;
import com.dgtz.api.beans.Principle;
import com.dgtz.api.beans.UserInfo;
import com.dgtz.api.contents.UsersShelf;
import com.dgtz.api.enums.EnumAuthErrors;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.DBConnector;
import com.dgtz.api.feature.SystemDelivery;
import com.dgtz.api.settings.ISystemDelivery;
import com.dgtz.api.utils.LanguageDetector;
import com.dgtz.api.utils.MD5;
import com.dgtz.db.api.domain.Notification;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.Instant;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.Collections;


/**
 * Created by sardor on 1/6/14.
 */
public class PrinciplesCreation extends DBConnector {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PrinciplesCreation.class);
    private String lang;

    public PrinciplesCreation() {
    }

    public PrinciplesCreation(String lang) {
        this.lang = LanguageDetector.detectLang(lang);
    }

    public EnumAuthErrors processOfRegistration(Principle principle) {

        EnumAuthErrors authErrors = EnumAuthErrors.OK;
        try {
            String username = org.apache.commons.lang3.StringUtils.normalizeSpace(principle.getUsername());
            principle.setUsername(username);

            boolean isEmailExist = getDbForQuery()
                    .getUserInfoByEmail(principle.getEmail()) != null;

            log.debug("USER EXISTENCE===> {}", isEmailExist);
            if (isEmailExist) {
                authErrors = EnumAuthErrors.EMAIL_EXIST;
            } else {
                String passHash = MD5.hash(principle.getPassword() + MD5.SALT);
                String hash = MD5.hash(System.currentTimeMillis() + MD5.SALT);

                PrivateInfo privateInfo = new PrivateInfo();
                privateInfo.birthDay = "";
                privateInfo.code = "";
                privateInfo.mobileNumber = "";
                privateInfo.mobileNumber = "";
                privateInfo.moreEmail = "";

                DcUsersEntity usersEntity = new DcUsersEntity();
                usersEntity.username = org.apache.commons.lang3.StringUtils.normalizeSpace(username);
                usersEntity.secword = passHash;
                usersEntity.fullname = "";
                usersEntity.email = principle.getEmail();
                usersEntity.profile = privateInfo;
                usersEntity.avatar = "/" + System.nanoTime();
                usersEntity.wallpic = "/wl" + System.nanoTime();
                usersEntity.city = "";
                usersEntity.about = "";
                usersEntity.country = "";
                usersEntity.date_reg = Instant.now() + "";
                usersEntity.enabled = 1;
                usersEntity.hash = hash;
                usersEntity.idFBSocial = "";
                usersEntity.idGSocial = "";
                usersEntity.idTWTRSocial = "";
                usersEntity.idVKSocial = "";
                usersEntity.social_links = Collections.emptySet();
                usersEntity.idUser = System.currentTimeMillis();
                usersEntity.lang = lang;
                usersEntity.verified = false;
                usersEntity.stars = 0l;
                usersEntity.type = 0; //basic streamer

                Gson json = new GsonBuilder().create();
                String jsonStr = json.toJson(usersEntity);
                String interimKey = MD5.hash(RMemoryAPI.getInstance().currentTimeMillis() + MD5.SALT);

                RMemoryAPI.getInstance()
                        .pushElemToMemory(Constants.REG_QUEUE + interimKey, 3, jsonStr);
                RMemoryAPI.getInstance()
                        .pushElemToMemory(Constants.REG_QUEUE + "email:"+usersEntity.email, 3, "1");

                log.debug("QUEUE sent to redis {} key: {}", jsonStr, interimKey);

                Notification notification = new Notification();
                notification.setIdFrom(1); // System user
                /*TYPE 14 - PROFILE CREATION, 13 - PASSWORD RESTORE*/
                notification.setType(14);
                notification.setIdUser(usersEntity.idUser);
                notification.setActivation(interimKey);
                notification.setUsername(principle.getUsername());
                notification.setEmail(principle.getEmail().trim());
                ISystemDelivery systemDelivery = SystemDelivery
                                .builder(notification)
                                .email();

            }

        } catch (Exception e) {
            log.error("ERROR IN TRYING TO REGISTER", e);
        }
        return authErrors;
    }

    public JSONObject processOfBodyActivation(String aValue) {

        UserInfo userInfo = null;
        DcUsersEntity queueEntity = null;
        EnumErrors errors = EnumErrors.NO_ERRORS;


        try {
            queueEntity = (DcUsersEntity) RMemoryAPI.getInstance().pullElemFromMemory(Constants.REG_QUEUE + aValue
                    , DcUsersEntity.class);

            log.debug("Queue of registered users {}", queueEntity);

            if (queueEntity == null) {
                errors = EnumErrors.URL_EXPIRED;
            }

            boolean isActivatedAlready = RMemoryAPI.getInstance().pullElemFromMemory(Constants.REG_QUEUE + "activated:" + aValue) != null;
            if (isActivatedAlready) {
                errors = EnumErrors.ALREADY_ACTIVATED;
            }

            if (errors == EnumErrors.NO_ERRORS) {
                RiakTP transport = RiakAPI.getInstance();
                IRiakSaveFactory riakSaveFactory = new RiakSaveFactory(transport);
                riakSaveFactory.buildUserData(queueEntity);
                riakSaveFactory.buildUserNotificationSettings(queueEntity.idUser + "");

                EnumSQLErrors sqlErrors = getDbForUpd().activateFreshBody(queueEntity);
                errors = sqlErrors == EnumSQLErrors.OK ? EnumErrors.NO_ERRORS : EnumErrors.ACTIVATION_ERROR;
            }
            if (errors == EnumErrors.NO_ERRORS) {
                UserAccountActivation activation = new UserAccountActivation();
                activation.activate(queueEntity.idUser);

                queueEntity.setAvatar(Constants.STATIC_URL + queueEntity.idUser + "/image" + queueEntity.avatar + ".jpg");
                userInfo = new UserInfo(queueEntity, queueEntity.profile, queueEntity.idUser, queueEntity.avatar, 1);

                RMemoryAPI.getInstance().pushElemToMemory(Constants.REG_QUEUE + "activated:" + aValue, -1, "true");
                RMemoryAPI.getInstance().delFromMemory(Constants.REG_QUEUE + aValue);

            }


        } catch (Exception e) {
            log.error("ERROR IN MAIN API IN SET TO REDIS", e);
            errors = EnumErrors.ACTIVATION_ERROR;
        }

        JSONObject json = new JSONObject();
        json.put("error", errors);
        json.put("userinfo", new JSONObject(userInfo == null ? new UserInfo() : userInfo));
        return json;
    }
}
