package by.kolbun.andersen.users_mock.users;

import by.kolbun.andersen.pool_example.ConnectionPool;

import java.sql.Connection;
import java.sql.Statement;

public abstract class AUser implements Runnable {
    ConnectionPool pool;
    Connection myConnect;
    Statement st;

    private String name;
    private int delay;

    public AUser(String name, int delay) {
        this.name = name;
        this.delay = delay;
        pool = ConnectionPool.getInstance();
    }

    public String getName() {
        return name;
    }

    public int getDelay() {
        return delay;
    }
}
