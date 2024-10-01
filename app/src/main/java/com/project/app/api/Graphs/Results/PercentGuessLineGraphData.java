package com.project.app.api.Graphs.Results;

import java.util.List;

public class PercentGuessLineGraphData {
    private List<String> labels;
    private List<Dataset> datasets;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public PercentGuessLineGraphData(List<String> labels, List<Dataset> datasets) {
        this.labels = labels;
        this.datasets = datasets;
    }

    public PercentGuessLineGraphData() {}

    public static class Dataset {
        private List<Double> data; // Data points
        private String label;
        private String borderColor; // Line color
        private String backgroundColor; // Fill color (optional)

        public Dataset(List<Double> data, String borderColor, String backgroundColor, String label) {
            this.data = data;
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
            this.label = label;
        }

        public List<Double> getData() {
            return data;
        }

        public void setData(List<Double> data) {
            this.data = data;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
    }
}
