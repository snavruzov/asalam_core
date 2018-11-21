package com.dgtz.api.beans;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sardor
 * Date: 12/19/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentInfo extends Info {

    private MediaInfo mediaInfo;
    private String contentType;
    private String author;
    private List<Tag> tags;
    private CountableInfo cntInfo;
    private CommentInfo commentInfo;

    public ContentInfo() {
    }

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public CountableInfo getCntInfo() {
        return cntInfo;
    }

    public void setCntInfo(CountableInfo cntInfo) {
        this.cntInfo = cntInfo;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
