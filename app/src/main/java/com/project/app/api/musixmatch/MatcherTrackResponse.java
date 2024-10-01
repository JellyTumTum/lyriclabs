package com.project.app.api.musixmatch;

public class MatcherTrackResponse {
    private Message message;

    // Getters and Setters

    public static class Message {
        private Header header;
        private Body body;

        // Getters and Setters

        public static class Header {
            private int status_code;
            private double execute_time;
            private int confidence;

            // Getters and Setters
        }

        public static class Body {
            private String track;

            // Getters and Setters
        }
    }
}

