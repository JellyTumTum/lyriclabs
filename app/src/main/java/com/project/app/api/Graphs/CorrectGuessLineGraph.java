package com.project.app.api.Graphs;
import java.util.List;

public class CorrectGuessLineGraph {
    public List<String> labels; // Represents rounds
    public List<LineDataSet> datasets;

    public CorrectGuessLineGraph(List<String> labels, List<LineDataSet> datasets) {
        this.labels = labels;
        this.datasets = datasets;
    }

    // Getters and Setters

    public static class LineDataSet {
        public String label; // User's name or identifier
        public List<Float> data; // Time to correct guess per round, use null for gaps
        public String borderColor; // Color of the line
        public int borderWidth;
        public boolean fill; // Set to false

        public LineDataSet(String label, List<Float> data, String borderColor, int borderWidth) {
            this.label = label;
            this.data = data;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.fill = false;
        }

        public LineDataSet() {}

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Float> getData() {
            return data;
        }

        public void setData(List<Float> data) {
            this.data = data;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        public boolean isFill() {
            return fill;
        }

        public void setFill(boolean fill) {
            this.fill = fill;
        }
    }
}

