package com.dgtz.api.contents;

import com.dgtz.api.feature.DBConnector;
import com.dgtz.db.api.domain.DcCategoriesEntity;
import com.dgtz.db.api.domain.DcLocationsEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/24/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuShelf extends DBConnector {

    public MenuShelf() {
        super();
    }

    public List<DcLocationsEntity> getAllCountries() {
        return getDbForQuery().extractAllCountries();
    }


}
