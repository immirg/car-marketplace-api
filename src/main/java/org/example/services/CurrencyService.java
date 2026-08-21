package org.example.services;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    public Map<String, Double> convert() {
        Map<String, Double> course = new HashMap<>();
        double eurBuy = 0;
        double eurSale = 0;
        double usdBuy = 0;
        double usdSale = 0;
        try {
            String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PrivatBank>>() {}.getType();
            List<PrivatBank> bank = gson.fromJson(body, listType);

            for (PrivatBank currency: bank) {
                if ("EUR".equals(currency.getCcy())) {
                    eurBuy = currency.getBuy();
                    eurSale = currency.getSale();
                }
                if ("USD".equals(currency.getCcy())) {
                    usdBuy = currency.getBuy();
                    usdSale = currency.getSale();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("There is no exchange rate for the currency");
        }
        course.put("eurBuy", eurBuy);
        course.put("eurSale", eurSale);
        course.put("usdBuy", usdBuy);
        course.put("usdSale", usdSale);
        return course;
    }

    @Data
    @NoArgsConstructor
    private class PrivatBank {
        String ccy;
        String base_ccy;
        Double buy;
        Double sale;
    }
}
