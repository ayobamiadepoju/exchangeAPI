package hng.backend.exchangeAPI.controller;

import hng.backend.exchangeAPI.dto.StatusResponse;
import hng.backend.exchangeAPI.model.Country;
import hng.backend.exchangeAPI.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/countries")
public class exchangeRatesController {

    @Autowired
    CountryService countryService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshCountries(){
        try{
            countryService.fetchAndSaveCountryData();
            System.out.println("Finished countryService.fetchAndSaveCountryData()");
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCountries(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "name_asc") String sort
    ){
        try {
            List<Country> countries = countryService.getCountries(region, currency, name, sort);
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid query parameters"));
        }}

    @GetMapping("/{name}")
    public ResponseEntity<Country> getCountryByName(@PathVariable String name) {
        Country country = countryService.getCountryByName(name);
        return ResponseEntity.ok(country);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCountryByName(@PathVariable String name) {
        countryService.deleteCountryByName(name);
        return ResponseEntity.ok("Country '" + name + "' deleted successfully.");
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        return ResponseEntity.ok(countryService.getStatus());
    }

    @GetMapping("/image")
    public ResponseEntity<?> getSummaryImage() {
        try {

            File imageFile = countryService.generateAndSaveSummaryImage();
            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to read summary image"));
        }
    }



}
