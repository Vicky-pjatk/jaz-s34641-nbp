package pl.pjwstk.jaz_s34641_nbp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pjwstk.jaz_s34641_nbp.dto.AverageRateResponse;
import pl.pjwstk.jaz_s34641_nbp.service.ExchangeRateService;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/rates")
@Tag(name = "NBP", description = "Obliczanie średniego kursu waluty z API NBP")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/average")
    @Operation(
            summary = "Oblicza średni kurs waluty",
            description = "Pobiera dane z API NBP, oblicza średni kurs waluty dla podanego zakresu dat i zapisuje zapytanie w bazie danych."
    )
    @ApiResponse(responseCode = "200", description = "Średni kurs obliczony poprawnie")
    @ApiResponse(responseCode = "400", description = "Nieprawidłowe parametry zapytania")
    @ApiResponse(responseCode = "404", description = "Brak danych w API NBP")
    @ApiResponse(responseCode = "503", description = "API NBP niedostępne")
    public AverageRateResponse getAverageRate(
            @Parameter(description = "Kod waluty, np. EUR, USD, CHF", example = "EUR")
            @RequestParam
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Kod waluty musi mieć dokładnie 3 litery")
            String currency,

            @Parameter(description = "Data początkowa w formacie RRRR-MM-DD", example = "2024-01-01")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Data końcowa w formacie RRRR-MM-DD", example = "2024-01-31")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return exchangeRateService.calculateAverageRate(currency, startDate, endDate);
    }
}