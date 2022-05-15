import org.example.LogRecord;
import org.example.LogsLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LogsLoaderTest {

    @Test
    void shouldLoadRecordsFromLogsFile() {
        List<LogRecord> logsList = LogsLoader.loadLogs("./src/test/resources/testLogsFile.txt");
        Assertions.assertEquals(7, logsList.size());
    }

    @Test
    void shouldLoadAnSortLogsRecords() {
        List<LogRecord> logsList = LogsLoader.loadLogs("./src/test/resources/testLogsFile.txt");
        Assertions.assertEquals("1", logsList.get(0).id);
        Assertions.assertEquals("26", logsList.get(1).id);
        Assertions.assertEquals("26",logsList.get(2).id);
        Assertions.assertEquals("5", logsList.get(3).id);
        Assertions.assertEquals("5", logsList.get(4).id);
        Assertions.assertEquals("abc", logsList.get(5).id);
        Assertions.assertEquals("abc", logsList.get(6).id);
    }
}