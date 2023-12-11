package ru.igojig.fxmessenger.services.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.storage.HistoryService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileHistoryServiceImpl implements HistoryService {

    private static final Logger logger = LogManager.getLogger(FileHistoryServiceImpl.class);

    public final String pathPrefix = "./history/";

    public static final String FILENAME_FORMAT_STRING = "h_%04d.txt";

    //
    public static final int LAST_LINES_COUNT = 100;


    public FileHistoryServiceImpl() {

    }

    @Override
    public void clearHistory() {
        Path root = Paths.get(pathPrefix);
        try (Stream<Path> list = Files.list(root)) {
            list.forEach(f -> {
                Path fileName = f.getFileName();
                if (fileName.toFile().getName().startsWith(FILENAME_FORMAT_STRING.substring(0, 2))) {
                    try {
                        Files.delete(f);
                    } catch (IOException e) {
                        logger.debug("Не удалось удалить файл: " + f, e);
                    }
                }
            });
            logger.debug("Файловая история очищена");
        } catch (IOException e) {
            logger.debug("Ошибка очистки истории", e);
        }
    }

    @Override
    public List<String> loadHistory(User user) {
        Path root = Paths.get(pathPrefix, String.format(FILENAME_FORMAT_STRING, user.getId()));

        try (BufferedReader bufferedReader = Files.newBufferedReader(root);
             LineNumberReader lineNumberReader = new LineNumberReader(bufferedReader);
        ) {
            int numLines = Integer.parseInt(lineNumberReader.readLine());
            int from = numLines <= LAST_LINES_COUNT ? 0 : numLines - LAST_LINES_COUNT;
            while (lineNumberReader.getLineNumber() < from) {
                lineNumberReader.readLine();
            }
            List<String> strings = new ArrayList<>();
            for (int i = from; i < numLines; i++) {
                String s = lineNumberReader.readLine();
                strings.add(s);
            }
            return strings;
        } catch (IOException ex) {
            logger.debug("Не удается открыть файл: " + root, ex);
            return Collections.emptyList();
        }


//        String fullPath = pathPrefix + String.format(FILENAME_FORMAT_STRING, user.getId());
//        File file = new File(fullPath);
//        if (!file.exists()) {
//            logger.warn("Файл c историей: " + fullPath + " не найден");
//            return Collections.emptyList();
//        }
//        try (FileReader fileReader = new FileReader(file);
//             LineNumberReader l = new LineNumberReader(fileReader)) {
//
//            int numLines = Integer.parseInt(l.readLine());
//
//            int from = numLines <= LAST_LINES_COUNT ? 0 : numLines - LAST_LINES_COUNT;
//
//            while (l.getLineNumber() < from) {
//                l.readLine();
//            }
//
//            List<String> strings = new ArrayList<>();
//            for (int i = from; i < numLines; i++) {
//                String s = l.readLine();
//                strings.add(s);
//            }
//            return strings;
//
//        } catch (Exception e) {
//            logger.warn("Файл с историей сообщений не удалось загрузить: " + fullPath, e);
//            return Collections.emptyList();
//        }

    }

    @Override
    public void saveHistory(User user, List<String> history) {
        Path root = Paths.get(pathPrefix, String.format(FILENAME_FORMAT_STRING, user.getId()));

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(root, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bufferedWriter.write(String.valueOf(history.size()) + '\n');
            for (String str : history) {
                bufferedWriter.write(str);
                bufferedWriter.write('\n');
            }
        } catch (IOException e) {
            logger.warn("Не удалось записать историю: " + root, e);
        }


//        String fullPath = pathPrefix + String.format(FILENAME_FORMAT_STRING, user.getId());
//        File file = new File(fullPath);
//        try (FileWriter fileWriter = new FileWriter(file)) {
//
//            // в начало файла пишем общее количество строк в нем
//            fileWriter.write(String.valueOf(history.size()) + '\n');
//
//            for (String sequence : history) {
//                fileWriter.write(sequence);
//                fileWriter.write('\n');
//            }
//
//        } catch (IOException e) {
//            logger.warn("Не удалось записать историю: " + fullPath, e);
//        }
    }


}
