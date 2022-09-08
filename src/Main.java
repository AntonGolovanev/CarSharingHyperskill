package carsharing;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static final String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db/";
    static Connection conn = null;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String dbFilename = getDbFilename(args);
        DB_URL = DB_URL + dbFilename;
        createTableCompany(getStatement());
        createTableCar(getStatement());
        createTableCustomer(getStatement());
        resetID(getStatement());
        mainMenu();
    }


    static void mainMenu() throws SQLException, ClassNotFoundException {
        while (true) {
            System.out.println("1. Log in as a manager\n" +
                    "2. Log in as a customer\n" +
                    "3. Create a customer\n" +
                    "0. Exit");
            int option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (option) {
                case 1 -> Manager.logInAsAManager();
                case 2 -> Customer.logInAsCustomer();
                case 3 -> Customer.createCustomer();
                case 0 -> System.exit(0);
                default -> System.out.println("Invalid operation try again!");
            }
        }

    }


    // create tables
    private static void createTableCustomer(Statement stmt) throws SQLException {
        String sql = "create table IF NOT EXISTS CUSTOMER(\n" +
                "ID INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "NAME VARCHAR(255) UNIQUE NOT NULL,\n" +
                "RENTED_CAR_ID INT,\n" +
                "CONSTRAINT fk_customer FOREIGN KEY (RENTED_CAR_ID) \n" +
                "REFERENCES CAR (ID)\n" +
                ");";
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    private static void createTableCompany(Statement stmt) throws SQLException {
        String sql = "create table IF NOT EXISTS COMPANY" +
                "(id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                " name VARCHAR(255) not NULL UNIQUE)";
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    private static void createTableCar(Statement stmt) throws SQLException {
        String sql = "create table IF NOT EXISTS CAR(\n" +
                "ID INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "NAME VARCHAR(255) UNIQUE NOT NULL,\n" +
                "COMPANY_ID INT NOT NULL,\n" +
                "CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID)\n" +
                "REFERENCES COMPANY(id)\n" +
                ");";
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    private static void resetID(Statement stmt) throws SQLException {
        String sql = "ALTER TABLE company ALTER COLUMN id RESTART WITH 1;\n" +
                "ALTER TABLE car ALTER COLUMN id RESTART WITH 1;\n" +
                "ALTER TABLE customer ALTER COLUMN id RESTART WITH 1;";
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();

    }

    static Statement getStatement() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL);
        conn.setAutoCommit(true);
        return conn.createStatement();
    }

    private static String getDbFilename(String[] args) {
        String dbFilename = "newData";
        if (args.length > 1) {
            if (args[0].equals("-databaseFileName")) {
                dbFilename = args[1];
            }
        }
        return dbFilename;
    }
}