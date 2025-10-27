package hng.backend.exchangeAPI.repository;

import hng.backend.exchangeAPI.model.Country;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findByRegionIgnoreCase(String region, Sort sort);
    List<Country> findByCurrencyCodeIgnoreCase(String currencyCode, Sort sort);
    List<Country> findByRegionIgnoreCaseAndCurrencyCodeIgnoreCase(String region, String currencyCode, Sort sort);

    @Query("SELECT c FROM Country c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Country> findByName(@Param("name") String name);

    List<Country> findTop5ByOrderByEstimatedGdpDesc();

    @Query("SELECT MAX(c.lastRefreshedAt) FROM Country c")
    Optional<LocalDateTime> findLatestRefreshTime();
}