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
import java.util.NoSuchElementException;

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
    ) {
        try {
            List<Country> countries = countryService.getCountries(region, currency, sort);

            if (countries == null || countries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "No countries found for the specified filters"
                        ));
            }

            if (!isValidRegion(region) && region != null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Invalid region parameter: " + region
                        ));
            }

            if (!isValidSort(sort)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", "error",
                                "message", "Invalid sort parameter"
                        ));
            }
            return ResponseEntity.ok(countries);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "An error occurred while processing your request"
                    ));
        }
    }


    @GetMapping("/countries/{name}")
    public ResponseEntity<Country> getCountryByName(@PathVariable String name) {
        Country country = countryService.getCountryByName(name);
        return ResponseEntity.ok(country);
    }

    @DeleteMapping("/countries/{name}")
    public ResponseEntity<?> deleteCountryByName(@PathVariable String name) {
        try {
            countryService.deleteCountryByName(name);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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


    private boolean isValidRegion(String region) {
        if (region == null) return true;
        List<String> validRegions = List.of(
                "Africa", "Americas", "Asia", "Europe", "Oceania", "Antarctic"
        );
        return validRegions.stream()
                .anyMatch(r -> r.equalsIgnoreCase(region));
    }

    private boolean isValidSort(String sort) {
        return sort != null && (
                sort.equalsIgnoreCase("name_asc") ||
                        sort.equalsIgnoreCase("name_desc") ||
                        sort.equalsIgnoreCase("gdp_asc") ||
                        sort.equalsIgnoreCase("gdp_desc")
        );
    }

}