import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Asynchronous {
    public static void main(String[] args) throws InterruptedException {

        var task1Future = CompletableFuture.supplyAsync(Asynchronous::insokeTask1);

        var task2Future = CompletableFuture.supplyAsync(Asynchronous::insokeTask2);

        CompletableFuture.allOf(task1Future, task2Future)
                .thenAccept(x -> System.out.println(task1Future.join() + ":" + task2Future.join()));

        sleep(5000);
    }

    private static String insokeTask1() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "tom";
    }

    private static String insokeTask2() {
        try {
            sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "ryan";

    }

}
