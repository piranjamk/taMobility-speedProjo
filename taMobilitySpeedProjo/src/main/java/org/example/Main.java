package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class Main {

    final static private String dataBaseTableName = "TATABLE";
    private static final Logger LOGS = LogManager.getLogger("Main");
    public static void main(String[] args) {

        if (args.length == 0)
        {
            LOGS.fatal("No logsFile specified as entry parameter!");
            System.exit(0);
        }
        List<LogRecord> logsList = LogsLoader.loadLogs(args[0]);
        LOGS.info("Start!");

        Connection con;
        Statement stmt;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:tademo", "SA", "");

            if (con!= null)
            {
                stmt = con.createStatement();

                DatabaseMetaData dbm = con.getMetaData();
                // check if table exists
                ResultSet tables = dbm.getTables(null, null, dataBaseTableName, null);
                if (tables.next()) {
                    System.out.println("Table exists");
                    LOGS.info("Table exists");
                }
                else
                {
                    LOGS.info("Table does not exists in database.");
                    stmt.executeUpdate("CREATE TABLE "+dataBaseTableName+" ( id VARCHAR(20) NOT NULL, state VARCHAR(50) NOT NULL, host VARCHAR(50), duration INT, alert BIT, PRIMARY KEY (id));  ");
                    LOGS.info("Table created");
                }

                //----------------    insert into database     -------------------------------------------------------------------------------
                if (logsList.isEmpty()) {
                    LOGS.info("Log file iis empty - nothing to process");
                }
                int currentIndex = logsList.size() -  1;
                while(currentIndex >= 0)
                {
                    if (currentIndex == 0)
                    {
                        LOGS.warn("There is no pair for record id: " + logsList.get(0).id);
                        break;
                    }

                    LogRecord firstEntry = logsList.get(currentIndex);
                    LogRecord secondEntry = logsList.get(currentIndex - 1);
                    if (!firstEntry.id.equals(secondEntry.id))
                    {
                        LOGS.warn("There is only single entry for id:" + firstEntry.id);
                        currentIndex--;
                    }
                    else
                    {
                        LOGS.info("inserting logs | log id::" + firstEntry.id);
                        String insertString = "INSERT INTO "+dataBaseTableName+"  VALUES ('"+firstEntry.id+"', '"+firstEntry.state+"', '"+firstEntry.host+"', "+( Math.abs(firstEntry.timeStamp - secondEntry.timeStamp))+", "+ (Math.abs(firstEntry.timeStamp - secondEntry.timeStamp) > 4) + ")";
                        stmt.executeUpdate(insertString);
                        con.commit();
                        LOGS.info("Log saved in database");
                        currentIndex-=2;
                    }
                }
                //print select from database
                ResultSet result;
                result = stmt.executeQuery(
                        "SELECT * FROM "+ dataBaseTableName);


                LOGS.debug("\n\n\n\nListing:");
                while(result.next()){
                    LOGS.debug(result.getString("id")+" | "+
                            result.getString("state")+" | "+
                            result.getString("host")+" | "+
                            result.getString("duration")+" | "+
                            result.getString("alert"));
                }
            } else
            {
                LOGS.fatal("Problem with creating connection");
            }
        }
        catch (Exception e)
        {
            LOGS.fatal("Program failed. Not connected to DB");
            LOGS.fatal(e.getMessage());
        }

        LOGS.info("Done!");
    }
}