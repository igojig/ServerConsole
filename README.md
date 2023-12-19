![Static Badge](https://img.shields.io/badge/Java-17%2B-blue)
![Static Badge](https://img.shields.io/badge/Lombok-blue)
![Static Badge](https://img.shields.io/badge/Log4j-blue)
![Static Badge](https://img.shields.io/badge/Sqlite-blue)
![Static Badge](https://img.shields.io/badge/Maven-blue)


# Сетевой чат (серверная часть)
Учебный проект GeekBrains (сервер и [клиент](https://github.com/igojig/FxMessager_client))

## Функционал
- аутентификация и регистрация клиентов
- проверка от повторного входа
- диспетчеризация сообщений от клиентов, в т.ч. приватных
- сохранение истории сообщений при выходе клиента
- загрузка истории сообщение при входе клиента
- изменение имени клиента
- ведение лога
- отключение клиента на этапе процедуры логина/регистрации при неактивности в течении заданного времени.<sup id="a1">[1](#f1)</sup>
- база данных пользователей, истории сообщений, лог-файл создаются в домашнем каталоге пользователя.<sup id="a2">[2](#f2)</sup>
- при старте проекта опционально можно очистить историю всех пользователей и создать дефолтных пользователей в БД.<sup id="a3">[3](#f3)</sup>
    

## Установка
```
git clone https://github.com/igojig/ServerConsole
```

## Запуск
```
chcp 1251
mvn clean package
cd /target
java -jar Server-jar-with-dependencies.jar
```
или запуск из IDE

### Связанный проект - [Клиентская часть](https://github.com/igojig/FxMessager_client)

## Настройки


___

### <sup id="f1">1. отключение клиента на этапе процедуры логина/регистрации при неактивности в течении заданного времени</sup> 
```java
  public class ClientHandler{
  ...
    public static final int WAIT_USER_AUTHORISATION_TIMEOUT = 60 * 1000;
  ...
  }
```
[⏎](#a1)

### <sup id="f2">2. база данных пользователей, истории сообщений, лог-файл создаются в домашнем каталоге пользователя</sup>
- [user.home]/chat_
- [user.home]/chat_/history

  | каталог                   | назначение                            |
  |---------------------------|---------------------------------------|
  | [user.home]/chat_         | лог;<br/>база данных(users.db);       |
  | [user.home]/chat_/history | история клиентов в текстовом формате; |
```java
public class LocalFileService {
    public static final String USER_HOME_DIR_ENVIRONMENT_VARIABLE = "user.home";
    public static final String STORAGE_DIR = "chat_";
    public static final String HISTORY_DIR = "history";
```
[⏎](#a2)

### <sup id="f3">3. при старте проекта опционально можно очистить историю всех пользователей и создать дефолтных пользователей в БД</sup>
```java
public class ServerApp {
    private static final boolean INIT_DB = true;
    private static final boolean CLEAR_HISTORY = true;
}
```
[⏎](#a3)

### Диаграмма взаимодействия

[//]: # (![UML]&#40;http://plantuml.com/plantuml/png/5Sox3G8n303GdYbWWRYdEeaHcC0aLiueja_-mFfmrUlU5ecU9UjoO-sh1fMlwvvvn1t0yTicvrwl2l9kcgJPkWdmj3TFU99_o1-cOOj0C98S1kDq56d7-Mwau-yF&#41;)
![UML](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/igojig/ServerConsole/master/github_assets/diagram.puml)