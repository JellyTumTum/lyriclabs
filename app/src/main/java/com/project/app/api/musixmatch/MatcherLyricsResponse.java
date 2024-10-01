package com.project.app.api.musixmatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.app.model.music.Song;

public class MatcherLyricsResponse {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message {
        private Header header;
        private Body body;

        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }

        @Override
        public String toString() {
            return this.getBody().getLyrics().toString();
        }

        public static class Header {
            private int status_code;
            private double execute_time;

            public int getStatus_code() {
                return status_code;
            }

            public void setStatus_code(int status_code) {
                this.status_code = status_code;
            }

            public double getExecute_time() {
                return execute_time;
            }

            public void setExecute_time(double execute_time) {
                this.execute_time = execute_time;
            }

        }

        public static class Body {
            private Lyrics lyrics;

            public Lyrics getLyrics() {
                return lyrics;
            }

            public void setLyrics(Lyrics lyrics) {
                this.lyrics = lyrics;
            }

            public static class Lyrics {
                private int restricted;
                private int instrumental;
                private String lyricsLanguage;

                private int explicit;

                // URLs and copyright info --> needs to be stored for legality n that.

                @JsonProperty("lyrics_id")
                private int lyricsID;
                @JsonProperty("lyrics_body")
                private String lyricsBody;
                @JsonProperty("script_tracking_url")
                private String scriptTrackingUrl;
                private boolean hasScriptTracking;
                private boolean hasPixelTracking;
                private boolean hasHtmlTracking;
                @JsonProperty("pixel_tracking_url")
                private String pixelTrackingUrl;

                @JsonProperty("html_tracking_url")
                private String htmlTrackingUrl;

                @JsonProperty("lyrics_copyright")
                private String lyricsCopyright;
                @JsonProperty("updated_time")
                private String updatedTime;

                public int getLyricsID() {
                    return lyricsID;
                }

                public void setLyricsID(int lyricsID) {
                    this.lyricsID = lyricsID;
                }

                public int getRestricted() {
                    return restricted;
                }

                public void setRestricted(int restricted) {
                    this.restricted = restricted;
                }

                public int getInstrumental() {
                    return instrumental;
                }

                public void setInstrumental(int instrumental) {
                    this.instrumental = instrumental;
                }

                public String getLyricsBody() {
                    return lyricsBody;
                }

                public void setLyricsBody(String lyricsBody) {
                    this.lyricsBody = lyricsBody;
                }

                public String getLyricsLanguage() {
                    return lyricsLanguage;
                }

                public void setLyricsLanguage(String lyricsLanguage) {
                    this.lyricsLanguage = lyricsLanguage;
                }

                public String getScriptTrackingUrl() {
                    return scriptTrackingUrl;
                }

                public void setScriptTrackingUrl(String scriptTrackingUrl) {
                    this.scriptTrackingUrl = scriptTrackingUrl;
                }

                public String getPixelTrackingUrl() {
                    return pixelTrackingUrl;
                }

                public void setPixelTrackingUrl(String pixelTrackingUrl) {
                    this.pixelTrackingUrl = pixelTrackingUrl;
                }

                public String getHtmlTrackingUrl() {
                    return htmlTrackingUrl;
                }

                public void setHtmlTrackingUrl(String htmlTrackingUrl) {
                    this.htmlTrackingUrl = htmlTrackingUrl;
                }

                public String getLyricsCopyright() {
                    return lyricsCopyright;
                }

                public void setLyricsCopyright(String lyricsCopyright) {
                    this.lyricsCopyright = lyricsCopyright;
                }

                public String getUpdatedTime() {
                    return updatedTime;
                }

                public void setUpdatedTime(String updatedTime) {
                    this.updatedTime = updatedTime;
                }

                public int getExplicit() {
                    return explicit;
                }

                public void setExplicit(int explicit) {
                    this.explicit = explicit;
                }

                public boolean isHasScriptTracking() {
                    return hasScriptTracking;
                }

                public void setHasScriptTracking(boolean hasScriptTracking) {
                    this.hasScriptTracking = hasScriptTracking;
                }

                public boolean isHasPixelTracking() {
                    return hasPixelTracking;
                }

                public void setHasPixelTracking(boolean hasPixelTracking) {
                    this.hasPixelTracking = hasPixelTracking;
                }

                public boolean isHasHtmlTracking() {
                    return hasHtmlTracking;
                }

                public void setHasHtmlTracking(boolean hasHtmlTracking) {
                    this.hasHtmlTracking = hasHtmlTracking;
                }

                @Override
                public String toString() {
                    return "Lyrics{" +
                            "lyricsID=" + lyricsID +
                            ", restricted=" + restricted +
                            ", instrumental=" + instrumental +
                            ", lyricsLanguage='" + lyricsLanguage + '\'' +
                            ", explicit=" + explicit +
                            ", lyricsBody= NOT PRINTING DUE TO LENGTH " +
                            ", scriptTrackingUrl='" + scriptTrackingUrl + '\'' +
                            ", pixelTrackingUrl='" + pixelTrackingUrl + '\'' +
                            ", htmlTrackingUrl='" + htmlTrackingUrl + '\'' +
                            ", lyricsCopyright='" + lyricsCopyright + '\'' +
                            ", updatedTime='" + updatedTime + '\'' +
                            '}';
                }

            }
        }
    }

    public MatcherLyricsResponse() {

    }
}

