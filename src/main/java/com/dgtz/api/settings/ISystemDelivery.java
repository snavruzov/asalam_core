package com.dgtz.api.settings;

import com.dgtz.db.api.domain.Notification;

/**
 * Created by sardor on 7/24/17.
 */
public interface ISystemDelivery {
    ISystemDelivery inbox();
    ISystemDelivery system();
    ISystemDelivery socket();
    ISystemDelivery push();
    ISystemDelivery email();
}
