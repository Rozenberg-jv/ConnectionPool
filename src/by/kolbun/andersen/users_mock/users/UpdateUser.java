package by.kolbun.andersen.users_mock.users;

import by.kolbun.andersen.pool_example.ManualDevelopersDao;
import by.kolbun.andersen.users_mock.UserThreadsExample;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateUser extends AUser {
    int result;

    public UpdateUser(String name, int delay) {
        super(name, delay);
    }

    @Override
    public void run() {
        while (!UserThreadsExample.stop) {
            try {
                myConnect = pool.retrieve();
                st = myConnect.createStatement();
//                String query = "update developers set salary = salary + 100 where specialty = 'C++' limit 1";
                String query = "update developers set salary = salary + 100 where salary < 1500 limit 1";
                result = st.executeUpdate(query);

                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\n > " + getName() + " > use connection: " + Integer.toHexString(myConnect.hashCode())
                        + " > use pool: " + Integer.toHexString(pool.hashCode()));
                System.out.println(result + " rows affected");
                if (result != 0) UserThreadsExample.updatInc();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    pool.putback(myConnect);
                    if (st != null) st.close();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(getName() + " finish ###");
    }
}
