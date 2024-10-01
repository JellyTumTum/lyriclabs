package com.project.app.api.Graphs;
import java.util.List;

public class GameAvgResponseBar {

    private List<String> labels; // Users
    private List<DataSet> datasets;

    public GameAvgResponseBar(List<String> labels, List<DataSet> datasets) {
        this.labels = labels;
        this.datasets = datasets;
    }

    // Getters and Setters
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DataSet> datasets) {
        this.datasets = datasets;
    }

    public static class DataSet {
        private String label; // Y-Axis Label. eg "Average Guess Time"
        private List<Float> data; // values of bars y axis. (average guess time basically
        private List<String> backgroundColor; // Colors for each bar
        private List<String> borderColor; // Border colors for each bar
        private int borderWidth; // Width of the border of the bars

        public DataSet(String label, List<Float> data, List<String> backgroundColor, List<String> borderColor, int borderWidth) {
            this.label = label;
            this.data = data;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
        }

        // Getters and Setters
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

        public List<String> getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(List<String> backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public List<String> getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(List<String> borderColor) {
            this.borderColor = borderColor;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }
    }
}

