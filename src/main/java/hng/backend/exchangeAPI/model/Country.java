package hng.backend.exchangeAPI.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String capital;
    private String region;
    private Long population;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("exchange_rate")
    private Double exchangeRate;

    @JsonProperty("estimated_gdp")
    private Double estimatedGdp;

    @JsonProperty("flag_url")
    private String flagUrl;

    @JsonProperty("last_refreshed_at")
    private LocalDateTime lastRefreshedAt;

}