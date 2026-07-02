import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Concurrent {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var service = Executors.newFixedThreadPool(2);

        var futureForTask1 = service.submit(Concurrent::invokeTask1);

        var futureForTask2 = service.submit(Concurrent::invokeTask2);

        var task1Result = futureForTask1.get(); // call is blocking

        var task2Result = futureForTask2.get(); // call is blocking

        System.out.println("--------Main Thread---------");

        service.shutdown();
    }

    private static String invokeTask1() throws InterruptedException{
        for(int i=0; i<10; i++) {
            System.out.println("task 1: " + i);
        }
        return "tom";
    }

    private static String invokeTask2() throws InterruptedException {
        for(int i=0; i<10; i++) {
            System.out.println("task 2: " + i);
        }
        return "ryan";
    }
}