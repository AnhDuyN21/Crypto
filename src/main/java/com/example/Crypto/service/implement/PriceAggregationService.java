package com.example.Crypto.service.implement;

import com.example.Crypto.entity.CryptoPrice;
import com.example.Crypto.repository.CryptoPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Iterator;

@Service
@AllArgsConstructor
public class PriceAggregationService {

    // URL API từ các nguồn
    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    // Các cặp giao dịch cần aggregate
    private static final String ETHUSDT = "ETHUSDT";
    private static final String BTCUSDT = "BTCUSDT";


    private final CryptoPriceRepository cryptoPriceRepository;


    private final RestTemplate restTemplate;


    private final ObjectMapper objectMapper;

    // Phương thức sẽ được chạy định kỳ mỗi 10 giây
    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        try {
            // 1. Gọi API Binance và chuyển kết quả sang dạng JsonNode
            ResponseEntity<String> binanceResponse = restTemplate.getForEntity(BINANCE_URL, String.class);
            JsonNode binanceData = objectMapper.readTree(binanceResponse.getBody());

            // 2. Gọi API Huobi
            ResponseEntity<String> huobiResponse = restTemplate.getForEntity(HUOBI_URL, String.class);
            JsonNode huobiRoot = objectMapper.readTree(huobiResponse.getBody());
            // Huobi trả về dữ liệu chứa trong key "data"
            JsonNode huobiData = huobiRoot.get("data");

            // 3. Xử lý aggregate cho các cặp giao dịch cần thiết
            processPair(ETHUSDT, binanceData, huobiData);
            processPair(BTCUSDT, binanceData, huobiData);
        } catch (Exception e) {
            e.printStackTrace();
            // Ở đây nên dùng framework logging (ví dụ SLF4J) để log lỗi chi tiết
        }
    }

    /**
     * Phương thức xử lý tính toán giá tốt nhất cho một pair từ dữ liệu của Binance và Huobi.
     *
     * @param pair       Cặp giao dịch cần xử lý (ví dụ: "ETHUSDT")
     * @param binanceData Dữ liệu từ Binance dưới dạng JsonNode (dạng mảng ticker)
     * @param huobiData   Dữ liệu từ Huobi dưới dạng JsonNode (danh sách ticker, nằm trong key "data")
     */
    private void processPair(String pair, JsonNode binanceData, JsonNode huobiData) {
        // Khởi tạo bestAsk với giá cao nhất có thể và bestBid với 0
        double bestAsk = Double.MAX_VALUE; // Giá mua tốt nhất: thấp nhất
        double bestBid = 0;                // Giá bán tốt nhất: cao nhất

        // Xử lý dữ liệu từ Binance:
        Iterator<JsonNode> binanceIterator = binanceData.elements();
        while (binanceIterator.hasNext()) {
            JsonNode ticker = binanceIterator.next();
            // So sánh symbol từ ticker với pair cần xử lý (không phân biệt chữ hoa/chữ thường)
            if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                // Lấy giá ask và bid từ ticker
                double askPrice = ticker.get("askPrice").asDouble();
                double bidPrice = ticker.get("bidPrice").asDouble();
                // Cập nhật bestAsk và bestBid bằng cách so sánh giá hiện tại với giá đã có
                bestAsk = Math.min(bestAsk, askPrice);
                bestBid = Math.max(bestBid, bidPrice);
            }
        }

        // Xử lý dữ liệu từ Huobi (nếu có):
        if (huobiData != null) {
            Iterator<JsonNode> huobiIterator = huobiData.elements();
            while (huobiIterator.hasNext()) {
                JsonNode ticker = huobiIterator.next();
                // Ví dụ: Huobi có thể cung cấp symbol bằng chữ thường, nên dùng equalsIgnoreCase
                if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                    double askPrice = ticker.get("ask").asDouble();
                    double bidPrice = ticker.get("bid").asDouble();
                    bestAsk = Math.min(bestAsk, askPrice);
                    bestBid = Math.max(bestBid, bidPrice);
                }
            }
        }

        // Nếu không tìm thấy dữ liệu hợp lệ cho cặp, thông báo và dừng xử lý
        if (bestAsk == Double.MAX_VALUE || bestBid == 0) {
            System.out.println("No pricing data found for pair: " + pair);
            return;
        }

        // Tạo đối tượng CryptoPrice với giá tốt nhất đã được tính
        CryptoPrice cryptoPrice = new CryptoPrice();
        cryptoPrice.setPair(pair);
        cryptoPrice.setBestAskPrice(bestAsk);
        cryptoPrice.setBestBidPrice(bestBid);
        cryptoPrice.setUpdatedAt(LocalDateTime.now());

        // Lưu đối tượng vào DB thông qua repository
        cryptoPriceRepository.save(cryptoPrice);

        System.out.println("Aggregated " + pair + " -> Best Ask: " + bestAsk + ", Best Bid: " + bestBid);
    }
}