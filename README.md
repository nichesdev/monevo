# Monevo

**Multi-Currency Wallet & Portfolio Platform**

Monevo é uma plataforma de carteira digital multi-moeda desenvolvida em Java e Spring Boot, criada para simular a compra, venda e conversão de moedas utilizando cotações de mercado obtidas de APIs externas.

O projeto tem como foco explorar **arquitetura orientada a eventos, processamento assíncrono, consistência financeira, concorrência e observabilidade**, indo além do modelo tradicional de CRUD.

## Funcionalidades

- Carteira virtual com suporte a múltiplas moedas
- Depósitos e saldo em BRL
- Compra e venda de moedas
- Conversão entre diferentes moedas
- Cotação de moedas obtida por API externa
- Atualização periódica das cotações
- Histórico de operações
- Portfolio com valorização dos ativos
- Ordens de mercado
- Ordens limitadas
- Ledger financeiro
- Idempotência de operações
- Processamento assíncrono
- Notificações de transações
- Histórico de cotações

## Arquitetura

```text
                       External FX API
                             │
                             ▼
                    Market Data Service
                             │
                           Kafka
                             │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
     Trading Service   Portfolio Service   Analytics
          │
          ▼
     Wallet / Ledger
          │
          ▼
      PostgreSQL

      Redis
        ├── Exchange Rates
        ├── Cache
        ├── Idempotency
        └── Distributed Locks

      RabbitMQ
          │
          ▼
   Notification Workers
```

## Fluxo de uma compra

```text
User
 │
 ▼
Create Order
 │
 ▼
Trading Service
 │
 ▼
Current Exchange Rate
 │
 ▼
Validate Balance
 │
 ▼
Process Transaction
 │
 ├── Debit BRL
 └── Credit Currency
 │
 ▼
Create Ledger Entries
 │
 ▼
Publish Event
 │
 ▼
Update Portfolio
```

## Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA

### Messaging

- Apache Kafka
- RabbitMQ

### Data

- PostgreSQL
- Redis

### Infrastructure

- Docker
- Docker Compose

### Observability

- OpenTelemetry
- Prometheus
- Grafana

### Testing

- JUnit 5
- Mockito
- Testcontainers
- k6

### CI/CD

- GitHub Actions

## Conceitos explorados

O Monevo foi projetado para demonstrar conceitos comuns em sistemas financeiros e distribuídos:

- Event-Driven Architecture
- Idempotency
- Concurrency Control
- Distributed Locking
- Transaction Management
- Financial Ledger
- Eventual Consistency
- Retry & Failure Handling
- Caching
- Asynchronous Processing
- Observability
- Load Testing

## Exemplo

Uma carteira pode possuir:

```text
BRL     R$ 4.580,00
USD     $1.000,00
EUR       €450,00
GBP       £100,00
```

Caso a cotação seja:

```text
USD/BRL = 5,42
```

uma compra de `US$ 500` representa:

```text
500 × 5,42 = R$ 2.710,00
```

A operação gera registros no ledger e eventos que podem ser processados pelos demais componentes da plataforma.

## Status

> Projeto em desenvolvimento.

Novos componentes e funcionalidades serão adicionados conforme a evolução da arquitetura.

## Objetivo

O Monevo foi desenvolvido como um projeto de estudo e portfólio com foco em **engenharia de software backend**, buscando reproduzir desafios encontrados em aplicações distribuídas que lidam com transações, processamento assíncrono e dados financeiros.
