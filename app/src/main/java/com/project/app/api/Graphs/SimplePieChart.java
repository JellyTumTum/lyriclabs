package com.project.app.api.Graphs;

import java.util.List;

public class SimplePieChart {
    private List<String> labels; // Usernames
    private List<Dataset> datasets; // Contains counts of first correct guesses and colors

    public SimplePieChart(List<String> labels, Dataset dataset) {
        this.labels = labels;
        this.datasets = List.of(dataset);
    }

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
        private List<Integer> data; // Counts of first correct guesses for each user
        private List<String> backgroundColor; // Background colors for each slice
        private List<String> borderColor; // Border colors for each slice
        private int borderWidth; // Border width for slices

        public Dataset(List<Integer> data, List<String> backgroundColors, List<String> borderColors, int borderWidth) {
            this.data = data;
            this.backgroundColor = backgroundColors;
            this.borderColor = borderColors;
            this.borderWidth = borderWidth;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
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
