package hng.backend.exchangeAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeUrlResponse {


    private String result;
    private String provider;
    private String documentation;
    private String termsOfUse;
    private Integer timeLastUpdateUnix;
    private String timeLastUpdateUtc;
    private String timeNextUpdateUnix;
    private String getTimeNextUpdateUtc;
    private String timeEolUnix;
    private String baseCode;
    private Map<String, Double> rates;
}
