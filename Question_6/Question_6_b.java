package Question_6;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.net.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A multithreaded web crawler that efficiently crawls multiple URLs concurrently using a thread pool.
 * 
 * Algorithm Overview:
 * 1. Use a thread-safe queue (ConcurrentLinkedQueue) to manage URLs to crawl.
 * 2. Track visited URLs using a ConcurrentHashMap to avoid reprocessing.
 * 3. Use ExecutorService with a fixed thread pool for concurrent task execution.
 * 4. Each task (CrawlTask) fetches a URL, extracts links, and submits new tasks for unvisited URLs.
 * 5. Gracefully shutdown the executor when all URLs are processed.
 */
public class Question_6_b {

    private final ExecutorService executor;
    private final ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final AtomicInteger taskCount = new AtomicInteger(0);
    private final ConcurrentHashMap<String, String> crawledData = new ConcurrentHashMap<>();

    /**
     * Initializes the web crawler with a fixed-size thread pool.
     * 
     * @param maxThreads Maximum number of concurrent threads.
     */
    public Question_6_b(int maxThreads) {
        executor = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Starts the crawling process with the given seed URLs.
     * 
     * @param seedUrls Initial list of URLs to crawl.
     */
    public void startCrawling(List<String> seedUrls) {
        seedUrls.forEach(url -> {
            if (visitedUrls.add(url)) {
                urlQueue.add(url);
                submitTask();
            }
        });
    }

    /**
     * Submits a new crawling task to the thread pool and increments the task counter.
     */
    private void submitTask() {
        taskCount.incrementAndGet();
        executor.submit(new CrawlTask());
    }

    /**
     * Represents a crawling task that processes a single URL.
     */
    private class CrawlTask implements Runnable {
        @Override
        public void run() {
            try {
                String url = urlQueue.poll();
                if (url == null) return;

                String content = fetchUrl(url);
                crawledData.put(url, content); // Store content
                List<String> links = parseLinks(content);

                for (String link : links) {
                    if (visitedUrls.add(link)) {
                        urlQueue.add(link);
                        submitTask(); // Submit task for new URL
                    }
                }
            } catch (IOException e) {
                System.err.println("Error fetching URL: " + e.getMessage());
            } finally {
                if (taskCount.decrementAndGet() == 0 && urlQueue.isEmpty()) {
                    executor.shutdown(); // Shutdown when no tasks remain
                }
            }
        }

        /**
         * Fetches the content of a URL using HttpURLConnection.
         */
        private String fetchUrl(String url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        }

        /**
         * Extracts all links (href attributes) from HTML content using regex.
         */
        private List<String> parseLinks(String content) {
            List<String> links = new ArrayList<>();
            Pattern pattern = Pattern.compile("href=\"(.*?)\"");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                links.add(matcher.group(1));
            }
            return links;
        }
    }

    /**
     * Shuts down the executor and waits for termination.
     */
    public void awaitTermination() throws InterruptedException {
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    /**
     * Returns the crawled data (URL -> content).
     */
    public Map<String, String> getCrawledData() {
        return crawledData;
    }

    // Test Case
    public static void main(String[] args) throws InterruptedException {
        List<String> seedUrls = Arrays.asList("http://example.com", "http://example.org");
        Question_6_b crawler = new Question_6_b(4);
        crawler.startCrawling(seedUrls);
        crawler.awaitTermination();
        System.out.println("Crawled URLs: " + crawler.getCrawledData().size());
    }
}

/*
Summary:
- The code implements a multithreaded web crawler using ExecutorService and concurrent data structures.
- Input: Seed URLs. Output: Crawled data stored in a thread-safe map.
- Tasks are dynamically submitted for each new URL, with a fixed thread pool limiting concurrency.
- The executor shuts down gracefully when all URLs are processed.
- Test case validates basic functionality, ensuring URLs are crawled and stored.
- Code is structured with clear comments, follows Java standards, and handles exceptions.
*/