import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        // Создаём пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(25);

        // Создаём список для хранения объектов Future
        // Объект Future можно использовать для проверки состояния задачи
        // и получения результата после завершения задачи.
        List<Future<Integer>> futures = new ArrayList<>();

        // Перебираем массив текстов
        for (String text : texts) {
            // Отправляем задачу в пул потоков для выполнения.
            // В метод submit() с помощю лямбда-выражении передаём функцию, возвращающую результат выполняемой задачи.
            Future<Integer> future = executor.submit(() -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            });
            // Добавляем объект Future в список
            futures.add(future);
        }

        // Перебираем список объектов Future. Метод get()класса Future
        // используется для получения результата задачи, которая была отправлена ExecutorService на выполнение.
        // Метод блокируется до тех пор, пока задача не завершится и не вернет результат задачи.
        // В этом случае метод get() вызывается для Future<Integer> объекта, поэтому он вернет Integer значение,
        // результатом котрого является максимальный размер подстроки без 'b'.
        int maxRange = 0;
        for (Future<Integer> future : futures) {
            int range = future.get();
            maxRange = Math.max(maxRange, range);
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Max Range: " + maxRange);

        // Закрываем пул потоков.
        executor.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
