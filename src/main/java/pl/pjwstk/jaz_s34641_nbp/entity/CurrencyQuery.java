package pl.pjwstk.jaz_s34641_nbp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_queries")
public class CurrencyQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "calculated_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal calculatedRate;

    @Column(name = "query_date_time", nullable = false)
    private LocalDateTime queryDateTime;

    protected CurrencyQuery() {
    }

    public CurrencyQuery(String currency,
                         LocalDate startDate,
                         LocalDate endDate,
                         BigDecimal calculatedRate,
                         LocalDateTime queryDateTime) {
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.calculatedRate = calculatedRate;
        this.queryDateTime = queryDateTime;
    }

    public Long getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getCalculatedRate() {
        return calculatedRate;
    }

    public LocalDateTime getQueryDateTime() {
        return queryDateTime;
    }
}