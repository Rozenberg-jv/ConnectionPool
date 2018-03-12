package by.kolbun.andersen.pool_example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

/**
 * 1 вариант:
 * - ConnectionPool.getInstance().initValues(url, driver, user, pass, connections)
 * - getInstance()
 * 2 вариант:
 * - ConnectionPool.initProp(prop_file)
 * - getInstance();
 */

public class ConnectionPool {
    private String url;
    private String user;
    private String pass;
    private Stack<Connection> availableConns = new Stack<>();
    private Stack<Connection> usedConns = new Stack<>();
//    private static String prop_file;

    private static volatile ConnectionPool INSTANCE;

    public static ConnectionPool getInstance() {
        if (INSTANCE == null)
            synchronized (ConnectionPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionPool();
                }
                // проблема: совместить синглтон и приватный конструктор с параметрами извне
            }
        return INSTANCE;
    }

    private ConnectionPool() {
    }

    public void initValues(String prop_file) throws FileNotFoundException {
        try (BufferedReader rdr = new BufferedReader(new FileReader(prop_file))) {
            this.url = rdr.readLine().substring(4);
            try {
                Class.forName(rdr.readLine().split(":")[1]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.user = rdr.readLine().split(":")[1];
            this.pass = rdr.readLine().split(":")[1];
            int connections = Integer.parseInt(rdr.readLine().split(":")[1]);
            for (int i = 0; i < connections; i++)
                availableConns.add(getNewConnection());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void initValues(String url, String driver, String user, String pass, int connections) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        this.url = url;
        this.user = user;
        this.pass = pass;
        for (int i = 0; i < connections; i++) {
            availableConns.add(getNewConnection());
        }
    }

    private Connection getNewConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public synchronized Connection retrieve() throws SQLException, InterruptedException {
        Connection newConn = null;
        if (getAvailableCount() == 0) {
            while (getAvailableCount() == 0) {
                System.out.println("# WAIT # ");
                wait();
            }
            System.out.println("# WAIT OUT #");
        }
        newConn = availableConns.pop();
        usedConns.add(newConn);
        return newConn;
    }
    /*public synchronized Connection retrieve() throws SQLException, InterruptedException {
        Connection newConn = null;
        if (availableConns.size() == 0) {
            newConn = getNewConnection();
        } else {
            newConn = availableConns.pop();
        }
        usedConns.add(newConn);
        return newConn;
    }*/

    public synchronized void putback(Connection c) throws IllegalArgumentException, InterruptedException {
        if (c != null) {
            if (usedConns.remove(c)) {
                availableConns.add(c);
                notify();
            } else {
                throw new IllegalArgumentException("Connection is not in the usedConns");
            }
        }
    }

    public int getAvailableCount() {
        return availableConns.size();
    }

    public int getUsedCount() {
        return usedConns.size();
    }
}