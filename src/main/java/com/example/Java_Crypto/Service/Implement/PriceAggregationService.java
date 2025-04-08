package com.example.Java_Crypto.Service.Implement;


import com.example.Java_Crypto.Entity.CryptoPrice;
import com.example.Java_Crypto.Repository.CryptoPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Iterator;

@Service
@AllArgsConstructor
public class PriceAggregationService {

    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    private static final String ETHUSDT = "ETHUSDT";
    private static final String BTCUSDT = "BTCUSDT";


    private final CryptoPriceRepository cryptoPriceRepository;


    private final RestTemplate restTemplate;


    private final ObjectMapper objectMapper;

    // Scheduler chạy mỗi 10 giây (10000 milliseconds)
    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        try {
            // Lấy dữ liệu từ Binance
            ResponseEntity<String> binanceResponse = restTemplate.getForEntity(BINANCE_URL, String.class);
            JsonNode binanceData = objectMapper.readTree(binanceResponse.getBody());

            // Lấy dữ liệu từ Huobi
            ResponseEntity<String> huobiResponse = restTemplate.getForEntity(HUOBI_URL, String.class);
            JsonNode huobiRoot = objectMapper.readTree(huobiResponse.getBody());
            JsonNode huobiData = huobiRoot.get("data");

            // Tính toán giá tốt nhất cho từng pair
            processPair(ETHUSDT, binanceData, huobiData);
            processPair(BTCUSDT, binanceData, huobiData);

        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi ở đây theo framework logging bạn dùng (vd: SLF4J)
        }
    }

    /**
     * Xử lý tính giá tốt nhất cho một pair dựa trên dữ liệu từ Binance và Huobi.
     *
     * Hints:
     * - Dùng askPrice để tính giá mua (BUY order) → Chọn ask giá thấp nhất.
     * - Dùng bidPrice để tính giá bán (SELL order) → Chọn bid giá cao nhất.
     */
    private void processPair(String pair, JsonNode binanceData, JsonNode huobiData) {
        double bestAsk = Double.MAX_VALUE; // Giá mua tốt nhất = thấp nhất
        double bestBid = 0;                // Giá bán tốt nhất = cao nhất

        // Xử lý dữ liệu từ Binance:
        Iterator<JsonNode> binanceIterator = binanceData.elements();
        while (binanceIterator.hasNext()) {
            JsonNode ticker = binanceIterator.next();
            if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                double askPrice = ticker.get("askPrice").asDouble();
                double bidPrice = ticker.get("bidPrice").asDouble();
                bestAsk = Math.min(bestAsk, askPrice);
                bestBid = Math.max(bestBid, bidPrice);
            }
        }

        // Xử lý dữ liệu từ Huobi:
        if (huobiData != null) {
            Iterator<JsonNode> huobiIterator = huobiData.elements();
            while (huobiIterator.hasNext()) {
                JsonNode ticker = huobiIterator.next();
                // Ví dụ: Huobi dùng ký hiệu thường là chữ thường (ethusdt), nên có thể chuyển về uppercase để so sánh.
                if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                    double askPrice = ticker.get("ask").asDouble();
                    double bidPrice = ticker.get("bid").asDouble();
                    bestAsk = Math.min(bestAsk, askPrice);
                    bestBid = Math.max(bestBid, bidPrice);
                }
            }
        }

        // Kiểm tra nếu không tìm thấy dữ liệu nào, có thể bỏ qua.
        if (bestAsk == Double.MAX_VALUE || bestBid == 0) {
            System.out.println("No pricing data found for pair: " + pair);
            return;
        }

        // Lưu vào database thông qua repository
        CryptoPrice cryptoPrice = new CryptoPrice();
        cryptoPrice.setPair(pair);
        cryptoPrice.setBestAskPrice(bestAsk);
        cryptoPrice.setBestBidPrice(bestBid);
        cryptoPrice.setUpdatedAt(LocalDateTime.now());

        cryptoPriceRepository.save(cryptoPrice);

        System.out.println("Aggregated " + pair + " -> Best Ask: " + bestAsk + ", Best Bid: " + bestBid);
    }
}

