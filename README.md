![Static Badge](https://img.shields.io/badge/Java-17%2B-blue)
![Static Badge](https://img.shields.io/badge/Lombok-blue)
![Static Badge](https://img.shields.io/badge/Log4j-blue)
![Static Badge](https://img.shields.io/badge/Sqlite-blue)
![Static Badge](https://img.shields.io/badge/Maven-blue)


# Сетевой чат (серверная часть)
Учебный проект GeekBrains

## Функционал
- аутентификация и регистрация клиентов
- диспетчеризация сообщений от клиентов, в т.ч. приватных
- сохранение истории сообщений при выходе клиента
- загрузка истории сообщение при входе клиента
- изменение имени клиента
- ведение лога
- отключение клиента на этапе процедуры логина/регистрации при неактивности в течении заданного времени. <sup id="a1">[1](#f1)</sup>
- база данных пользователей, истории сообщений, лог-файл создаются в домашнем каталоге пользователя. <sup id="a2">[2](#f2)</sup>
    

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

## Настройки


___

### <sup id="f1">отключение клиента на этапе процедуры логина/регистрации при неактивности в течении заданного времени</sup> 
```java
  public class ClientHandler{
  ...
    public static final int WAIT_USER_AUTHORISATION_TIMEOUT = 60 * 1000;
  ...
  }
```
[⏎](#a1)

### <sup id="f2">база данных пользователей, истории сообщений, лог-файл создаются в домашнем каталоге пользователя</sup>
- [user.home]/chat_
- [user.home]/chat_/history

  | каталог                   | назначение                           |
        |---------------------------|--------------------------------------|
  | [user.home]/chat_         | лог<br/>база данных(users.db)        |
  | [user.home]/chat_/history | история клиентов в текстовом формате |