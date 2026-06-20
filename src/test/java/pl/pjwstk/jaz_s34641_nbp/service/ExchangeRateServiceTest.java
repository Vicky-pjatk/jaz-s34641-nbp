package pl.pjwstk.jaz_s34641_nbp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import pl.pjwstk.jaz_s34641_nbp.dto.AverageRateResponse;
import pl.pjwstk.jaz_s34641_nbp.dto.NbpRate;
import pl.pjwstk.jaz_s34641_nbp.dto.NbpRatesResponse;
import pl.pjwstk.jaz_s34641_nbp.entity.CurrencyQuery;
import pl.pjwstk.jaz_s34641_nbp.exception.InvalidRequestException;
import pl.pjwstk.jaz_s34641_nbp.repository.CurrencyQueryRepository;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyQueryRepository currencyQueryRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void shouldCalculateAverageRate() {
        // given
        String currency = "EUR";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);

        NbpRatesResponse nbpResponse = new NbpRatesResponse(
                "A",
                "euro",
                "EUR",
                List.of(
                        new NbpRate("001/A/NBP/2024", LocalDate.of(2024, 1, 1), new BigDecimal("4.2000")),
                        new NbpRate("002/A/NBP/2024", LocalDate.of(2024, 1, 2), new BigDecimal("4.3000"))
                )
        );

        when(restTemplate.getForObject(any(URI.class), eq(NbpRatesResponse.class)))
                .thenReturn(nbpResponse);

        when(currencyQueryRepository.save(any(CurrencyQuery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        AverageRateResponse result = exchangeRateService.calculateAverageRate(currency, startDate, endDate);

        // then
        assertEquals("EUR", result.currency());
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
        assertEquals(new BigDecimal("4.2500"), result.averageRate());
        assertEquals(2, result.ratesCount());
    }

    @Test
    void shouldThrowExceptionForPlnCurrency() {
        // given
        String currency = "PLN";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // when + then
        assertThrows(
                InvalidRequestException.class,
                () -> exchangeRateService.calculateAverageRate(currency, startDate, endDate)
        );
    }
}