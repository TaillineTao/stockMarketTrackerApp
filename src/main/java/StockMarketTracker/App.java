package StockMarketTracker;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static java.lang.Thread.sleep;

public class App extends Application {
    // The URL for the API
//    private static final String YAHOO_FINANCE_API =
//            "https://finance.yahoo.com/quote/";

    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Dow Jones Industrial Average");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Stock Price (USD)");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Dow Jones Industrial Average Stock Price");
        series = new XYChart.Series<>();
        // series.setName("Line");
        lineChart.getData().add(series);
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();

        // Start a background thread for querying and updating the chart data
        new Thread(this::updateData).start();
    }

    private void updateData() {
        // Queue for containing timestamps and stock price
        Queue<StockData> stockDataQueue = new LinkedList<>();

        long startSeconds = Instant.now().getEpochSecond();

        // This is the loop for querying data
        while (true) {

            // Try to query the stock information
            try {
                // Stock symbol for the Dow Jones Industrial Average
                String symbol = "^DJI";
                Stock stock = YahooFinance.get(symbol);
                // Get the current stock price
                BigDecimal price = stock.getQuote().getPrice();
                // Record the timestamp for the query
                Date timestamp = new Date();
                long currSeconds = Instant.now().getEpochSecond();

                // Add the stockData to the queue, in the form (timestamp, price)
                StockData stockData = new StockData(timestamp, price);
                stockDataQueue.add(stockData);

                // Print the stockData
                System.out.println(stockData);

                // Update the chart data
                long secSinceStart = currSeconds - startSeconds;
                // Update the chart data within the JavaFX Application Thread

                Platform.runLater(() -> {

                    series.getData().add(new XYChart.Data<>(secSinceStart, price));

                    // To ensure that we're not adding the series multiple times,
                    // first check if the lineChart data already contains the series.
                    if (!lineChart.getData().contains(series)) {
                        lineChart.getData().add(series);
                    }

                });
            } catch (IOException e) {
                System.out.println("Failure to connect. Trying again.");
            }
            // Catch exception if there is a connection error

            // Wait before repeating the query
            try {
                // Wait time in milliseconds between queries
                int waitTimeMs = 1000;
                sleep(waitTimeMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) { launch(args); }
}