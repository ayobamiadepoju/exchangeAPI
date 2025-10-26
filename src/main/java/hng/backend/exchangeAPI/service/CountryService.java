package hng.backend.exchangeAPI.service;

import hng.backend.exchangeAPI.dto.CountryUrlResponse;
import hng.backend.exchangeAPI.dto.ExchangeUrlResponse;
import hng.backend.exchangeAPI.dto.StatusResponse;
import hng.backend.exchangeAPI.model.Country;
import hng.backend.exchangeAPI.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class CountryService {

    @Value("${countries}")
    private String countryUrl;

    @Value("${exchange.rates}")
    private String exchangeUrl;

    private final CountryRepository countryRepository;

    private final RestTemplate restTemplate;

    private final Random random = new Random();

    public CountryService(CountryRepository countryRepository, RestTemplate restTemplate) {
        this.countryRepository = countryRepository;
        this.restTemplate = restTemplate;
    }

    private LocalDateTime lastRefreshTimestamp;

    public void fetchAndSaveCountryData() {
        try {
            CountryUrlResponse[] countries = restTemplate.getForObject(countryUrl, CountryUrlResponse[].class);
            ExchangeUrlResponse exchangeRates = restTemplate.getForObject(exchangeUrl, ExchangeUrlResponse.class);

            if (countries == null || exchangeRates == null) {
                throw new IllegalStateException("Failed to fetch data from one or both APIs.");
            }

            LocalDateTime timestamp = LocalDateTime.now();

            for (CountryUrlResponse countryData : countries) {
                Country existing = countryRepository.findByName(countryData.getName()).orElse(null);
                Country country = mapToCountryEntity(countryData, exchangeRates, timestamp);

                if (existing != null) {
                    country.setId(existing.getId()); // update instead of insert
                }

                countryRepository.save(country);
            }
            this.lastRefreshTimestamp = timestamp;
        } catch (RestClientException e) {
            throw new RuntimeException("External data source unavailable: " + e.getMessage(), e);
        }
    }


    public List<Country> getCountries(String region, String currency, String name, String sort) {
        Sort sortOrder = getSortOrder(sort);

        if (region != null && currency != null) {
            return countryRepository.findByRegionAndCurrencyCode(region, currency, sortOrder);
        } else if (region != null) {
            return countryRepository.findByRegion(region, sortOrder);
        } else if (currency != null) {
            return countryRepository.findByCurrencyCode(currency, sortOrder);
        }else {
            return countryRepository.findAll(sortOrder);
        }
    }

    private Sort getSortOrder(String sort) {
        switch (sort.toLowerCase()) {
            case "gdp_desc":
                return Sort.by(Sort.Direction.DESC, "estimatedGdp");
            case "gdp_asc":
                return Sort.by(Sort.Direction.ASC, "estimatedGdp");
            case "population_desc":
                return Sort.by(Sort.Direction.DESC, "population");
            case "population_asc":
                return Sort.by(Sort.Direction.ASC, "population");
            case "name_desc":
                return Sort.by(Sort.Direction.DESC, "name");
            default:
                return Sort.by(Sort.Direction.ASC, "name");
        }
    }


    //HELPER METHODS
    private Country mapToCountryEntity(CountryUrlResponse countryData,
                                       ExchangeUrlResponse exchangeRates,
                                       LocalDateTime timestamp){
        Country country = new Country();
        country.setName(countryData.getName());
        country.setCapital(countryData.getCapital());
        country.setRegion(countryData.getRegion());
        country.setPopulation(countryData.getPopulation());
        country.setFlagUrl(countryData.getFlag());
        country.setLastRefreshedAt(timestamp);

        if (countryData.getCurrencies() != null && !countryData.getCurrencies().isEmpty()){
            String currencyCode = countryData.getCurrencies().getFirst().getCode().toUpperCase();
            country.setCurrencyCode(currencyCode);

            Map<String, Double> rates = exchangeRates.getRates();
            if (rates.containsKey(currencyCode)){
                Double exchangeRate = rates.get(currencyCode);
                country.setExchangeRate(exchangeRate);

                double randomMultiplier = 1000 + (random.nextDouble() * 1000);
                double estimatedGdp = (countryData.getPopulation() * randomMultiplier) / exchangeRate;
                country.setEstimatedGdp(estimatedGdp);
            } else {
                country.setExchangeRate(null);
                country.setEstimatedGdp(null);
            }
        } else {
            country.setCurrencyCode(null);
            country.setExchangeRate(null);
            country.setEstimatedGdp(0.0);
        }
        return country;
    }

    public Country getCountryByName(String name) {
        return countryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Country not found: " + name));

    }

    public void deleteCountryByName(String name) {
        Country country = countryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Country not found: " + name));
        countryRepository.delete(country);
    }

    public StatusResponse getStatus() {
        long totalCountries = countryRepository.count();
        LocalDateTime lastRefreshed = countryRepository.findLatestRefreshTime()
                .orElse(null);

        return new StatusResponse(totalCountries, lastRefreshed);
    }

    public File generateAndSaveSummaryImage() {
        if (lastRefreshTimestamp == null) {
            lastRefreshTimestamp = countryRepository.findLatestRefreshTime().orElse(LocalDateTime.now());
        }

        long totalCountries = countryRepository.count();
        List<Country> top5 = countryRepository.findTop5ByOrderByEstimatedGdpDesc();

        int width = 600;
        int height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("🌍 Country Summary", 20, 40);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Total countries: " + totalCountries, 20, 80);
        g.drawString("Last refreshed: " + lastRefreshTimestamp.toString(), 20, 110);

        g.drawString("Top 5 by GDP:", 20, 150);
        int y = 180;
        for (Country c : top5) {
            g.drawString(c.getName() + " — " + String.format("%.2f", c.getEstimatedGdp()), 40, y);
            y += 25;
        }

        g.dispose();

        File dir = new File("cache");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "summary.png");
        try {
            ImageIO.write(image, "png", new File("cache/summary.png"));
            System.out.println("Saving summary image at: " + new File("cache/summary.png").getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save summary image: " + e.getMessage());
        }

        return file;
    }
}