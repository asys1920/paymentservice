# Payment Service
## Table of Contents

- [Description](#description)
- [Documentation](#documentation)
- [Features](#features)
- [Requirements](#requirements)
- [Quick Start / Setup](#quick-start--setup)
- [API](#api)

## Description
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/asys1920/paymentservice)](https://github.com/asys1920/paymentservice/releases/tag/v1.0.0)

This microservice is part of the car-rental project which was built
by the Asys course 19/20 at the TH Bingen.

It handles payments.

The Microservice can be monitored by Prometheus.

Logs can be sent to Elasticsearch/Logstash using Filebeat.

## Documentation
See [Management project](https://github.com/asys1920/management) for a documentation of the whole Car-Rental project.
## Features
This microservice can handle the payment and if the payment was successful it set the bill as paid. Furthermore, it exposes health,
info and shutdown endpoints using Spring Boot Actuator. By exposing a special /actuator/prometheus endpoint it can
be monitored using Prometheus. By using Filebeat the logs the microservice generates are sent to Elasticsearch/Logstash.

## Requirements
A JDK with at least Java Version 11.

### Local
### Docker
## Quick Start / Setup
### Run Local
### Run Docker

## API
To see a full documentation view the swagger documentation while running the microservice. You can
find the Swagger Documentation at `http://<host>:<port>/swagger-ui.html` 

Method | Endpoint | Parameters | Request Body | Description
--- | --- | ---  | --- | ---
PATCH | /pay | N/A | PaymentRequest in JSON Format | Pays a bill
