package hng.backend.exchangeAPI.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryUrlResponse {

    private String name;
    private String capital;
    private String region;
    private Long population;
    private List<Currency> currencies;
    private String flag;
    private String independent;

    @Getter
    @Setter
    public static class Currency {
        private String code;
        private String name;
        private String symbol;
    }
}
