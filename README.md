# korekto-load-file-grader
[![Build](https://github.com/lernejo/korekto-load-file-grader/actions/workflows/ci.yml/badge.svg)](https://github.com/lernejo/korekto-load-file-grader/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/lernejo/korekto-load-file-grader/branch/main/graph/badge.svg?token=A6kYtPT5DX)](https://codecov.io/gh/lernejo/korekto-load-file-grader)
![License](https://img.shields.io/badge/License-Elastic_License_v2-blue)

Korekto grader & exercise about loading files in Java.

Exercise subject: [here](EXERCISE_fr.adoc)

# How to launch
You will need these 2 env vars:
* `GH_LOGIN` your GitHub login
* `GH_TOKEN` a [**P**ersonal **A**ccess **T**oken](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic) with permissions `repo:read` and `user:read`

```bash
git clone git@github.com:lernejo/korekto-load-file-grader.git
mvn compile exec:java -Dexec.args="-s=$GH_LOGIN" -Dgithub_token="$GH_TOKEN"
```

## With IntelliJ

After cloning the project, open it in with IntelliJ.

Then create a new **Application** configuration
* launching the class `com.github.lernejo.korekto.toolkit.launcher.GradingJobLauncher`
* with program arguments: `-s=$GH_LOGIN`
* with VM options: `-Dgithub_token="$GH_TOKEN`
