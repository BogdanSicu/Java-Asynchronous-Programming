import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class SynchronizerTest {

    public static void main(String[] args) {
        run();
    }

    private static void run() {

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 500));
            return new Quotation(ThreadLocalRandom.current().nextInt(1, 100),"server-A");
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 500));
            return new Quotation(ThreadLocalRandom.current().nextInt(1, 100),"server-B");
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 500));
            return new Quotation(ThreadLocalRandom.current().nextInt(1, 100),"server-C");
        };

//        Callable<Quotation> fetchQuotationD = () -> {
//            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 500));
//            return new Quotation(ThreadLocalRandom.current().nextInt(1, 100),"server-D");
//        };
//
//        Callable<Quotation> fetchQuotationE = () -> {
//            Thread.sleep(ThreadLocalRandom.current().nextInt(450, 500));
//            return new Quotation(ThreadLocalRandom.current().nextInt(1, 100),"server-E");
//        };

        var startInstant = Instant.now();

        var taskList = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);

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