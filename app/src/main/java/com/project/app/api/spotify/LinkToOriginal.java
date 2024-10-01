package com.project.app.api.spotify;

import java.util.Map;

public class LinkToOriginal {

    // From some research, appears that things like songs can have duplicates due to other markets or re-releases as part of other albums.
    // This can be used (hopefully as of 02/01/2024) to retrieve the original so dupes arent stored in the database.
    private Map<String, String> external_urls;
    private String href;
    private String id;
    private String type;
    private String uri;

    public Map<String, String> getExternal_urls() {
        return external_urls;
    }

    public void setExternal_urls(Map<String, String> external_urls) {
        this.external_urls = external_urls;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public LinkToOriginal() {}
}
