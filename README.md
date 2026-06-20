# jaz-s34641-nbp


Aplikacja Spring Boot udostępnia REST API, które oblicza średni kurs wybranej waluty na podstawie danych z API NBP dla podanego zakresu dat. Każde wykonane zapytanie jest zapisywane w bazie danych H2.

## Technologie

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Swagger / OpenAPI
- Maven

## Uruchomienie projektu

W głównym katalogu projektu należy uruchomić:

mvn spring-boot:run

Aplikacja działa pod adresem:

http://localhost:8080

## Endpoint REST API

Aplikacja udostępnia jeden endpoint GET:

GET /api/rates/average

Przykładowe zapytanie:

http://localhost:8080/api/rates/average?currency=EUR&startDate=2024-01-01&endDate=2024-01-31

Przykładowa odpowiedź:

{
  "id": 1,
  "currency": "EUR",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "averageRate": 4.3651,
  "ratesCount": 22,
  "queryDateTime": "2026-06-20T10:56:07.0760676"
}

## Swagger

Dokumentacja API jest dostępna pod adresem:

http://localhost:8080/swagger-ui.html

## Baza danych H2

Konsola H2 jest dostępna pod adresem:

http://localhost:8080/h2-console

Dane logowania:

JDBC URL: jdbc:h2:file:./data/nbp-db  
User Name: sa  
Password:  

Tabela zapisująca zapytania:

currency_queries

Kolumny tabeli:

- id
- currency
- start_date
- end_date
- calculated_rate
- query_date_time

## Testy

Testy można uruchomić poleceniem:

mvn test

## Autor

Viktoriia Melnychuk  
s34641
