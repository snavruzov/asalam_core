package com.dgtz.api.feature;

import com.dgtz.api.constants.InboxMapper;
import com.dgtz.db.api.builder.IChannelFactory;
import com.dgtz.db.api.builder.IQueryFactory;
import com.dgtz.db.api.builder.ISaveFactory;
import com.dgtz.db.api.factory.DataBaseAPI;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/22/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DBConnector {

    private IQueryFactory db;
    private ISaveFactory dbs;
    private IChannelFactory dbch;

    public DBConnector() {
        db = DataBaseAPI.getInstance();
        dbs = DataBaseAPI.getSaveInstance();
        dbch = DataBaseAPI.getChannelInstance();
        InboxMapper.getInstance().init();
    }


    protected IQueryFactory getDbForQuery() {
        return db;
    }

    protected ISaveFactory getDbForUpd() {
        return dbs;
    }

    protected IChannelFactory getDbForChannel() {
        return dbch;
    }

}
