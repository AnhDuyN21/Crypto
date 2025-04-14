package com.example.Crypto.service.implement;

import com.example.Crypto.entity.CryptoPrice;
import com.example.Crypto.repository.CryptoPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class PriceAggregationService {

    // URLs cho API
    private static final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    // Các cặp giao dịch cần xử lý (bạn có thể mở rộng thành danh sách nếu cần)
    private static final String ETHUSDT = "ETHUSDT";
    private static final String BTCUSDT = "BTCUSDT";

    private final CryptoPriceRepository cryptoPriceRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Chạy mỗi 10 giây
    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        try {
            // Gọi API Binance
            ResponseEntity<String> binanceResponse = restTemplate.getForEntity(BINANCE_URL, String.class);
            JsonNode binanceData = objectMapper.readTree(binanceResponse.getBody());

            // Gọi API Huobi; Huobi trả về JSON có key "data"
            ResponseEntity<String> huobiResponse = restTemplate.getForEntity(HUOBI_URL, String.class);
            JsonNode huobiRoot = objectMapper.readTree(huobiResponse.getBody());
            JsonNode huobiData = huobiRoot.get("data");

            // Xử lý từng cặp giao dịch; có thể mở rộng thêm cặp khác nếu cần.
            processAndUpdatePair(ETHUSDT, binanceData, huobiData);
            processAndUpdatePair(BTCUSDT, binanceData, huobiData);

        } catch (Exception e) {
            e.printStackTrace();
            // Nên sử dụng logging framework để log chi tiết lỗi thay vì printStackTrace()
        }
    }

    /**
     * Phương thức này xử lý tính toán giá cho cặp giao dịch 'pair' từ dữ liệu của Binance và Huobi,
     * so sánh với bản ghi hiện có trong DB và lưu giá mới nhất.
     */
    private void processAndUpdatePair(String pair, JsonNode binanceData, JsonNode huobiData) {
        BigDecimal bestAsk = BigDecimal.valueOf(Double.MAX_VALUE); // Giá mua tốt nhất
        BigDecimal bestBid = BigDecimal.ZERO;                      // Giá bán tốt nhất

        // Duyệt dữ liệu từ Binance
        Iterator<JsonNode> binanceIterator = binanceData.elements();
        while (binanceIterator.hasNext()) {
            JsonNode ticker = binanceIterator.next();
            if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                JsonNode askNode = ticker.get("askPrice");
                JsonNode bidNode = ticker.get("bidPrice");

                if (askNode != null && bidNode != null) {
                    BigDecimal askPrice = new BigDecimal(askNode.asText());
                    BigDecimal bidPrice = new BigDecimal(bidNode.asText());
                    bestAsk = bestAsk.min(askPrice);
                    bestBid = bestBid.max(bidPrice);
                }
            }
        }

        // Duyệt dữ liệu từ Huobi
        if (huobiData != null) {
            Iterator<JsonNode> huobiIterator = huobiData.elements();
            while (huobiIterator.hasNext()) {
                JsonNode ticker = huobiIterator.next();
                if (pair.equalsIgnoreCase(ticker.get("symbol").asText())) {
                    JsonNode askNode = ticker.get("askPrice");
                    JsonNode bidNode = ticker.get("bidPrice");

                    // Nếu API Huobi dùng "ask" và "bid" thay vì "askPrice"/"bidPrice" => thay đổi key ở đây
                    if (askNode != null && bidNode != null) {
                        BigDecimal askPrice = new BigDecimal(askNode.asText());
                        BigDecimal bidPrice = new BigDecimal(bidNode.asText());
                        bestAsk = bestAsk.min(askPrice);
                        bestBid = bestBid.max(bidPrice);
                    }
                }
            }
        }

        // Kiểm tra xem có dữ liệu hợp lệ hay không
        if (bestAsk.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) == 0 ||
                bestBid.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("No pricing data found for pair: " + pair);
            return;
        }

        // So sánh với giá hiện tại trong DB
        CryptoPrice existingPrice = cryptoPriceRepository.findTopByPairOrderByUpdatedAtDesc(pair);
        if (existingPrice != null &&
                existingPrice.getBestAskPrice().compareTo(bestAsk) == 0 &&
                existingPrice.getBestBidPrice().compareTo(bestBid) == 0) {
            // Nếu giá không thay đổi, bỏ qua
            return;
        }

        // Tạo đối tượng và lưu DB
        CryptoPrice cryptoPrice = CryptoPrice.builder()
                .pair(pair)
                .bestAskPrice(bestAsk)
                .bestBidPrice(bestBid)
                .updatedAt(LocalDateTime.now())
                .build();

        cryptoPriceRepository.save(cryptoPrice);
        System.out.println("Updated " + pair + ": Best Ask = " + bestAsk + ", Best Bid = " + bestBid);
    }


}