package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static carsharing.Main.*;

public class Customer {
    static void logInAsCustomer() throws SQLException, ClassNotFoundException {
        Statement stat = Main.getStatement();
        String sql = "SELECT * FROM CUSTOMER";
        ResultSet rs = stat.executeQuery(sql);
        if (!rs.next()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Customer list:");
            do {
                String id = rs.getString("ID");
                String name = rs.getString("NAME");
                System.out.printf("%s. %s%n", id, name);
            } while (rs.next());
            System.out.println("0. Back");
            int option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            if (option == 0) {
                mainMenu();
            } else {
                customerMenu(String.valueOf(option));
            }
        }
        System.out.println();

    }

    static void customerMenu(String customerID) throws SQLException, ClassNotFoundException {
        System.out.println("1. Rent a car\n" +
                "2. Return a rented car\n" +
                "3. My rented car\n" +
                "0. Back");
        int option = scanner.nextInt();
        scanner.nextLine();
        System.out.println();

        switch (option) {
            case 1 -> rentCar(customerID);
            case 2 -> returnCar(customerID);
            case 3 -> getRentedCar(customerID);
            case 0 -> mainMenu();
            default -> System.out.println("Invalid operation!");
        }
        System.out.println();
        customerMenu(customerID);
    }

    private static void getRentedCar(String customerID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        String sql = String.format("SELECT cr.Name As car_name, co.name AS company_name\n" +
                "FROM CUSTOMER cu\n" +
                "JOIN CAR cr\n" +
                "ON cu.RENTED_CAR_ID =  cr.ID\n" +
                "JOIN company co\n" +
                "ON cr.COMPANY_ID = co.id\n" +
                "WHERE cu.id = %s", customerID);
        ResultSet rs = stat.executeQuery(sql);
        if (!rs.next()) {
            System.out.println("You didn't rent a car!");
        } else {

            do {
                String carName = rs.getString("car_name");
                String companyName = rs.getString("company_name");
                System.out.println("Your rented a car:");
                System.out.println(carName);
                System.out.println("Company:");
                System.out.println(companyName);

            } while (rs.next());

        }
        stat.close();
        conn.close();
    }


    private static void rentCar(String customerID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        String sql = String.format("Select rented_car_id FROM customer where id = %s\n" +
                "and rented_car_id is not null",customerID);
        ResultSet rs = stat.executeQuery(sql);
        if (!rs.next()) {
            rentCarProcess(customerID);
        } else {
            System.out.println("You've already rented a car!");
        }
        stat.close();
        conn.close();
    }

    private static void rentCarProcess(String customerID) throws ClassNotFoundException, SQLException {
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
                System.out.printf("%s. %s%n", id, name);
            } while (rs.next());
            System.out.println("0. Back");

            int option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            if (option == 0) {
                customerMenu(customerID);
            } else {
                System.out.println();
                carMenuASCustomer(customerID);
            }

        }
        stat.close();
        conn.close();
    }


    private static void carMenuASCustomer( String customerID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        Map<String, String> map = new HashMap<>();
        String sql = " SELECT car.id, car.name, car.company_id \n" +
                "                    FROM car LEFT JOIN customer \n" +
                "                    ON car.id = customer.rented_car_id \n" +
                "                    WHERE customer.name IS NULL";
        ResultSet rs = stat.executeQuery(sql);
        if (!rs.next()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("Choose a car:");
            do {
                String id = rs.getString("ID");
                String name = rs.getString("NAME");
                map.put(id, name);
                System.out.printf("%s. %s%n", id, name);
            } while (rs.next());
            System.out.println("0. Back");
            int option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            if (option == 0) {
                customerMenu(customerID);
            } else {
                String rentedCarSql = String.format("UPDATE customer SET RENTED_CAR_ID = %s\n" +
                        "WHERE Id = %s", option, customerID);
                stat.executeUpdate(rentedCarSql);
                String carName = map.get(String.valueOf(option));
                System.out.printf("You rented '%s'%n", carName);


            }
        }
        stat.close();
        conn.close();
    }
    private static void returnCar(String customerID) throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        String checkCarSql = String.format("SELECT RENTED_CAR_ID\n" +
                "From customer\n" +
                "WHERE ID = %s\n" +
                "AND RENTED_CAR_ID IS NOT NULL", customerID);
        ResultSet rs = stat.executeQuery(checkCarSql);

        if (!rs.next()) {
            System.out.println("You didn't rent a car!");
        } else {
            String returnCarSql = String.format("\n" +
                    "UPDATE customer SET RENTED_CAR_ID = null\n" +
                    "WHERE ID = %s", customerID);
            stat.executeUpdate(returnCarSql);
            System.out.println("You've returned a rented car!");
        }
        stat.close();
        conn.close();
    }
    static void createCustomer() throws SQLException, ClassNotFoundException {
        Statement stat = getStatement();
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        String sql = String.format("\n" +
                "INSERT INTO customer (NAME) VALUES ('%s')", customerName);
        stat.execute(sql);
        stat.close();
        conn.close();
        System.out.println("The customer was added!");
        System.out.println();
    }

}
