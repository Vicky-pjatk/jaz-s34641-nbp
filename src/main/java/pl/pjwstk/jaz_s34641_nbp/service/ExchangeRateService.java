package pl.pjwstk.jaz_s34641_nbp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.pjwstk.jaz_s34641_nbp.dto.AverageRateResponse;
import pl.pjwstk.jaz_s34641_nbp.dto.NbpRate;
import pl.pjwstk.jaz_s34641_nbp.dto.NbpRatesResponse;
import pl.pjwstk.jaz_s34641_nbp.entity.CurrencyQuery;
import pl.pjwstk.jaz_s34641_nbp.exception.InvalidRequestException;
import pl.pjwstk.jaz_s34641_nbp.exception.NbpNoDataException;
import pl.pjwstk.jaz_s34641_nbp.exception.NbpServiceUnavailableException;
import pl.pjwstk.jaz_s34641_nbp.repository.CurrencyQueryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeRateService {


    private static final int MAX_DAYS_PER_NBP_REQUEST = 90;


    private static final String NBP_URL =
            "http://api.nbp.pl/api/exchangerates/rates/a/{currency}/{startDate}/{endDate}/";

    private final RestTemplate restTemplate;
    private final CurrencyQueryRepository currencyQueryRepository;

    public ExchangeRateService(RestTemplate restTemplate,
                               CurrencyQueryRepository currencyQueryRepository) {
        this.restTemplate = restTemplate;
        this.currencyQueryRepository = currencyQueryRepository;
    }

    public AverageRateResponse calculateAverageRate(String currency,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {
        validateInput(currency, startDate, endDate);

        String currencyCode = currency.toUpperCase();

        List<NbpRate> allRates = getRatesFromNbp(currencyCode, startDate, endDate);

        if (allRates.isEmpty()) {
            throw new NbpNoDataException(
                    "Brak danych NBP dla waluty " + currencyCode +
                            " w zakresie od " + startDate + " do " + endDate + "."
            );
        }

        BigDecimal sum = allRates.stream()
                .map(NbpRate::mid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRate = sum.divide(
                BigDecimal.valueOf(allRates.size()),
                4,
                RoundingMode.HALF_UP
        );

        CurrencyQuery savedQuery = currencyQueryRepository.save(new CurrencyQuery(
                currencyCode,
                startDate,
                endDate,
                averageRate,
                LocalDateTime.now()
        ));

        return new AverageRateResponse(
                savedQuery.getId(),
                savedQuery.getCurrency(),
                savedQuery.getStartDate(),
                savedQuery.getEndDate(),
                savedQuery.getCalculatedRate(),
                allRates.size(),
                savedQuery.getQueryDateTime()
        );
    }

    private void validateInput(String currency, LocalDate startDate, LocalDate endDate) {
        if (currency == null || !currency.matches("^[A-Za-z]{3}$")) {
            throw new InvalidRequestException("Kod waluty musi mieć dokładnie 3 litery, np. EUR, USD, CHF.");
        }

        if (currency.equalsIgnoreCase("PLN")) {
            throw new InvalidRequestException("Nie podawaj PLN. API NBP zwraca kursy walut obcych względem PLN.");
        }

        if (startDate == null) {
            throw new InvalidRequestException("Parametr startDate jest wymagany.");
        }

        if (endDate == null) {
            throw new InvalidRequestException("Parametr endDate jest wymagany.");
        }

        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("Data endDate nie może być wcześniejsza niż startDate.");
        }

        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Data endDate nie może być z przyszłości.");
        }
    }

    private List<NbpRate> getRatesFromNbp(String currencyCode,
                                          LocalDate startDate,
                                          LocalDate endDate) {
        List<NbpRate> result = new ArrayList<>();

        LocalDate currentStartDate = startDate;

        while (!currentStartDate.isAfter(endDate)) {
            LocalDate currentEndDate = currentStartDate.plusDays(MAX_DAYS_PER_NBP_REQUEST - 1);

            if (currentEndDate.isAfter(endDate)) {
                currentEndDate = endDate;
            }

            URI uri = buildNbpUri(currencyCode, currentStartDate, currentEndDate);

            List<NbpRate> ratesFromPart = fetchSinglePartFromNbp(
                    uri,
                    currencyCode,
                    currentStartDate,
                    currentEndDate
            );

            result.addAll(ratesFromPart);

            currentStartDate = currentEndDate.plusDays(1);
        }

        return result;
    }

    private URI buildNbpUri(String currencyCode, LocalDate startDate, LocalDate endDate) {
        return UriComponentsBuilder
                .fromUriString(NBP_URL)
                .queryParam("format", "json")
                .buildAndExpand(currencyCode, startDate, endDate)
                .toUri();
    }

    private List<NbpRate> fetchSinglePartFromNbp(URI uri,
                                                 String currencyCode,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        try {
            NbpRatesResponse response = restTemplate.getForObject(uri, NbpRatesResponse.class);

            if (response == null || response.rates() == null) {
                return List.of();
            }

            return response.rates();

        } catch (HttpClientErrorException.NotFound exception) {

            return List.of();

        } catch (HttpClientErrorException.BadRequest exception) {
            throw new InvalidRequestException(
                    "NBP odrzuciło zapytanie. Sprawdź walutę i daty: "
                            + currencyCode + ", " + startDate + " - " + endDate + "."
            );

        } catch (HttpClientErrorException exception) {
            throw new InvalidRequestException(
                    "NBP zwróciło błąd klienta: " + exception.getStatusCode().value() + "."
            );

        } catch (HttpServerErrorException exception) {
            throw new NbpServiceUnavailableException(
                    "NBP zwróciło błąd serwera: " + exception.getStatusCode().value() + "."
            );

        } catch (RestClientException exception) {
            throw new NbpServiceUnavailableException(
                    "Nie udało się połączyć z API NBP. Szczegóły: " + exception.getMessage()
            );
        }
    }
}