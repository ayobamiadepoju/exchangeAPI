package hng.backend.exchangeAPI.controller;

import hng.backend.exchangeAPI.dto.StatusResponse;
import hng.backend.exchangeAPI.model.Country;
import hng.backend.exchangeAPI.service.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class exchangeRatesController {

    @Autowired
    CountryService countryService;

    @PostMapping("/countries/refresh")
    public ResponseEntity<?> refreshCountries(){
        try{
            countryService.fetchAndSaveCountryData();
            System.out.println("Finished refreshing Country Data");
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountries(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false, defaultValue = "name_asc") String sort
    ){
        try {
            List<Country> countries = countryService.getCountries(region, currency, sort);
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid query parameters"));
        }}

    @GetMapping("/countries/{name}")
    public ResponseEntity<Country> getCountryByName(@PathVariable String name) {
        Country country = countryService.getCountryByName(name);
        return ResponseEntity.ok(country);
    }

    @DeleteMapping("/countries/{name}")
    public ResponseEntity.HeadersBuilder<?> deleteCountryByName(@PathVariable String name) {
        countryService.deleteCountryByName(name);
        return ResponseEntity.noContent();
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        return ResponseEntity.ok(countryService.getStatus());
    }

    @GetMapping("/countries/image")
    public ResponseEntity<byte[]> getSummaryImage() {
        try {
            log.info("Generating summary image...");
            byte[] imageBytes = countryService.generateAndSaveSummaryImage();
             return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(imageBytes);
        } catch (Exception e) {
            log.error("Failed to generate summary image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}