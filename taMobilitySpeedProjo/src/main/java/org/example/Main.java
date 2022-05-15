package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class Main {

    final static private String dataBaseTableName = "TATABLE";
    private static final Logger LOGS = LogManager.getLogger("Main");
    public static void main(String[] args) {

        List<LogRecord> logsList = LogsLoader.loadLogs("C:\\Users\\piran\\Desktop\\tamobilityspeedprojo\\taMobility-speedProjo\\taMobilitySpeedProjo\\src\\test\\resources\\testLogsFile.txt");
        System.out.println("Hello world!");
        LOGS.info("Start!");

        Connection con;
        Statement stmt;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/tademo", "SA", "");

            if (con!= null)
            {
                stmt = con.createStatement();
//REMOVE - TEMPORARY
//FOR TESTING PURPOSES ONLY
                //stmt.executeUpdate("DROP TABLE "+tablename);

                DatabaseMetaData dbm = con.getMetaData();
                // check if table exists
                ResultSet tables = dbm.getTables(null, null, dataBaseTableName, null);
                if (tables.next()) {
                    System.out.println("Table exists");
                }
                else
                {
                    stmt.executeUpdate("CREATE TABLE "+dataBaseTableName+" ( id VARCHAR(20) NOT NULL, state VARCHAR(50) NOT NULL, host VARCHAR(50), duration INT, alert BIT, PRIMARY KEY (id));  ");
                }

                //----------------    insert into database     -------------------------------------------------------------------------------
                if (logsList.isEmpty()) {
                }
                int currentIndex = logsList.size() -  1;
                while(currentIndex >= 0)
                {
                    if (currentIndex == 0)
                    {
                        System.out.println("There is no pair for record id: " + logsList.get(0).id);
                        break;
                    }

                    LogRecord firstEntry = logsList.get(currentIndex);
                    LogRecord secondEntry = logsList.get(currentIndex - 1);
                    if (!firstEntry.id.equals(secondEntry.id))
                    {
                        currentIndex--;
                    }
                    else
                    {
                        String insertString = "INSERT INTO "+dataBaseTableName+"  VALUES ('"+firstEntry.id+"', '"+firstEntry.state+"', '"+firstEntry.host+"', "+( Math.abs(firstEntry.timeStamp - secondEntry.timeStamp))+", "+ (Math.abs(firstEntry.timeStamp - secondEntry.timeStamp) > 4) + ")";
                        stmt.executeUpdate(insertString);
                        con.commit();
                        currentIndex-=2;        //id VARCHAR(20) NOT NULL, state VARCHAR(50) NOT NULL,  type VARCHAR(20), host VARCHAR(50), timeStamp INT, alert BIT, PRIMARY KEY (id));  ");
                    }
                }
                //print select from database
                System.out.println("\n\n\n\n");
                ResultSet result;
                result = stmt.executeQuery(
                        "SELECT * FROM "+ dataBaseTableName);


                System.out.println("Listing:");
                while(result.next()){
                    System.out.println(result.getString("id")+" | "+
                            result.getString("state")+" | "+
                            result.getString("host")+" | "+
                            result.getString("duration")+" | "+
                            result.getString("alert"));
                }
            } else
            {
                System.out.println("Problem with creating connection");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }

        System.out.println("Done!");
    }
}