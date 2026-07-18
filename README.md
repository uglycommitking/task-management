# Task Management API

Pet-проект: REST API для управления задачами - создание, редактирование,
удаление и управление жизненным циклом задачи (статусы), с хранением в PostgreSQL.

## Стек

Java 21, Spring Boot 4, Spring Data JPA (Hibernate), PostgreSQL, Maven,
JUnit 5 / Mockito / Testcontainers, Swagger, Docker.

## Функциональность

Эндпоинты:

GET /tasks/{id} — получение задачи  
POST /tasks — создание задачи  
PUT /tasks/{id} — обновление задачи  
DELETE /tasks/{id} — удаление задачи  
POST /tasks/{id}/start — перевод задачи в статус "выполняется"  
POST /tasks/{id}/complete — перевод задачи в статус "выполнено"  
PATCH /tasks/{id}/reopen — возврат задачи из статуса "выполнено" в "выполняется"  

Правила:  
1) входные данные валидируются  
2) запрос несуществующей задачи возвращает 404
3) задача в статусе "выполнено" не может быть изменена - попытка вернёт 409.

## Запуск

Требования: Java 21, Maven, Docker.

### Вариант 1 - через Docker Compose

    mvn clean package
    docker compose up --build

Поднимает приложение и PostgreSQL одной командой.

### Вариант 2 - локальная разработка

1. Поднять только базу данных:

   docker compose up postgres-db

2. Запустить приложение из IDE или из корня проекта:

   mvn spring-boot:run

Без переменных окружения приложение подключается к localhost:5432.

Приложение доступно на http://localhost:8080,
Swagger UI — http://localhost:8080/swagger

## Тесты

Запуск: mvn verify

Покрытие: юнит-тесты сервиса (Mockito) и контроллера (@WebMvcTest + MockMvc),
интеграционные тесты полного цикла с PostgreSQL в Docker.

Примечание: на Docker Engine 29+ интеграционные тесты могут падать с ошибкой
"Could not find a valid Docker environment". Обход: добавить "min-api-version": "1.24"
в Docker Engine.
