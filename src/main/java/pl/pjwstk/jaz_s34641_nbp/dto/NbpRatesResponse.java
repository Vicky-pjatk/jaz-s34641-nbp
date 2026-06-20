package pl.pjwstk.jaz_s34641_nbp.dto;

import java.util.List;

public record NbpRatesResponse(
        String table,
        String currency,
        String code,
        List<NbpRate> rates
) {
}