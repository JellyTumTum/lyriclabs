package com.project.app.api.spotify;

import java.util.List;

public class ArtistSearchResponse {

    public static class Artists {
        private String href;
        private List<ArtistItem> items;
        private int limit;
        private String next;
        private int offset;
        private String previous;
        private int total;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public List<ArtistItem> getItems() {
            return items;
        }

        public void setItems(List<ArtistItem> items) {
            this.items = items;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public Artists() {}

        @Override
        public String toString() {
            return "Artists{\n" +
                    "  href='" + href + '\'' + "\n" +
                    "  items=" + items + "\n" +
                    "  limit=" + limit + "\n" +
                    "  next='" + next + '\'' + "\n" +
                    "  offset=" + offset + "\n" +
                    "  previous='" + previous + '\'' + "\n" +
                    "  total=" + total + "\n" +
                    '}';
        }
    }



    public static class Followers {
        private int total;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public Followers() {}

        @Override
        public String toString() {
            return "Followers{\n" +
                    "  total=" + total + "\n" +
                    '}';
        }
    }


    private Artists artists;

    // Getters and Setters
    public Artists getArtists() {
        return artists;
    }

    public void setArtists(Artists artists) {
        this.artists = artists;
    }

    public ArtistSearchResponse() {}

    @Override
    public String toString() {
        return "ArtistSearchResponse{\n" +
                "  artists=" + (artists == null ? "null" : artists.toString()) + "\n" +
                '}';
    }
}



