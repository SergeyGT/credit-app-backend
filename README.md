# Credit Application Backend API

Backend-сервис для оформления кредитных заявок, управления клиентами и договорами.

## Описание

Система позволяет:
- Создавать и искать клиентов
- Оформлять кредитные заявки
- Подписывать договоры
- Получать списки заявок и договоров с фильтрацией и пагинацией

## Стек технологий

> *Укажите актуальный стек проекта*

- **Backend**: Java 17+ / Spring Boot 3.x
- **База данных**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Сборка**: Maven 
- **Контейнеризация**: Docker

## API Endpoints

Базовый URL: `/api`

---

### Клиенты (`/api/clients`)

| Метод | URL | Описание | Тело запроса |
|-------|-----|----------|--------------|
| `POST` | `/api/clients` | Создание нового клиента | `ClientDto` |
| `GET` | `/api/clients?page=0` | Список всех клиентов (пагинация) | — |
| `GET` | `/api/clients/{id}` | Получение клиента по ID | — |
| `GET` | `/api/clients/find` | Поиск клиента по телефону, ФИО, паспорту | — |

#### Параметры запроса `/api/clients/find`

| Параметр | Тип | Описание |
|----------|-----|----------|
| `page` | `int` | Номер страницы (по умолчанию `0`) |
| `phone` | `string` | Поиск по телефону (необязательно) |
| `passport` | `string` | Поиск по паспорту (необязательно) |
| `firstName` | `string` | Поиск по имени (необязательно) |
| `lastName` | `string` | Поиск по фамилии (необязательно) |
| `middleName` | `string` | Поиск по отчеству (необязательно) |

---

### Заявки (`/api/applications`)

| Метод | URL | Описание | Тело запроса |
|-------|-----|----------|--------------|
| `POST` | `/api/applications` | Оформление заявки на кредит | `CreateCreditApplicationRequestDto` |
| `PATCH` | `/api/applications/{id}/sign` | Подписание договора по ID заявки | — |
| `GET` | `/api/applications?page=0` | Список всех заявок (пагинация) | — |
| `GET` | `/api/applications/approved?page=0` | Список одобренных заявок | — |
| `GET` | `/api/applications/{id}` | Получение заявки по ID | — |
| `GET` | `/api/applications/status` | Фильтр заявок по статусу | — |

#### Параметры запроса `/api/applications/status`

| Параметр | Тип | Описание |
|----------|-----|----------|
| `status` | `string` | `PENDING`, `APPROVED`, `REJECTED`, `SIGNED` |
| `page` | `int` | Номер страницы (по умолчанию `0`) |

---

### Договоры (`/api/agreements`)

| Метод | URL | Описание | Тело запроса |
|-------|-----|----------|--------------|
| `GET` | `/api/agreements?page=0` | Список всех договоров (пагинация) | — |
| `GET` | `/api/agreements/signed?page=0` | Список подписанных договоров | — |
| `GET` | `/api/agreements/{id}` | Получение договора по ID | — |
| `GET` | `/api/agreements/status` | Фильтр договоров по статусу подписи | — |

#### Параметры запроса `/api/agreements/status`

| Параметр | Тип | Описание |
|----------|-----|----------|
| `status` | `string` | `SIGNED`, `NOT_SIGNED`, `PENDING` |
| `page` | `int` | Номер страницы (по умолчанию `0`) |

---

## Структура DTO

### `ClientDto` (POST `/api/clients`)

```json
{
  "firstName": "Иван",
  "lastName": "Иванов",
  "middleName": "Иванович",
  "passport": "1234567890",
  "gender": "MALE",
  "familyStatus": "MARRIED",
  "residenceAddress": "г. Москва, ул. Ленина, д. 1",
  "registrationAddress": "г. Москва, ул. Ленина, д. 1",
  "phone": "+79998887766",
  "employmentStartDate": "2020-01-01",
  "employmentEndDate": null,
  "employmentPosition": "Программист",
  "organizationName": "ООО Техно",
  "loanPurpose": "Покупка квартиры"
}
