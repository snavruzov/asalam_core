package com.dgtz.api.contents;

import org.junit.Before;
import org.junit.Test;

/**
 * BroCast.
 * Author: Sardor Navruzov
 * Date: 1/19/15
 */
public class MediaShelfTest {
    private long idMedia;
    private long idUser;

    @Before
    public void setUp() throws Exception {
        idMedia = 10005;
        idUser = 109;

    }

    @Test
    public void testExtractMediaActivityByIdMedia() throws Exception {

        /*String lat = "25.2048";
        String lng = "55.2708";
        HttpResponse<JsonNode> body = Unirest.get(Constants.PUSH_URL+"address/?latlng="+lat+","+lng).asJson();
        JSONObject json = body.getBody().getObject();
        System.out.println("Client live location {}"+ json.getString("city")+" "+ json.getString("country"));

        String state = json.getString("state");
        String country = json.getString("country");
        String city = json.getString("city");
        String full_address = json.getString("formatted_address");

        if(state.isEmpty() || city.equals(state)){
            state = ",";
        } else {
            state = ","+state+",";
        }
        String location = city + state + country;
*/
        System.out.println("OFF");
    }
}
