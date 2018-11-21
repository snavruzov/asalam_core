package com.dgtz.api.beans;

import com.dgtz.db.api.factory.GsonInsta;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sardor on 2/26/17.
 */
public class EventClipsEntity {
    public String url = "https://d23emc6sfto8yr.cloudfront.net/default/hls_vod/demo_event.m3u8";
    public String thumb = "https://d28kehli7v50ap.cloudfront.net/defaults/media0.jpg";
    public int defclip = 0;

    public static ArrayList<EventClipsEntity> getClips(){
        ArrayList<EventClipsEntity> list = new ArrayList<>();
        EventClipsEntity entity = new EventClipsEntity();
        entity.url = "https://d23emc6sfto8yr.cloudfront.net/default/hls_vod/demo_event.m3u8";
        entity.thumb = "https://d28kehli7v50ap.cloudfront.net/defaults/media0.jpg";
        entity.defclip = 0;
        list.add(entity);

        entity = new EventClipsEntity();
        entity.url = "https://d23emc6sfto8yr.cloudfront.net/default/hls_vod/demo_event1.m3u8";
        entity.thumb = "https://d28kehli7v50ap.cloudfront.net/defaults/media1.jpg";
        entity.defclip = 1;
        list.add(entity);

        entity = new EventClipsEntity();
        entity.url = "https://d23emc6sfto8yr.cloudfront.net/default/hls_vod/demo_event2.m3u8";
        entity.thumb = "https://d28kehli7v50ap.cloudfront.net/defaults/media2.jpg";
        entity.defclip = 2;
        list.add(entity);

        return list;
    }

    @Override
    public String toString() {
        Gson gson = GsonInsta.getInstance();
        return gson.toJson(this);
    }
}
