package com.dgtz.api.beans;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class MediaStreamFormat {

    private Integer index;
    private Integer width;
    private Integer height;
    private String codec_type;
    private String r_frame_rate;
    private Integer bit_rate;
    private Integer channels;
    private TagsInfo tags;

    public String getR_frame_rate() {
        return r_frame_rate;
    }

    public void setR_frame_rate(String r_frame_rate) {
        this.r_frame_rate = r_frame_rate;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getBit_rate() {
        return bit_rate;
    }

    public void setBit_rate(Integer bit_rate) {
        this.bit_rate = bit_rate;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCodec_type() {
        return codec_type;
    }

    public void setCodec_type(String codec_type) {
        this.codec_type = codec_type;
    }

    public TagsInfo getTags() {
        return tags;
    }

    public void setTags(TagsInfo tags) {
        this.tags = tags;
    }
}
