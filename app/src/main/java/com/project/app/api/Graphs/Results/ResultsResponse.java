package com.project.app.api.Graphs.Results;

import com.project.app.api.Graphs.ScatterGraph;

public class ResultsResponse {

    String responseMessage;
    PercentGuessLineGraphData percentGuessLineGraphData;
    
    ScatterGraph roomAverageResponseAccuracyScatterGraph;

    ScatterGraph timeRoomAverageResponseAccuracyScatterGraph;

    ScatterGraph multiplayerPracticeTimeComparisonScatterGraph;

    public ResultsResponse() {

    }

    public ResultsResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public PercentGuessLineGraphData getPercentGuessLineGraphData() {
        return percentGuessLineGraphData;
    }

    public void setPercentGuessLineGraphData(PercentGuessLineGraphData percentGuessLineGraphData) {
        this.percentGuessLineGraphData = percentGuessLineGraphData;
    }

    public ScatterGraph getRoomAverageResponseAccuracyScatterGraph() {
        return roomAverageResponseAccuracyScatterGraph;
    }

    public void setRoomAverageResponseAccuracyScatterGraph(ScatterGraph roomAverageResponseAccuracyScatterGraph) {
        this.roomAverageResponseAccuracyScatterGraph = roomAverageResponseAccuracyScatterGraph;
    }

    public ScatterGraph getTimeRoomAverageResponseAccuracyScatterGraph() {
        return timeRoomAverageResponseAccuracyScatterGraph;
    }

    public void setTimeRoomAverageResponseAccuracyScatterGraph(ScatterGraph timeRoomAverageResponseAccuracyScatterGraph) {
        this.timeRoomAverageResponseAccuracyScatterGraph = timeRoomAverageResponseAccuracyScatterGraph;
    }

    public ScatterGraph getMultiplayerPracticeTimeComparisonScatterGraph() {
        return multiplayerPracticeTimeComparisonScatterGraph;
    }

    public void setMultiplayerPracticeTimeComparisonScatterGraph(ScatterGraph multiplayerPracticeTimeComparisonScatterGraph) {
        this.multiplayerPracticeTimeComparisonScatterGraph = multiplayerPracticeTimeComparisonScatterGraph;
    }
}
