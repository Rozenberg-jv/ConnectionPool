package by.kolbun.andersen.users_mock;

import by.kolbun.andersen.pool_example.ConnectionPool;

public class ConnsCountDaemon extends Thread {
    ConnectionPool pool;

    public ConnsCountDaemon() {
        setDaemon(true);
        pool = ConnectionPool.getInstance();
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(500);
                System.out.println("\n\t > pool daemon > Connections free: " + pool.getAvailableCount() + ", used: " + pool.getUsedCount() + "");
                System.out.println("\t > pool daemon > use pool: " + Integer.toHexString(pool.hashCode()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
