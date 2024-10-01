package com.project.app.api.Graphs;

import java.util.List;

public class ScatterGraph {
    private List<String> labels;
    private List<ScatterDataSet> datasets;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<ScatterDataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<ScatterDataSet> datasets) {
        this.datasets = datasets;
    }

    public ScatterGraph(List<String> labels, List<ScatterDataSet> datasets) {
        this.labels = labels;
        this.datasets = datasets;
    }

    public static class ScatterDataSet {
        private String label;
        private List<Point> data;
        private String backgroundColor;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Point> getData() {
            return data;
        }

        public void setData(List<Point> data) {
            this.data = data;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public ScatterDataSet() {}

        // Constructor
        public ScatterDataSet(String label, List<Point> data, String backgroundColor) {
            this.label = label;
            this.data = data;
            this.backgroundColor = backgroundColor;
        }

        public static class Point {
            private double x; // x-coordinate (Average response time)
            private double y; // y-coordinate (Accuracy percentage)

            private String roomID;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }

            public String getRoomID() {
                return roomID;
            }

            public void setRoomID(String roomID) {
                this.roomID = roomID;
            }

            public Point(double x, double y, String roomID) {
                this.x = x;
                this.y = y;
                this.roomID = roomID;
            }
        }
    }
}

