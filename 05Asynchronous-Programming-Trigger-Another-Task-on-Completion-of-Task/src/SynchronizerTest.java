import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SynchronizerTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        runSynchronously();

        runConcurrently();

        runAsynchronously();
    }

    private static void runAsynchronously() throws InterruptedException {

        System.out.println("---------------Asynchronously------------------");

        Supplier<Quotation> fetchQuotationA = () -> {
            try {
                Thread.sleep(new Random().nextInt(450, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation(new Random().nextInt(1, 100),"server-A");
        };

        Supplier<Quotation> fetchQuotationB = () -> {
            try {
                Thread.sleep(new Random().nextInt(450, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation(new Random().nextInt(1, 100),"server-B");
        };

        Supplier<Quotation> fetchQuotationC = () -> {
            try {
                Thread.sleep(new Random().nextInt(450, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation(new Random().nextInt(1, 100),"server-C");
        };

        Supplier<Quotation> fetchQuotationD = () -> {
            try {
                Thread.sleep(new Random().nextInt(450, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation(new Random().nextInt(1, 100),"server-D");
        };

        Supplier<Quotation> fetchQuotationE = () -> {
            try {
                Thread.sleep(new Random().nextInt(450, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation(new Random().nextInt(1, 100),"server-E");
        };

        var taskList = List.of(fetchQuotationA,
                fetchQuotationB,
                fetchQuotationC,
                fetchQuotationD,
                fetchQuotationE);

        var startInstant = Instant.now();

        var futures = new ArrayList<CompletableFuture<Quotation>>();
        var quotations = new ConcurrentLinkedDeque<Quotation>();

        for (Supplier<Quotation> task : taskList) {
            futures.add(CompletableFuture.supplyAsync(task));
        }

        List<CompletableFuture<Void>> voids = new ArrayList<>();

        for (CompletableFuture<Quotation> completableFuture : futures) {
            voids.add(completableFuture.thenAccept(
                        quotations::add
                    ));
        }

        for(CompletableFuture<Void> r : voids) {
            r.join(); // blocking - just to make the main thread to wait for the others to finish
        }

        var bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::value)).orElseThrow();

        var endInstant = Instant.now();

        System.out.println("Best Quotation is [" + bestQuotation.value() + "]"
                + "\nBest server is [" + bestQuotation.description() + "]"
                + "\n(millis) " + Duration.between(endInstant, startInstant).toMillis());

    }

    private static void runConcurrently() throws ExecutionException, InterruptedException {

        System.out.println("---------------Concurrently------------------");

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-A");
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-B");
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-C");
        };

        Callable<Quotation> fetchQuotationD = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-D");
        };

        Callable<Quotation> fetchQuotationE = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-E");
        };

        var taskList = List.of(fetchQuotationA,
                fetchQuotationB,
                fetchQuotationC,
                fetchQuotationD,
                fetchQuotationE);

        var executorService = Executors.newFixedThreadPool(6);
        var futures = new ArrayList<Future<Quotation>>();
        var quotations = new ArrayList<Quotation>();

        var startInstant = Instant.now();

        for(Callable<Quotation> task : taskList) {
            futures.add(executorService.submit(task));
        }

        for(Future<Quotation> future: futures) {
            quotations.add(future.get());
        }

        var bestQuotation = quotations.stream().min(Comparator.comparing(Quotation::value))
                .orElseThrow();

        var endInstant = Instant.now();

        System.out.println("Best Quotation is [" + bestQuotation.value() + "]"
                + "\nBest server is [" + bestQuotation.description() + "]"
                + "\n(millis) " + Duration.between(endInstant, startInstant).toMillis());

        executorService.shutdown();

    }

    private static void runSynchronously() {
        System.out.println("--------------Synchronously------------------");

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-A");
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-B");
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-C");
        };

        Callable<Quotation> fetchQuotationD = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-D");
        };

        Callable<Quotation> fetchQuotationE = () -> {
            Thread.sleep(new Random().nextInt(450, 500));
            return new Quotation(new Random().nextInt(1, 100),"server-E");
        };

        var startInstant = Instant.now();

        var taskList = List.of(fetchQuotationA,
                fetchQuotationB,
                fetchQuotationC,
                fetchQuotationD,
                fetchQuotationE);

        Quotation bestQuotation = taskList.stream().map(task -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).min(Comparator.comparing(Quotation::value))
                .orElseThrow();

        var endInstant = Instant.now();

        System.out.println("Best Quotation is [" + bestQuotation.value() + "]"
            + "\nBest server is [" + bestQuotation.description() + "]"
            + "\n(millis) " + Duration.between(endInstant, startInstant).toMillis());

    }
}