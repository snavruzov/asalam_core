package com.dgtz.api.constants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * BroCast.
 * Copyright: Sardor Navruzov
 * 2013-2016.
 */
public class InboxMapper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InboxMapper.class);
    private static InboxMapper ourInstance = new InboxMapper();

    public static InboxMapper getInstance() {
        return ourInstance;
    }

    private InboxMapper() {
        map();
    }

    public void init(){
        if(ourInstance==null){
            log.debug("Inited mapper");
            map();
        }
    }

    public void map() {
        // Only one time
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
