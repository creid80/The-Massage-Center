package C195.utilities;

import java.sql.Connection;
import java.sql.DriverManager;

/**@author Carol Reid*/

/**This class establishes and closes the connection to the database.*/
public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection connection;

    /**This method opens the connection to the database.*/
    public static void openConnection(){
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**This method closes the connection to the database.*/
    public static void closeConnection() {
        try {
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
