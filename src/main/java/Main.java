import com.dgtz.api.feature.AmazonS3Module;
import com.google.gson.Gson;
import org.joda.time.Instant;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {

        /*
        try {
            IP2Location loc = new IP2Location();
            if (true)
            {
                loc.IPDatabasePath = "/opt/IP-COUNTRY-REGION-CITY-LATITUDE-LONGITUDE.BIN";

                IPResult rec = loc.IPQuery("5.31.183.59");
                if ("OK".equals(rec.getStatus()))
                {
                    System.out.println(rec.getCountryShort());
                }
                else if ("EMPTY_IP_ADDRESS".equals(rec.getStatus()))
                {
                    System.out.println("IP address cannot be blank.");
                }
                else if ("INVALID_IP_ADDRESS".equals(rec.getStatus()))
                {
                    System.out.println("Invalid IP address.");
                }
                else if ("MISSING_FILE".equals(rec.getStatus()))
                {
                    System.out.println("Invalid database path.");
                }
                else if ("IPV6_NOT_SUPPORTED".equals(rec.getStatus()))
                {
                    System.out.println("This BIN does not contain IPv6 data.");
                }
                else
                {
                    System.out.println("Unknown error." + rec.getStatus());
                }
                if (rec.getDelay() == true)
                {
                    System.out.println("The last query was delayed for 5 seconds because this is an evaluation copy.");
                }
                System.out.println("Java Component: " + rec.getVersion());
            }
            else
            {
                System.out.println("Usage: Main <IPDatabasePath> <IPAddress> [IPLicensePath]");
                System.out.println(" ");
                System.out.println("   <IPDatabasePath>      Specify BIN data file");
                System.out.println("   <IPAddress>           Specify IP address");
                System.out.println("   [IPLicensePath]       Path of registration license file (optional)");
                System.out.println("                         * Please leave this field empty for unregistered version.");
                System.out.println(" ");
                System.out.println("URL: http://www.ip2location.com");
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
        */

        /*String json = "{\n" +
                "  \"id\": 1234,\n" +
                "  \"output_urls\": {\n" +
                "    \"mp4\": \"http://mybucket.s3.amazonaws.com/abc1234/videos/mp4/demo.mp4\",\n" +
                "    \"mp4:360p\": \"http://mybucket.s3.amazonaws.com/abc1234/videos/mobile/demo.mp4\",\n" +
                "    \"hls\": \"http://mybucket.s3.amazonaws.com/abc1234/videos/hls/demo.m3u8\",\n" +
                "    \"jpg:200x\": [\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/small/demo_01.jpg\",\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/small/demo_02.jpg\",\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/small/demo_03.jpg\"\n" +
                "    ],\n" +
                "    \"jpg:640x\": [\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/large/demo_01.jpg\",\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/large/demo_02.jpg\",\n" +
                "      \"http://mybucket.s3.amazonaws.com/abc1234/thumbnails/large/demo_03.jpg\"\n" +
                "    ],\n" +
                "    \"storyboard:640x\": \"http://mybucket.s3.amazonaws.com/abc1234/storyboard/demo.png\"\n" +
                "  },\n" +
                "  \"errors\": {\n" +
                "    \"output\": {\n" +
                "      \"webm\": \"output_audio_cant_be_resampled\",\n" +
                "      \"gif:300x\": \"gif_not_uploaded\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"event\": \"job.completed\"\n" +
                "}";

        com.google.gson.JsonObject jobj = new Gson().fromJson(json, com.google.gson.JsonObject.class);
        String j = jobj.getAsJsonObject("output_urls").getAsJsonArray("jpg:200x").get(3).getAsString();
        System.out.println(j);*/
        /*String jsono = "{\"Request-URI\":\"\",\"Method\":{\"action\":\"on_publish\",\"client_id\":243,\"ip\":\"91.74.24.46\",\"vhost\":\"__defaultVhost__\",\"app\":\"dgtz\",\"tcUrl\":\"rtmp://stream.asalam.com:1935/dgtz\",\"stream\":\"live_id6791_23407\"},\"HTTP-Version\":\"\"}";
        com.google.gson.JsonObject json = new Gson().fromJson(jsono, com.google.gson.JsonObject.class);

        if(json.getAsJsonObject("Method").has("stream")) {

            System.out.println("good");
        }

            long first = RMemoryAPI.getInstance().currentTimeMillis();
        try {
            Thread.sleep(900);
        } catch (Exception e){}
*/


        String json = "{\n" +
                "  \"id\": 49342345,\n" +
                "  \"event\": \"job.completed\",\n" +
                "  \"output_urls\": {\n" +
                "    \"mp4_360p\": \"http://videobucket.s3.amazonaws.com/1234/test_360p.mp4\",\n" +
                "    \"mp4_240p\": \"http://videobucket.s3.amazonaws.com/1234/test_240p.mp4\"\n" +
                "  },\n" +
                "  \"errors\": {},\n" +
                "  \"metadata\": {\n" +
                "    \"mp4:360p\": {\n" +
                "      \"streams\": {\n" +
                "        \"video\": {\n" +
                "          \"codec\": \"h264\",\n" +
                "          \"width\": 480,\n" +
                "          \"height\": 360,\n" +
                "          \"aspect\": 1.33,\n" +
                "          \"pix_fmt\": \"yuv420p\",\n" +
                "          \"fps\": 15.0,\n" +
                "          \"bitrate\": 795\n" +
                "        },\n" +
                "        \"audio\": {\n" +
                "          \"codec\": \"aac\",\n" +
                "          \"sample_rate\": 44100,\n" +
                "          \"channels\": 2,\n" +
                "          \"bitrate\": 128\n" +
                "        }\n" +
                "      },\n" +
                "      \"format\": {\n" +
                "        \"name\": \"mov\",\n" +
                "        \"duration\": 5,\n" +
                "        \"size\": 517120,\n" +
                "        \"mime_type\": \"video/mp4\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"source\": {\n" +
                "      \"streams\": {\n" +
                "        \"video\": {\n" +
                "          \"codec\": \"mpeg4\",\n" +
                "          \"width\": 320,\n" +
                "          \"height\": 240,\n" +
                "          \"aspect\": 1.33,\n" +
                "          \"pix_fmt\": \"yuv420p\",\n" +
                "          \"fps\": 15.0,\n" +
                "          \"bitrate\": 501\n" +
                "        },\n" +
                "        \"audio\": {\n" +
                "          \"codec\": \"aac\",\n" +
                "          \"sample_rate\": 24000,\n" +
                "          \"channels\": 2,\n" +
                "          \"bitrate\": 53\n" +
                "        }\n" +
                "      },\n" +
                "      \"format\": {\n" +
                "        \"name\": \"mov\",\n" +
                "        \"duration\": 5,\n" +
                "        \"size\": 325632,\n" +
                "        \"mime_type\": \"video/mp4\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"mp4:240p\": {\n" +
                "      \"streams\": {\n" +
                "        \"video\": {\n" +
                "          \"codec\": \"h264\",\n" +
                "          \"width\": 320,\n" +
                "          \"height\": 240,\n" +
                "          \"aspect\": 1.33,\n" +
                "          \"pix_fmt\": \"yuv420p\",\n" +
                "          \"fps\": 15.0,\n" +
                "          \"bitrate\": 460\n" +
                "        },\n" +
                "        \"audio\": {\n" +
                "          \"codec\": \"aac\",\n" +
                "          \"sample_rate\": 44100,\n" +
                "          \"channels\": 2,\n" +
                "          \"bitrate\": 53\n" +
                "        }\n" +
                "      },\n" +
                "      \"format\": {\n" +
                "        \"name\": \"mov\",\n" +
                "        \"duration\": 5,\n" +
                "        \"size\": 299008,\n" +
                "        \"mime_type\": \"video/mp4\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

       /* com.google.gson.JsonObject jobj = new Gson().fromJson(json, com.google.gson.JsonObject.class);
        String j = jobj.getAsJsonObject("metadata")
                .getAsJsonObject("mp4:360p")
                .getAsJsonObject("format")
                .get("duration").getAsString();
        System.out.println(j);*/

        System.out.println(Instant.now().toString());


        //s3Module.uploadImageFile("1/image/test.jpg", "/home/sardor/Pictures/9341781.jpg");

        //String stream = "live_id2419_51635?sdfsdfs";
        //System.out.println(stream.substring(0, stream.contains("?")?stream.indexOf("?"):stream.length()));



    }
}