import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DataAggregator {

    public static ProductInfo aggregateProductInfo(String productName) throws ExecutionException, InterruptedException {
        CompletableFuture<Double> priceCF = CompletableFuture.supplyAsync(DataAggregator::fetchPrice)
                .exceptionally(ex -> 0.0);
        CompletableFuture<String> descriptionCF = CompletableFuture.supplyAsync(DataAggregator::fetchDescription)
                .exceptionally(ex -> "Нет данных");
        CompletableFuture<Double> ratingCF = CompletableFuture.supplyAsync(DataAggregator::fetchRating)
                .exceptionally(ex -> 0.0);

        CompletableFuture<ProductInfo> productCF = CompletableFuture.allOf(priceCF, descriptionCF, ratingCF)
                .thenApply(v -> new ProductInfo(productName, priceCF.join(), descriptionCF.join(), ratingCF.join()));

        return productCF.get();

    }

    private static final Random rnd = new Random();
    private static double fetchPrice() {
        try {
            doRandomFetch();
            return Math.round(rnd.nextDouble(0.1, 1000) * 100.0) / 100.0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static String fetchDescription() {
        try {
            doRandomFetch();
            return "Описание товара";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static double fetchRating() {
        try {
            doRandomFetch();
            return Math.round(rnd.nextDouble(0.1, 5.01) * 100.0) / 100.0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static void doRandomFetch() throws InterruptedException {
        Thread.sleep(rnd.nextInt(1000, 3001));
        if (rnd.nextInt(0, 100) >= 80) {
            throw new RuntimeException("exception while fetch");
        }
    }
}
