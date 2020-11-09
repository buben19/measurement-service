# Measurement service

Simple measurement service allowing user to save and retrieve measurements and find longest measurement streaks.

## Run application

To run application simply run:

    ./mwn package
    docker-compose up

## Used technologies

Application uses following technologies:

  * Spring Boot, Hibernate, REST HATEOAS API.
  * PostgreSQL as backend database server.
  * Spock framework.
  * Prometheus monitoring system.
  * Grafana web server to view metrics.
  * Pgadmin4.

## Services

After docker server is up and running, you can access following services.

  * [REST API](http://localhost:8081/measurements)
  * [Pgadmin](http://localhost:5050)
    * Credentials: `postgres@example.com`:`password`
  * [Prometheus console](http://localhost:9090)
  * [Grafana web server](http://localhost:3000)
    * Credentials: `admin`:`admin`
