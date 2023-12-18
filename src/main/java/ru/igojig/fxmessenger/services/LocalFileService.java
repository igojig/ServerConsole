package ru.igojig.fxmessenger.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

public class LocalFileService {
    public static final String USER_HOME_DIR_ENVIRONMENT_VARIABLE = "user.home";
    public static final String STORAGE_DIR = "chat_";
    public static final String HISTORY_DIR = "history";

    private static final Logger logger = LogManager.getLogger(LocalFileService.class);

    public static Path userHomePath;
    public static Path storagePath;
    public static Path historyPath;


    public static void initStorage() {
        String userDir = System.getProperty(USER_HOME_DIR_ENVIRONMENT_VARIABLE);
        if (userDir == null) {
            logger.warn("Не удалось найти рабочий каталог пользователя");
            return;
        }

        userHomePath = Path.of(userDir);
        storagePath = Path.of(userDir, STORAGE_DIR);
        historyPath = Path.of(userDir, STORAGE_DIR, HISTORY_DIR);

        if (!Files.exists(historyPath)) {
            try {
                Files.createDirectories(historyPath);
                logger.info("Создали каталог для хранения истории:" + historyPath);
                return;
            } catch (IOException e) {
                logger.debug("Не удалось создать каталог для хранения истории", e);
                return;
            }
        }
    }

    public static void clearHistory(boolean doClear) {
        if (!doClear) {
            return;
        }

        try (Stream<Path> list = Files.list(historyPath)) {
            list.forEach(f -> {
                try {
                    Files.delete(f);
                } catch (IOException e) {
                    logger.debug("Не удалось удалить файл: " + f, e);
                }
            });
            logger.debug("Файловая история очищена");
        } catch (IOException e) {
            logger.debug("Ошибка очистки истории", e);
        }

    }

    public static void copyDB() {
        Path resolve = storagePath.resolve("users.db");
        try (InputStream resourceAsStream = LocalFileService.class.getResourceAsStream("/users.db")) {
            assert resourceAsStream != null;
            Files.copy(resourceAsStream, resolve);
            logger.info(String.format("Скопировали БД: [%s]", resolve));
        } catch (FileAlreadyExistsException e) {
            logger.warn(String.format("Файл [%s] существует", resolve));
        } catch (IOException e) {
            logger.error(String.format("Не удалось скопировать БД в рабочий каталог: [%s]", resolve), e);
            System.exit(0);
        }
    }
}
