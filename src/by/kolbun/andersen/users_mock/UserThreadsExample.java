package by.kolbun.andersen.users_mock;

import by.kolbun.andersen.pool_example.ConnectionPool;
import by.kolbun.andersen.users_mock.users.SelectionUser;
import by.kolbun.andersen.users_mock.users.DeletionUser;
import by.kolbun.andersen.users_mock.users.InsertionUser;
import by.kolbun.andersen.users_mock.users.UpdateUser;

import java.io.FileNotFoundException;

public class UserThreadsExample {

    private final int time;
    public static boolean stop = false;

    private static int statCreate = 0;
    private static int statDelete = 0;
    private static int statUpdate = 0;

    public static void creatInc() {
        statCreate++;
    }

    public static void deletInc() {
        statDelete++;
    }

    public static void updatInc() {
        statUpdate++;
    }

    private final String url = "jdbc:mysql://localhost/andersen_jdbc";
    private final String driver = "com.mysql.jdbc.Driver";
    private final String user = "root";
    private final String pass = "root";
    private final int maxConnCount = 3;

    public UserThreadsExample(int time) {
        this.time = time;
    }

    public void execExample() {

        // 1
//        ConnectionPool.getInstance().initValues(url, driver, user, pass, maxConnCount);

        // 2
        try {
            ConnectionPool.getInstance().initValues("jdbc_prop.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        new ConnsCountDaemon();

        Thread t1 = new Thread(new SelectionUser("selectUser", 1000));
        Thread t2 = new Thread(new InsertionUser("insertUser", 1100));
        Thread t3 = new Thread(new UpdateUser("updateUser", 980));
        Thread t4 = new Thread(new DeletionUser("deleteUser", 700));
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stop = true;
        }

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("\n\n\t >> Conclusion:");
        System.out.println("\t || rows created: " + statCreate);
        System.out.println("\t || rows deleted: " + statDelete);
        System.out.println("\t || rows updated: " + statUpdate);

    }
}
