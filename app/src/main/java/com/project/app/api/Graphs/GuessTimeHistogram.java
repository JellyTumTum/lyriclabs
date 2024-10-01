package com.project.app.api.Graphs;

import java.util.List;

public class GuessTimeHistogram {
    private List<String> labels; // represents time intervals.
    private List<Dataset> datasets;

    public GuessTimeHistogram(List<String> labels, List<Dataset> datasets) {
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

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public static class Dataset {
        private String label; // For differentiating multiple datasets, like "Global" vs "By Artist"
        private List<Integer> data; // Count of guesses within each interval
        private String backgroundColor; // To customize the look of the histogram bars

        public Dataset(String label, List<Integer> data, String backgroundColor) {
            this.label = label;
            this.data = data;
            this.backgroundColor = backgroundColor;
        }

        // Getters and Setters
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
    }
}
