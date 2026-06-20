package pl.pjwstk.jaz_s34641_nbp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AverageRateResponse(
        Long id,
        String currency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal averageRate,
        Integer ratesCount,
        LocalDateTime queryDateTime
) {
}