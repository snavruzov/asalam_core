package com.dgtz.api.utils;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.slf4j.LoggerFactory;

/**
 * Created by Sardor Navruzov on 6/4/15.
 */
public final class IP2LocationDefiner {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IP2LocationDefiner.class);

    public IP2LocationDefiner(){

    }

    public static String detectIPLocation(String addressIP){
        String latlng = "";

        log.debug("ADDRESS FOR IP2L: {}", addressIP);
        try {
            IP2Location loc = new IP2Location();

            loc.IPDatabasePath = "/opt/configs/IP2LOCAL/IP-COUNTRY-REGION-CITY-LATITUDE-LONGITUDE.BIN";

            IPResult rec = loc.IPQuery(addressIP);
            if ("OK".equals(rec.getStatus())) {
                latlng = rec.getLatitude()+";"+rec.getLongitude();
            } else if ("EMPTY_IP_ADDRESS".equals(rec.getStatus())) {
                log.error("IP address cannot be blank.");
            } else if ("INVALID_IP_ADDRESS".equals(rec.getStatus())) {
                log.error("Invalid IP address.");
            } else if ("MISSING_FILE".equals(rec.getStatus())) {
                log.error("Invalid database path.");
            } else if ("IPV6_NOT_SUPPORTED".equals(rec.getStatus())) {
                log.error("This BIN does not contain IPv6 data.");
            } else {
                log.error("Unknown error." + rec.getStatus());
            }
            if (rec.getDelay()) {
                log.error("The last query was delayed for 5 seconds because this is an evaluation copy.");
            }
            log.info("Java Component: {}", rec.getVersion());

        } catch (Exception e) {
            log.error("ERROR IN IP2LOCATION", e);
        }

        return latlng;
    }

    public static String detectCountryCodeByIPLocation(String addressIP){
        String code = "";

        log.debug("ADDRESS FOR IP2L: {}", addressIP);
        try {
            IP2Location loc = new IP2Location();

            loc.IPDatabasePath = "/opt/configs/IP2LOCAL/IP-COUNTRY-REGION-CITY-LATITUDE-LONGITUDE.BIN";

            IPResult rec = loc.IPQuery(addressIP);
            if ("OK".equals(rec.getStatus())) {
                code = rec.getCountryShort();
            } else if ("EMPTY_IP_ADDRESS".equals(rec.getStatus())) {
                log.error("IP address cannot be blank.");
            } else if ("INVALID_IP_ADDRESS".equals(rec.getStatus())) {
                log.error("Invalid IP address.");
            } else if ("MISSING_FILE".equals(rec.getStatus())) {
                log.error("Invalid database path.");
            } else if ("IPV6_NOT_SUPPORTED".equals(rec.getStatus())) {
                log.error("This BIN does not contain IPv6 data.");
            } else {
                log.error("Unknown error." + rec.getStatus());
            }
            if (rec.getDelay()) {
                log.error("The last query was delayed for 5 seconds because this is an evaluation copy.");
            }
            log.info("Java Component: {}", rec.getVersion());

        } catch (Exception e) {
            log.error("ERROR IN IP2LOCATION", e);
        }

        return code;
    }
}
