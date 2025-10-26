package hng.backend.exchangeAPI.model;

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
    private String currencyCode;
    private Double exchangeRate;
    private Double estimatedGdp;
    private String flagUrl;

    private LocalDateTime lastRefreshedAt;

}