package StockMarketTracker;

import java.math.BigDecimal;
import java.util.Date;

public class StockData {
    private final Date timestamp;
    private final BigDecimal price;

    public StockData(Date timestamp, BigDecimal price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}