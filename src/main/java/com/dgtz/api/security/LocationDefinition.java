package com.dgtz.api.security;

import com.dgtz.mcache.api.factory.Constants;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class LocationDefinition extends HttpClientWrapper {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LocationDefinition.class);


    private String local = "Undefined location";
    private String country = "Undefined location";
    private String city = "Undefined location";

    public LocationDefinition() {
    }

    public LocationDefinition(double lat, double lng, String langCode) {

        try {
            String city_en = requestGeoLocation(lat, lng, "en");
            String val = city_en.replace(" ", "");
            Set<String> codes = RMemoryAPI.getInstance().pullSetElemFromMemory(Constants.TRANSLATION + ":list");
            codes.forEach(cod -> {
                String val_tr = RMemoryAPI.getInstance()
                        .pullHashFromMemory(Constants.TRANSLATION + val, "location" + ":" + cod);
                if (val_tr == null) {
                    try {
                        String cty = requestGeoLocation(lat, lng, cod);
                        RMemoryAPI.getInstance()
                                .pushHashToMemory(Constants.TRANSLATION + val, "location" + ":" + cod, cty);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });


        } catch (Exception e) {
            this.local = " ";
            this.country = " ";
            this.city = " ";
            log.error("ERROR IN GEO_MAP DEFINITION", e);
        }

    }

    public static String shortCountryDefinition(double lat, double lng) {

        String short_code = null;
        try {
            GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAnomZpWOs9d5SwZlOZC5AhBMYhndbo1Bg");
            LatLng latLng = new LatLng(lat, lng);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).language("en").await();

            for (AddressComponent elem : results[0].addressComponents) {
                for (AddressComponentType tp : elem.types) {
                    if (tp == AddressComponentType.COUNTRY) {
                        short_code = elem.shortName;
                    }
                }
            }

            log.debug("Google map result: {}", short_code);

        } catch (Exception e) {
            log.error("ERROR IN GEO_MAP SHORT COUNTRY CODE DEFINITION", e);
        }

        return short_code;

    }

    private String requestGeoLocation(double lat, double lng, String lang) throws Exception {

        String city_in = "";
        String state_in = "";
        String country_in = "";

        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAnomZpWOs9d5SwZlOZC5AhBMYhndbo1Bg");
        LatLng latLng = new LatLng(lat, lng);
        GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).language(lang).await();

        boolean stop = false;
        for (GeocodingResult res : results) {
            for (AddressType type : res.types) {
                switch (type) {
                    case LOCALITY:
                        state_in = res.formattedAddress;
                        stop = true;
                        break;
                    case ADMINISTRATIVE_AREA_LEVEL_3:
                        state_in = res.formattedAddress;
                        stop = true;
                        break;
                    case ADMINISTRATIVE_AREA_LEVEL_2:
                        state_in = res.formattedAddress;
                        stop = true;
                        break;
                    case ADMINISTRATIVE_AREA_LEVEL_1:
                        state_in = res.formattedAddress;
                        stop = true;
                        break;
                }
                if (stop) break;

            }
            if (stop) break;
        }


        for (AddressComponent elem : results[0].addressComponents) {
            for (AddressComponentType tp : elem.types) {
                if (!stop) {
                    switch (tp) {
                        case LOCALITY:
                            city_in = elem.longName;
                            stop = true;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_1:
                            city_in = elem.longName;
                            stop = true;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_2:
                            city_in = elem.longName;
                            stop = true;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_3:
                            city_in = elem.longName;
                            stop = true;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_4:
                            city_in = elem.longName;
                            stop = true;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_5:
                            city_in = elem.longName;
                            stop = true;
                            break;
                    }
                }
                if (tp == AddressComponentType.COUNTRY) {
                    country_in = elem.longName;
                    city_in = (state_in.isEmpty()) ? city_in + ", " + country_in : state_in;
                }

            }
        }


        System.out.println("Google map result: {}:::{}" + city_in + " " + country_in);

        if (lang.equals("en")) {
            city = city_in.replace("\"","");
            country = country_in.replace("\"","");
            this.local = results[0].formattedAddress;
        }

        log.debug("RES {}, {}, {}", new Object[]{city, country, this.local});

        return city_in;
    }


    public String getCityName() {
        return this.city;
    }

    public String getCountryName() {
        return this.country;
    }

    public String getLocal() {
        return this.local;
    }
}