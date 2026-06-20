package pl.pjwstk.jaz_s34641_nbp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NbpRate(
        String no,
        LocalDate effectiveDate,
        BigDecimal mid
) {
}