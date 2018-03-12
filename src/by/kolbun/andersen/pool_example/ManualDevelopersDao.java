package by.kolbun.andersen.pool_example;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ManualDevelopersDao {

    private ConnectionPool pool;

    public ManualDevelopersDao(String url, String driver, String user, String pass, int count) {
        this.pool = ConnectionPool.getInstance();
        pool.initValues(url, driver, user, pass, count);
//        this.pool = ConnectionPool.getInstance();
    }

    void showTable() {
        System.out.println(" > showTable()");
        Connection conn = null;
        Statement st = null;
        ResultSet result = null;
        try {
            conn = pool.retrieve();
            st = conn.createStatement();
            result = st.executeQuery("SELECT * FROM developers");
            printFullTable(result);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) result.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pool.putback(conn);
            } catch (IllegalArgumentException | InterruptedException iae) {
                System.out.println("connection is not from pool, cant put it back");
            }
        }
    }

    void insertRecord(String[] data) {
        System.out.println(" > insertRecord()");
        Connection conn = null;
        PreparedStatement st = null;
        int result;

        String name = data.length >= 1 ? data[0] : "";
        String specialty = data.length >= 2 ? data[1] : "";
        int salary = data.length >= 3 ? Integer.parseInt(data[2]) : 0;

        try {
            conn = pool.retrieve();
            st = conn.prepareStatement("INSERT INTO developers (name, specialty, salary) VALUES (?,?,?)");
            st.setString(1, name);
            st.setString(2, specialty);
            st.setInt(3, salary);
            result = st.executeUpdate();
            System.out.println(result + " rows was affected.");
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pool.putback(conn);
            } catch (IllegalArgumentException | InterruptedException iae) {
                System.out.println("connection is not from pool, cant put it back");
            }
        }

    }


    void updateById(String[] data, String val) {
        System.out.println(" > updateById()");

        Connection conn = null;
        PreparedStatement st = null;
        int result;

        int id = Integer.parseInt(data[0]);
        String column = data.length >= 2 ? data[1] : "";

        try {
            conn = pool.retrieve();
            switch (column) {
                case "name":
                    st = conn.prepareStatement("UPDATE developers SET name = ? WHERE id = ?;");
                    st.setString(1, val);
                    break;
                case "specialty":
                    st = conn.prepareStatement("UPDATE developers SET specialty = ? WHERE id = ?;");
                    st.setString(1, val);
                    break;
                case "salary":
                    st = conn.prepareStatement("UPDATE developers SET salary = ? WHERE id = ?;");
                    st.setInt(1, Integer.parseInt(val));
                    break;
                default:
                    System.out.println("Wrong data.");
                    return;
            }
            st.setInt(2, id);
            result = st.executeUpdate();
            System.out.println(result + " rows was affected.");
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pool.putback(conn);
            } catch (IllegalArgumentException | InterruptedException iae) {
                System.out.println("connection is not from pool, cant put it back");
            }
        }
    }

    void deleteById(int id) {
        System.out.println(" > deleteById()");
        Connection conn = null;
        PreparedStatement st = null;
        String result;
        try {
            conn = pool.retrieve();
            st = conn.prepareStatement("DELETE FROM developers WHERE id = ?");
            st.setInt(1, id);
            result = st.executeUpdate() != 0 ? "Success" : "No deletion was performed";
            System.out.println(result);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pool.putback(conn);
            } catch (IllegalArgumentException | InterruptedException iae) {
                System.out.println("connection is not from pool, cant put it back");
            }
        }
    }

    void getGeneralReport() {
        System.out.println(" > getGeneralReport()");

        Connection conn = null;
        Statement st = null, st2 = null;
        ResultSet result1 = null, result2 = null;
        try {
            conn = pool.retrieve();

            st = conn.createStatement();
            String query = "SELECT specialty, count(*) AS cnt FROM developers GROUP BY specialty ORDER BY cnt DESC;";
            result1 = st.executeQuery(query);

            st2 = conn.createStatement();
            query = "SELECT specialty, avg(salary) AS sal FROM developers GROUP BY specialty;";
            result2 = st2.executeQuery(query);

            System.out.println("\n#==================#");
            System.out.println("Specialty:\t\tCount:\t\t");
            String specialty;
            int count;
            while (result1.next()) {
                specialty = result1.getString("specialty");
                count = result1.getInt("cnt");
                System.out.print(specialty + "\t\t");
                System.out.print("\t" + count + "\t\t");
                System.out.println();
            }

            System.out.println("\n#==================#");
            System.out.println("Specialty:\t\tSalary:\t\t");
            int salary;
            while (result2.next()) {
                specialty = result2.getString("specialty");
                salary = result2.getInt("sal");
                System.out.print(specialty + "\t\t");
                System.out.print(salary + "\t\t");
                System.out.println();
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                pool.putback(conn);
            } catch (IllegalArgumentException | InterruptedException iae) {
                System.out.println("connection is not from pool, cant put it back");
            }
            try {
                if (result1 != null) result1.close();
                if (result2 != null) result2.close();
                if (st != null) st.close();
                if (st2 != null) st2.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println();
            }
        }
        System.out.println();
    }


    public static void printFullTable(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            System.out.println("\n! NULL RESULTSET !");
            return;
        }

//        System.out.println("#==================#");
        System.out.println("id:\t\tName:\t\tSpecialty:\t\tSalary:\t\t");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String specialty = resultSet.getString("specialty");
            int salary = resultSet.getInt("salary");
            System.out.print(id + "\t\t");
            System.out.print(name + "\t\t");
            System.out.print(specialty + "\t\t");
            System.out.print("\t" + salary + "\t\t");
            System.out.println();
        }
    }

    public void createTable() {
        Connection con = null;
        Statement st = null;
        try {
            con = pool.retrieve();
            st = con.createStatement();
            String query = "CREATE TABLE `developers` (\n" +
                    "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` VARCHAR(50) NOT NULL,\n" +
                    "  `specialty` VARCHAR(50) NOT NULL,\n" +
                    "  `salary` INT(11) NOT NULL,\n" +
                    "        PRIMARY KEY (`id`)\n" +
                    ")";
            st.execute(query);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                pool.putback(con);
                if (st != null) st.close();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void countConns() {
        System.out.println("free: " + pool.getAvailableCount() + ", used: " + pool.getUsedCount());
    }
}
