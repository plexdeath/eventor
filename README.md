# Eventor

Gradle‑плагин

## Назначение

Плагин добавляет задачу `processEvents`, которая:

- читает JSON‑файл с событиями;
- в зависимости от поля `actionType` вызывает соответствующие методы сервиса:
  - `sendHttpEvent`
  - `saveToDatabase`
  - `sendToKafka`

## Установка

1. Убедитесь, что в проекте существует структура каталогов:

```
src/main/resources/META-INF/gradle-plugins/
```

2. Создайте файл `eventor.processor.properties` в указанной директории со следующим содержимым:

```
implementation-class=ru.komus.eventorplugin.processor.EventorProcessorPlugin
```

3. Подключите плагин в `build.gradle`:

```groovy
plugins {
    id 'eventor.processor' version '1.0'
}
```

## Конфигурация

В `build.gradle` можно указать путь к JSON‑файлу с событиями:

```groovy
eventorProcessor {
    jsonFilePath = 'src/test/resources/events.json'
}
```

### Параметры

- `jsonFilePath` — путь к файлу с событиями

## Использование

1. Соберите проект:

```bash
./gradlew clean build
```

2. Запустите задачу обработки событий:

```bash
./gradlew processEvents
```

3. Проверка группы задач (опционально):

```bash
./gradlew tasks --group processing
```

В списке должна присутствовать задача `processEvents`.

## Структура JSON

Пример содержимого файла `events.json`:

```json
[
  {
    "id": 1,
    "eventName": "Test Event",
    "timestamp": "2024-01-01T00:00:00Z",
    "actionType": "HTTP",
    "payload": {
      "test": "data"
    }
  }
]
```

### Обязательные поля

- `actionType` — определяет тип действия:
  - `HTTP`
  - `DB`
  - `KAFKA`

## Задачи плагина

### processEvents

- **Группа:** `processing`
- **Описание:** Process events from JSON file
- **Действие:** читает JSON‑файл и запускает обработку событий

## Зависимости

Плагин использует:

- **Jackson** (`com.fasterxml.jackson.databind`) — для работы с JSON
- **Gradle API**  для интеграции с системой сборки

Убедитесь, что зависимости указаны в `build.gradle`:

```groovy
dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.3'
}
```
## Контакты

- **Автор:** Vladimir_Safonov
- **Email:** safonov-ve@yandex.ru
- **Репозиторий:** https://github.com/plexdeath/eventor.git
