# Exercice commencé
Votre note est de **8.81**/23.

## Détail
* Compilation & Tests: 1/1
* Code Coverage: 3.81/4
    * Code coverage: 76.19%, expected: > 85.0% with `mvn verify`

* Part 1 - Cat program: 4/4
* Part 2 - CSV reader: 0/6
    * In the case of (period undisclosed)/wind_speed_10m/NIGHT/AVG, expecting exit code to be `0` but was `1` and expecting output to be `12.238435716683451 km/h` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`
    * In the case of (period undisclosed)/temperature_2m/NIGHT/MIN, expecting exit code to be `0` but was `1` and expecting output to be `3.4 °C` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`
    * In the case of (period undisclosed)/temperature_2m/NIGHT/MIN, expecting exit code to be `0` but was `1` and expecting output to be `-16.4 °C` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`
    * In the case of (period undisclosed)/wind_speed_10m/NIGHT/SUM, expecting exit code to be `0` but was `1` and expecting output to be `180752.1 km/h` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`

* Part 3 - streaming: 0/8
    * In the case of (period undisclosed)/wind_speed_10m/NIGHT/MIN, expecting exit code to be `0` but was `1` and expecting output to be `0.4 km/h` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`
    * In the case of (period undisclosed)/pressure_msl/NIGHT/AVG, expecting exit code to be `0` but was `1` and expecting output to be `1015.2184951513027 hPa` but was `Error: Could not find or load main class fr.lernejo.file.CsvReader\nCaused by...`



*Analyse effectuée à 1970-01-01T00:00:00Z.*
