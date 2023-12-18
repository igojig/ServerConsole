![Static Badge](https://img.shields.io/badge/java-17%2B-blue)
![Static Badge](https://img.shields.io/badge/Lombok-blue)
![Static Badge](https://img.shields.io/badge/Log4j-blue)
![Static Badge](https://img.shields.io/badge/sqlite-blue)
![Static Badge](https://img.shields.io/badge/maven-blue)


# Сетевой чат (серверная часть)
Учебный проект GeekBrains [^1]

## Функционал
- диспетчеризация сообщений от клиентов
- сохранение истории сообщений при выходе клиента
- загрузка истории сообщение при входе клиента
- аутентификация и регистрация клиентов
- ведение лога
- отключение клиента на этапе процедуры логина/регистрации при неактивности в течении 60 сек. [^2] Задается параметром:
    ````java
  public class ClientHandler{
  ...
    public static final int WAIT_USER_AUTHORISATION_TIMEOUT = 60 * 1000;
  ...
  }
    ````
- при запуске ищется домашний каталог пользователя. В нем создаются директории:
    - [user.home]/chat_              
    - [user.home]/chat_/history
    - 
      | каталог                   | назначение                           |
      |---------------------------|--------------------------------------|
      | [user.home]/chat_         | лог<br/>база данных(users.db)        |
      | [user.home]/chat_/history | история клиентов в текстовом формате |

## Установка/
````
git clone https://github.com/igojig/ServerConsole
````

## Запуск
````
chcp 1251
mvn clean package
cd /target
java -jar Server-jar-with-dependencies.jar
````
или запуск из IDE

[^1]: 