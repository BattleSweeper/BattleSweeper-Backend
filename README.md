<h2 align="center">BattleSweeper-Backend</h2>

[![Java CI with Gradle](https://github.com/BattleSweeper/BattleSweeper-Backend/actions/workflows/gradle.yml/badge.svg)](https://github.com/BattleSweeper/BattleSweeper-Backend/actions/workflows/gradle.yml)

#### Visit [Backend-Specs.md](./Backend-Specs.md) for details

#### How to run server:
1. Create .env file in project root directory
2. Fill the informations
##### Example: 
```env
EMAIL_ADDR=<email address>
EMAIL_PSWD=<email password>

DB_HOST=<host url for mariadb connection ex)jdbc:mariadb://localhost>
DB_PORT=<host port for mariadb connection>
DB_USERNAME=<mariadb username>
DB_PASSWORD=<mariadb passwd>

TOKEN_SECRET_KEY=<key for jwt generation>
```
3. Run `BattleSweeperBackendApplication#main` function 
