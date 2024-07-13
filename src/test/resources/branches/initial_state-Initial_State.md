# Exercice commencé
Votre note est de **4.64**/9.

## Détail
* Compilation & Tests: 1/1
* Code Coverage: 3.64/4
    * Code coverage: 72.73%, expected: > 85.0% with `mvn verify`

* Part 1 - Cat program: 0/4
    * In the case of no argument expecting exit code to be `3` but was `1` and expecting output to be `Missing argument` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`
    * In the case of 2 arguments expecting exit code to be `4` but was `1` and expecting output to be `Too many arguments` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`
    * In the case of a non-existing file expecting exit code to be `5` but was `1` and expecting output to be `File not found` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`
    * In the case of a directory expecting exit code to be `6` but was `1` and expecting output to be `A file is required` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`
    * In the case of a large file expecting exit code to be `7` but was `1` and expecting output to be `File too large` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`
    * In the case of a normal file expecting exit code to be `0` but was `1` and expecting output to be `never too cherries \n ice sundae elephant \n talk the in \n When asked favori...` but was `Error: Could not find or load main class fr.lernejo.file.Cat\nCaused by: java...`

*Analyse effectuée à 1970-01-01T00:00:00Z.*
