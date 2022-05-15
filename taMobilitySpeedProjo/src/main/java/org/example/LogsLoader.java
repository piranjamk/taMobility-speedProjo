package org.example;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LogsLoader {

    public static List<LogRecord> loadLogs(String logsFileToLoad)
    {
        List<LogRecord> logsList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logsFileToLoad));
            String line = reader.readLine();
            Gson gson = new Gson();
            while (line != null) {
                logsList.add(gson.fromJson(line, LogRecord.class));
                line = reader.readLine();
            }
            reader.close();

            Collections.sort( logsList, Comparator.comparing((LogRecord log) -> log.id)); //nlogn - compare lexicographically
            for (LogRecord log: logsList) {
                System.out.println(log.id  +", "+ log.state +", "+ log.type + ", "+log.host + ", "+ log.timeStamp); //improve later: toString
            }
        } catch (IOException e) {
            System.out.println("Could not find file " + logsFileToLoad);
        }

        return logsList;
    }
}
