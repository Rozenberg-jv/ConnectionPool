package by.kolbun.andersen.ignore;

import java.sql.*;

public class DevelopersJdbcDemo {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/andersen_jdbc";

    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void execExample() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;

        System.out.println("Registering JDBC driver...");

        Class.forName(JDBC_DRIVER);

        System.out.println("Creating database connection...");
        connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

        System.out.println("Executing statement...");
        statement = connection.createStatement();

        String sql;
        sql = "SELECT * FROM developers";

        ResultSet resultSet = statement.executeQuery(sql);

        System.out.println("Retrieving data from database...");
        System.out.println("\n[table]Developers:");


//        printResults(resultSet);

//        printDevsBySpecialty("C#", connection);

//        System.out.println(getSpecByName(connection, "Olya"));

//        setSalaryToSpec(connection, "C#", 100);




        System.out.println("\nClosing connection and releasing resources...");
        resultSet.close();
        statement.close();
        connection.close();
    }

    private static void setSalaryToSpec(Connection connection, String spec, int i) throws SQLException {
        String query = "update developers set salary = ? where specialty like ?";
        ResultSet result = null;

        connection.setAutoCommit(false);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, i);
        ps.setString(2, spec);

        ps.executeUpdate();
        if ("".equals(spec)) connection.rollback();
        else {
            result = ps.executeQuery("select * from developers");
            connection.commit();
        }
//        printResults(result);

    }

    /*private static void printResults(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            System.out.println("\n! NULL RESULTSET !");
            return;
        }

        System.out.println("\n#==================#");
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
    }*/

    /* using prepared statement*/
    private static void printDevsBySpecialty(String specialty, Connection connection) throws SQLException {
        ResultSet result = null;

        String query = "select * from developers where specialty like ?";
        PreparedStatement ps = connection.prepareStatement(query, ResultSet.CONCUR_UPDATABLE);
        ps.setString(1, specialty);

        result = ps.executeQuery();

        /*result.first();
        int sal = result.getInt("salary");
        result.updateInt("salary", sal + 100);
        result.updateRow();
        System.out.println("1st record was updated: +100 salary");*/

//        printResults(result);
    }

    private static String getSpecByName(Connection connection, String name) throws SQLException {
        String SQL = "{? = call getDeveloperSpec(?)}";
        CallableStatement callableStatement = connection.prepareCall(SQL);

        callableStatement.setString(2, name);

        callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
        callableStatement.execute();

        return callableStatement.getString(1);

    }

}
