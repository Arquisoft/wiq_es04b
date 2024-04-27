# wiq_es04b

[![Deploy on release](https://github.com/Arquisoft/wiq_es04b/actions/workflows/release.yml/badge.svg)](https://github.com/Arquisoft/wiq_es04b/actions/workflows/release.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Arquisoft_wiq_es04b&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Arquisoft_wiq_es04b)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Arquisoft_wiq_es04b&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Arquisoft_wiq_es04b)

### 🚀 TEAM

- **Pelayo Rojas Iñigo**
- **Álvaro Arias Martínez De Vega**
- **Ricardo Díaz Núñez**
- **Roberto Peña Goy**
- **Iker Álvarez Fernández**

### Local deployment instructions

1. Fist you have to clone the repository using a CMD and the following command: `git clone https://github.com/Arquisoft/wiq_es04b.git` or using an IDE with Git integration or any other app of your preference.

2. Then you have to execute the [runServer.bat](https://github.com/Arquisoft/wiq_es04b/blob/master/database/hsqldb/bin/runServer.bat) to start the local database.

3. With the database initialized you have to open a CMD in the project root directory and execute the following command `mvnw spring-boot:run`, to start the application.

4. When the application is started the web app uses the port 3000. You can access the app through any web client using the following URL: http://localhost:3000/.

5. If you wish to execute the tests you have to open a CMD in the project root directory (you could use the same you used before), you have to execute `set EXCLUDE_JUNIT=true` if you also want to execute the E2E tests. Then to execute the tests you have to use the following command: `mvnw org.jacoco:jacoco-maven-plugin:prepare-agent verify`.

6. If you want to obtain the report you have to torn off the app and in the same CMD as before execute the following command: `mvnw org.jacoco:jacoco-maven-plugin:report`.