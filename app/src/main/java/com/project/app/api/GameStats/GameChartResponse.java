package com.project.app.api.GameStats;

import com.project.app.api.Graphs.CorrectGuessLineGraph;
import com.project.app.api.Graphs.GameAvgResponseBar;
import com.project.app.api.Graphs.SimplePieChart;
import com.project.app.api.rooms.FrontendArtist;

import java.util.List;

public class GameChartResponse {

    String responseMessage;

    GameAvgResponseBar avgResponseBarChartData;

    CorrectGuessLineGraph correctGuessLineGraphData;

    SimplePieChart correctGuessPieChartData;

    String notification;

    public GameChartResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public GameChartResponse() {
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public GameAvgResponseBar getAvgResponseBarChartData() {
        return avgResponseBarChartData;
    }

    public void setAvgResponseBarChartData(GameAvgResponseBar avgResponseBarChartData) {
        this.avgResponseBarChartData = avgResponseBarChartData;
    }

    public CorrectGuessLineGraph getCorrectGuessLineGraphData() {
        return correctGuessLineGraphData;
    }

    public void setCorrectGuessLineGraphData(CorrectGuessLineGraph correctGuessLineGraphData) {
        this.correctGuessLineGraphData = correctGuessLineGraphData;
    }

    public SimplePieChart getCorrectGuessPieChartData() {
        return correctGuessPieChartData;
    }

    public void setCorrectGuessPieChartData(SimplePieChart correctGuessPieChartData) {
        this.correctGuessPieChartData = correctGuessPieChartData;
    }


}
