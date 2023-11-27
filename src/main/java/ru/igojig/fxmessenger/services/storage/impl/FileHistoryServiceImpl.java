package ru.igojig.fxmessenger.services.storage.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.storage.HistoryService;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileHistoryServiceImpl implements HistoryService {

    private static final Logger logger = LogManager.getLogger(FileHistoryServiceImpl.class);

    public final String pathPrefix = "src/main/resources/history/";

    public static final String FILENAME_FORMAT_STRING = "h_%04d.txt";


    //
    public static final int LAST_LINES_COUNT = 100;


    public FileHistoryServiceImpl() {
//        String fileName=String.format(FILENAME_FORMAT_STRING, user.getId());
//        file=new File(fileName);

    }

    @Override
    public List<String> getHistory(User user) {

        String fullPath=pathPrefix + String.format(FILENAME_FORMAT_STRING, user.getId());
        File file = new File(fullPath);
        if (!file.exists()) {
            logger.warn("Файл c историей: " + fullPath + " не найден");
            return Collections.emptyList();
        }
        try (FileReader fileReader = new FileReader(file);
             LineNumberReader l = new LineNumberReader(fileReader)) {

            int numLines = Integer.parseInt(l.readLine());
//            System.out.println(numLines);

            //TODO - проверить!!!! - плучаются null строки
            int from = numLines <= LAST_LINES_COUNT ? 0 : numLines - LAST_LINES_COUNT;

            while (l.getLineNumber() < from) {
                l.readLine();
            }

            List<String> strings = new ArrayList<>();
            for (int i = from; i < numLines; i++) {
                String s = l.readLine();
                strings.add(s);
            }
            return strings;

        } catch (Exception e) {
//            e.printStackTrace();
            logger.warn("Файл с историей сообщений не удалось загрузить: " + fullPath, e);
            return Collections.emptyList();
        }

    }

    @Override
    public void setHistory(User user, List<String> history) {
        String fullPath=pathPrefix + String.format(FILENAME_FORMAT_STRING, user.getId());
        File file = new File(fullPath);
        try (FileWriter fileWriter = new FileWriter(file)) {

            // в начало файла пишем общее количество строк в нем
            fileWriter.write(String.valueOf(history.size()) + '\n');

            for (String sequence : history) {
                fileWriter.write(sequence);
                fileWriter.write('\n');
            }

        } catch (IOException e) {
//            e.printStackTrace();
            logger.warn("Не удалось записать историю: " + fullPath, e);
        }
    }


}
