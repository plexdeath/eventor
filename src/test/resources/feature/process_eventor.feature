Feature: Event Processing

  Scenario: HTTP event
    Given an event with actionType "HTTP"
    When the event is processed
    Then it should be sent via HTTP

  Scenario: DB event
    Given an event with actionType "DB"
    When the event is processed
    Then it should be saved to the database

  Scenario: Kafka event
    Given an event with actionType "KAFKA"
    When the event is processed
    Then it should be sent to Kafka