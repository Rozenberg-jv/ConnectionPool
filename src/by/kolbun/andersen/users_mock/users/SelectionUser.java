package by.kolbun.andersen.users_mock.users;

import by.kolbun.andersen.pool_example.ManualDevelopersDao;
import by.kolbun.andersen.users_mock.UserThreadsExample;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectionUser extends AUser {

    ResultSet result = null;

    public SelectionUser(String name, int delay) {
        super(name, delay);
    }

    @Override
    public void run() {
        while (!UserThreadsExample.stop) {
            try {
                myConnect = pool.retrieve();
                st = myConnect.createStatement();
                String query = "select * from developers";
                result = st.executeQuery(query);

                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\n > " + getName() + " > use connection: " + Integer.toHexString(myConnect.hashCode())
                        + " > use pool: " + Integer.toHexString(pool.hashCode()));
//                System.out.println(" > " + getName() + " > use pool: " + Integer.toHexString(pool.hashCode())); //
                ManualDevelopersDao.printFullTable(result);
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    pool.putback(myConnect);
                    if (result != null) {
                        result.close();
                    }
                    if (st != null) st.close();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(getName() + " finish ###");
    }
}
