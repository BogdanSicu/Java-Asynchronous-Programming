import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SynchronizerTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        runAsyncAnyOf(); // -> anyOf() is just waiting for any of the completableFutures to finish, not for all of them
        // allOf is waiting for all of them to finish

        runAsyncAnyOfCaveat();
    }

    private static void runAsyncAnyOf() {
        Supplier<Weather> weatherA = () -> {
            try {
                Thread.sleep(new Random().nextInt(450,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-a");
        };

        Supplier<Weather> weatherB = () -> {
            try {
                Thread.sleep(new Random().nextInt(450,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-b");
        };

        Supplier<Weather> weatherC = () -> {
            try {
                Thread.sleep(new Random().nextInt(450,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-c");
        };

        Supplier<Weather> weatherD = () -> {
            try {
                Thread.sleep(new Random().nextInt(450,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-d");
        };

        Supplier<Weather> weatherE = () -> {
            try {
                Thread.sleep(new Random().nextInt(450,500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-e");
        };

        var taskList = List.of(weatherA, weatherB, weatherC, weatherD, weatherE);

        List<CompletableFuture<Weather>> weatherList = new ArrayList<>();
        for(Supplier<Weather> task : taskList) {
            var future = CompletableFuture.supplyAsync(task);
            weatherList.add(future);
        }

        var result =
                CompletableFuture.anyOf(weatherList.toArray(CompletableFuture[]::new));

        result.thenAccept(System.out::println).join();
    }

    private static void runAsyncAnyOfCaveat() {
        System.out.println("\n-------------proving concept---------------");

        Supplier<Weather> weatherA = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-a");
        };

        Supplier<Weather> weatherB = () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("partly-sunny", "server-b");
        };

        var futureA =  CompletableFuture.supplyAsync(weatherA);
        var futureB =  CompletableFuture.supplyAsync(weatherB);

        var future =  CompletableFuture.anyOf(futureA, futureB);

        future.thenAccept(s -> {
            System.out.println(futureA);
            System.out.println(futureB);
            System.out.println(future.join());
        }).join(); // force the main thread to wait
    }
}