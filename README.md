![Bot](https://github.com/Space27/JavaBackendSpring/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/Space27/JavaBackendSpring/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker

### Автор: Локосов Даниил

Приложение для отслеживания обновлений контента по ссылкам (*GitHub* и *StackOverflow*).
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 21` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.

## Стек

* `Java 21`
* `Spring Boot 3`
* Взаимодействие сервисов - `WebClient` `HttpInterface`
* БД - `PostgreSQL`
* Миграции - `Liquibase`
* Очередь сообщений - `Kafka`
* Взаимодействие с БД - `JDBC`(через JdbcClient) / `jOOQ` / `JPA`
* `Docker`
* Тесты - `JUnit 5` `Mockito` `WireMock` `TestContainers`
* Метрики - `Prometheus` `Grafana`

## Команды бота

Список команд бота доступен через вкладку **Меню** в диалоге с ботом:
* `/help` - вывести список команд
* `/start` - регистрация пользователя
* `/end` - удаление пользователя
* `/track <ссылка>` - начать отслеживать ссылку
* `/untrack <ссылка>` - прекратить отслеживать ссылку
* `/list` - вывести список отслеживаемых ссылок

## Запуск проекта

### Прямой запуск
1. Склонировать содержимое репозитория
2. Конфигурационные файлы, в которых можно изменить модель Retry (fixed, linear, exponential), взаимодействие с БД (jdbc, jooq, jpa), а также использование очереди (true/false):
   * [Bot](bot/src/main/resources/application.yml) - необходимо установить **telegram-token**
   * [Scrapper](scrapper/src/main/resources/application.yml)
3. Собрать проект командой `mvn package -DskipTests` (ввести команду можно через терминал, открывающийся двойным нажатием *Ctrl* в IntelliJ IDEA)
4. Поднять `PostgreSQL` и накатить [миграции](migrations) (можно выполнить при помощи команды `docker compose up -d liquibase-migrations`)
5. Опционально поднять `Kafka` при помощи `docker compose up -d kafka1`
6. Опционально поднять `Grafana` при помощи `docker compose up -d grafana`
7. Запустить приложение [Scrapper](scrapper/src/main/java/edu/java/scrapper/ScrapperApplication.java), затем [Bot](bot/src/main/java/edu/java/bot/BotApplication.java)

### Запуск через Docker
Сервисы собраны в легковесные [Docker-образы](https://github.com/Space27?tab=packages&repo_name=JavaBackendSpring), поэтому их можно подтянуть с GitHub Packages и запустить без сборки.  
Для ручной сборки образов необходимо предварительно собрать проект командой `mvn package -DskipTests`.
1. Скопировать или скачать [compose.yml](compose.yml)
2. Скачать конфигурационные файлы [миграции](migrations) и [prometheus](prometheus.yml) или запустить `docker compose run -d --rm files`
3. Указать в [compose.yml](compose.yml) **TELEGRAM_API_KEY** для bot
4. Выполнить `docker compose up -d bot`
5. Выключить приложение `docker compose down`
