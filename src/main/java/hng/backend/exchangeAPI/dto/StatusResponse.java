package hng.backend.exchangeAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {

    @JsonProperty("total_countries")
    private Long totalCountries;

    @JsonProperty("last_refreshed_at")
    private LocalDateTime lastRefreshedAt;
}
