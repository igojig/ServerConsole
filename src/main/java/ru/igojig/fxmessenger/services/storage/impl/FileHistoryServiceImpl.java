package ru.igojig.fxmessenger.services.storage.impl;

import ru.igojig.fxmessenger.model.User;
import ru.igojig.fxmessenger.services.storage.HistoryService;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileHistoryServiceImpl implements HistoryService {

    public static final String FILENAME_FORMAT_STRING="h_%04d.txt";
    public static final int LAST_LINES_COUNT=100;



    public FileHistoryServiceImpl() {
//        String fileName=String.format(FILENAME_FORMAT_STRING, user.getId());
//        file=new File(fileName);
    }

    @Override
    public  List<String> getHistory(User user) {
        File file=new File(String.format(FILENAME_FORMAT_STRING, user.getId()));
        if (!file.exists()) {
            System.out.println("Файл: " + file.getName() + " не найден");
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

        } catch ( Exception e) {
            e.printStackTrace();
            System.out.println("Файл с историей сообщений не удалось загрузить: " + file.getName());
            return Collections.emptyList();
        }

    }

    @Override
    public void setHistory(User user, List<String> history) {
        File file=new File(String.format(FILENAME_FORMAT_STRING, user.getId()));
        try (FileWriter fileWriter = new FileWriter(file)) {

            // в начало файла пишем общее количество строк в нем
            fileWriter.write(String.valueOf(history.size()) + '\n');

            for (String sequence : history) {
                fileWriter.write(sequence);
                fileWriter.write('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось записать историю: " + file);
        }
    }



}
