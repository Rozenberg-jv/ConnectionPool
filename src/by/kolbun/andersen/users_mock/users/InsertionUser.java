package by.kolbun.andersen.users_mock.users;

import by.kolbun.andersen.users_mock.UserThreadsExample;

import java.sql.SQLException;

public class InsertionUser extends AUser {

    public InsertionUser(String name, int delay) {
        super(name, delay);
    }

    int result;

    @Override
    public void run() {
        while (!UserThreadsExample.stop) {
            try {
                myConnect = pool.retrieve();
                st = myConnect.createStatement();
                String query = "insert into developers (name, specialty, salary) values ('newName', 'C++', 101)";
                result = st.executeUpdate(query);

                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\n > " + getName() + " > use connection: " + Integer.toHexString(myConnect.hashCode())
                        + " > use pool: " + Integer.toHexString(pool.hashCode()));
//                System.out.println(" > " + getName() + " > use pool: " + Integer.toHexString(pool.hashCode())); //
                System.out.println(result + " rows affected");
                if (result != 0) UserThreadsExample.creatInc();
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
