package com.project.app.api.Graphs;


public class ArtistSpecificGraphs {

    String responseMessage;

    SimplePieChart pieChart;

    GameAvgResponseBar barChart;

    public ArtistSpecificGraphs() {}

    public ArtistSpecificGraphs(SimplePieChart pieChart, GameAvgResponseBar barChart) {
        this.pieChart = pieChart;
        this.barChart = barChart;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public SimplePieChart getPieChart() {
        return pieChart;
    }

    public void setPieChart(SimplePieChart pieChart) {
        this.pieChart = pieChart;
    }

    public GameAvgResponseBar getBarChart() {
        return barChart;
    }

    public void setBarChart(GameAvgResponseBar barChart) {
        this.barChart = barChart;
    }
}
