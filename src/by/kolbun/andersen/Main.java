package by.kolbun.andersen;


import by.kolbun.andersen.pool_example.CrudJdbcExample;
import by.kolbun.andersen.users_mock.UserThreadsExample;

public class Main {

    public static void main(String[] args) {

        // example with CRUD app
        /*CrudJdbcExample example1 = new CrudJdbcExample();
        example1.execExample();*/

        // example with mocking several users threads
        /**
         * На вход подается время таймера, после окончания которого все потоки выполняют итерацию до конца и отключаются
         */
        UserThreadsExample example2 = new UserThreadsExample(6000);
        example2.execExample();


    }
}
