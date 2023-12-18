package ru.igojig.fxmessenger.services.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.storage.HistoryService;
import ru.igojig.fxmessenger.services.LocalFileService;

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

public class FileHistoryServiceImpl implements HistoryService {

    private static final Logger logger = LogManager.getLogger(FileHistoryServiceImpl.class);
    public static final String FILENAME_FORMAT_STRING = "h_%04d.txt";
    // сколько строк сохраняем в истории пользователя
    public static final int NUM_LINES_STORED = 100;


    public FileHistoryServiceImpl() {

    }

    @Override
    public List<String> loadHistory(User user) {
        Path root = Paths.get(LocalFileService.historyPath.toString(), String.format(FILENAME_FORMAT_STRING, user.getId()));

        try (BufferedReader bufferedReader = Files.newBufferedReader(root);
             LineNumberReader lineNumberReader = new LineNumberReader(bufferedReader);
        ) {
            int numLines = Integer.parseInt(lineNumberReader.readLine());
            int from = numLines <= NUM_LINES_STORED ? 0 : numLines - NUM_LINES_STORED;
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
    }

    @Override
    public void saveHistory(User user, List<String> history) {
        Path root = Paths.get(LocalFileService.historyPath.toString(), String.format(FILENAME_FORMAT_STRING, user.getId()));

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(root, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            bufferedWriter.write(String.valueOf(history.size()) + '\n');
            for (String str : history) {
                bufferedWriter.write(str);
                bufferedWriter.write('\n');
            }
        } catch (IOException e) {
            logger.warn("Не удалось записать историю: " + root, e);
        }
    }
}
