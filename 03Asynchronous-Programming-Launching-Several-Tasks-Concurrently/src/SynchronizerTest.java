import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class SynchronizerTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        runSynchronously();

        runConcurrently();
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