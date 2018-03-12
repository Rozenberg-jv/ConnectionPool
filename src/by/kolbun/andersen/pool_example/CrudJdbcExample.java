package by.kolbun.andersen.pool_example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CrudJdbcExample {

    private final String url = "jdbc:mysql://localhost/andersen_jdbc";
    private final String driver = "com.mysql.jdbc.Driver";
    private final String user = "root";
    private final String pass = "root";
    private final int maxConnCount = 5;

    private final ManualDevelopersDao dao = new ManualDevelopersDao(url, driver, user, pass, maxConnCount);

    public void execExample() {
        String input;
        System.out.println("Scheme of table developers:\n" +
                "[int id(auto_inc, not null),\nvarchar name(not null),\n" +
                "varchar specialty(not null),\nint salary(not null)]\n");
        printLegend();

        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("choose:");
            input = rdr.readLine();

            while (!"e".equals(input)) {
                //
                switch (input) {
                    case "0":
                        printLegend();
                        break;
                    case "1":
                        dao.showTable();
                        break;
                    case "2":
                        System.out.println("Enter name, specialty and salary via spaces");
                        String[] data = rdr.readLine().split(" ");
                        dao.insertRecord(data);
                        break;
                    case "3":
                        System.out.println("Enter id and column_name [name/specialty/salary] that you want to update (via space): ");
                        String[] data2 = rdr.readLine().split(" ");
                        System.out.println("Enter new value:");
                        String val = rdr.readLine();
                        dao.updateById(data2, val);
                        break;
                    case "4":
                        System.out.println("Enter id of record to delete");
                        int id = Integer.parseInt(rdr.readLine());
                        dao.deleteById(id);
                        break;
                    case "5":
                        dao.getGeneralReport(); // batch/transaction?
                        break;
                    case "count":
                        dao.countConns();
                        break;
                    default:
                        System.out.println("There is no such command. Try again");
                        break;

                }
                System.out.print("choose:");
                input = rdr.readLine();
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private void printLegend() {
        System.out.println("List of commands:\n" +
                " * 1 : show all records\n" +
                " * 2 : create new record\n" +
                " * 3 : update record by id\n" +
                " * 4 : delete record by id\n" +
                " * 5 : get a general report\n" +
                " * 0 : print this tips again\n" +
                " * e : exit");
    }

}
