package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static carsharing.Main.*;

public class Manager {

    static void logInAsAManager() throws SQLException, ClassNotFoundException {
        System.out.println("1. Company list\n" +
                "2. Create a company\n" +
                "0. Back");
        int option = scanner.nextInt();
        scanner.nextLine();
        System.out.println();

        switch (option) {
            case 1 -> companyListAsManager();
            case 2 -> createACompany();
            case 0 -> mainMenu();
            default -> System.out.println("Invalid operation !");
        }
        System.out.println();
        logInAsAManager();
    }

    private static void companyListAsManager() throws SQLException, ClassNotFoundException {
        Map<String, String> map = new HashMap<>();
        Statement stat = getStatement();
        String sql = "SELECT * FROM COMPANY";
        ResultSet rs = stat.executeQuery(sql);
        if (!rs.next()) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Choose a company:");
            do {
                String id = rs.getString("ID");
                String name = rs.getString("NAME");
                map.put(id, name);
                System.out.printf("%s. %s%n", id, name);
            } while (rs.next());
            stat.close();
            conn.close();
            System.out.println("0. Back");

            int option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            if (option == 0) {
                logInAsAManager();
            } else {
                System.out.println();
                String companyName = map.get(String.valueOf(option));
                System.out.printf("'%s' company:%n", companyName);
                carMenuAsManager(String.valueOf(option));
            }
        }
    }

    private static void createACompany() throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        System.out.println("Enter the company name:");
        String companyName = scanner.nextLine();
        String sql = String.format("INSERT INTO COMPANY (name) VALUES ('%s')", companyName);
        stat.execute(sql);
        System.out.println("The company was created!");
        stat.close();
        conn.close();

    }

    private static void carMenuAsManager(String companyID) throws SQLException, ClassNotFoundException {
        System.out.println("1. Car list\n" +
                "2. Create a car\n" +
                "0. Back");
        int option = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        switch (option) {
            case 1 -> carListAsManager(companyID);
            case 2 -> createCar(companyID);
            case 0 -> logInAsAManager();
            default -> System.out.println("Invalid operation!");
        }
        System.out.println();
        carMenuAsManager(companyID);

    }

    private static void carListAsManager(String companyID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        String sql = String.format("SELECT * FROM CAR WHERE COMPANY_ID = %s", companyID);
        ResultSet rs = stat.executeQuery(sql);
        int count = 0;
        if (!rs.next()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("Car list:");
            do {
                count++;
                String name = rs.getString("NAME");
                System.out.printf("%s. %s%n", count, name);
            } while (rs.next());

        }
        stat.close();
        conn.close();
    }

    private static void createCar(String companyID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        System.out.println("Enter the car name:");
        String carName = scanner.nextLine();
        String sql = String.format("\n" +
                "INSERT INTO CAR (NAME, COMPANY_ID) VALUES ('%s',%s)", carName, companyID);
        stat.execute(sql);
        System.out.println("The car was added!");
        System.out.println();
        stat.close();
        conn.close();
    }
}