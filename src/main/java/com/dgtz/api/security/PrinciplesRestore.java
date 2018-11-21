package com.dgtz.api.security;

import com.brocast.riak.api.beans.DcUsersEntity;
import com.dgtz.api.beans.Principle;
import com.dgtz.api.constants.Formula;
import com.dgtz.api.contents.UsersShelf;
import com.dgtz.api.enums.EnumErrors;
import com.dgtz.api.feature.DBConnector;
import com.dgtz.api.feature.SystemDelivery;
import com.dgtz.api.settings.ISystemDelivery;
import com.dgtz.api.utils.MD5;
import com.dgtz.db.api.domain.Notification;
import com.dgtz.db.api.domain.Notificator;
import com.dgtz.db.api.enums.EnumSQLErrors;
import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.LoggerFactory;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 2/26/14
 */
public class PrinciplesRestore extends DBConnector {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PrinciplesRestore.class);


    public EnumErrors processOfEmailAlter(Principle principle) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        DcUsersEntity user = getDbForQuery().getUserInfoByEmail(principle.getEmail());
        if (user != null) {

            Notification notification = new Notification();
            notification.setIdFrom(1); // System user
            /*TYPE 14 - PROFILE CREATION, 13 - PASSWORD RESTORE*/
            notification.setType(15);
            notification.setIdUser(user.idUser);
            notification.setActivation(user.hash);
            notification.setUsername(user.username);
            ISystemDelivery systemDelivery = SystemDelivery
                    .builder(notification)
                    .email();


        } else {
            errors = EnumErrors.NO_SUCH_MAIL;
        }

        return errors;
    }


    public EnumErrors processOfPasswordRestore(Principle principle) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        DcUsersEntity user = getDbForQuery().getUserInfoByEmail(principle.getEmail());
        if (user != null) {

            Notification notification = new Notification();
            notification.setIdFrom(1); // System user
            /*TYPE 14 - PROFILE CREATION, 13 - PASSWORD RESTORE*/
            notification.setType(13);
            notification.setIdUser(user.idUser);
            notification.setActivation(user.hash);
            notification.setUsername(user.username);
            notification.setEmail(user.email);
            RMemoryAPI.getInstance()
                    .pushElemToMemory(Formula.PASS_RESTORE + user.hash,4, user.email);
            ISystemDelivery systemDelivery = SystemDelivery
                            .builder(notification)
                            .email();
        } else {
            errors = EnumErrors.NO_SUCH_MAIL;
        }

        return errors;
    }

    public EnumErrors confirmOfPasswordRestore(String hash) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            String email = RMemoryAPI.getInstance().pullElemFromMemory(Formula.PASS_RESTORE + hash);
            DcUsersEntity user = null;
            if (email != null) {
                user = getDbForQuery().getUserInfoByEmail(email);
                log.debug("RESTORE PASS USER INFO: {}", user.toString());
            }
            if (user == null || user.idUser == 0 || user.email == null || user.email.isEmpty()) {
                errors = EnumErrors.NO_SUCH_MAIL;
            }
        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API: ", e);
        }


        return errors;
    }

    public EnumErrors acceptPasswordRestore(String password, String hash) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {
            String email = RMemoryAPI.getInstance().pullElemFromMemory(Formula.PASS_RESTORE + hash);
            DcUsersEntity user = null;
            if (email != null) {
                user = getDbForQuery().getUserInfoByEmail(email);
            }
            if (user != null && user.idUser != 0 && user.email != null && !user.email.isEmpty()) {
                user.secword = (MD5.hash(password + MD5.SALT));
                EnumSQLErrors sqlErrors = getDbForUpd().updateUserPass(user);
                if (sqlErrors != EnumSQLErrors.OK) {
                    errors = EnumErrors.UNKNOWN_ERROR;
                }
            } else {
                errors = EnumErrors.NO_PASS_ERROR;
            }
        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API: ", e);
        }

        if (errors == EnumErrors.NO_ERRORS) {
            try {
                RMemoryAPI.getInstance().delFromMemory(Formula.PASS_RESTORE + hash);
            } catch (Exception e) {
                log.error("ERROR IN MAIN API: ", e);
            }
        }

        return errors;
    }

    private String getRandomPassword() {
        StringBuffer password = new StringBuffer(9);
        password.append(RandomStringUtils.randomAlphanumeric(8));
        return password.toString();
    }
}
