package Question_4;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This program identifies the top 3 trending hashtags from tweets posted in February 2024.
 * 
 * Algorithm Overview:
 * 1. Filter tweets to include only those from February 2024.
 * 2. Extract hashtags from each tweet's text.
 * 3. Count occurrences of each hashtag.
 * 4. Sort hashtags by count (descending) and name (descending) to resolve ties.
 * 5. Return the top 3 hashtags based on the sorted list.
 * 
 * Data Structures Used:
 * - HashMap: Efficiently count hashtag frequencies.
 * - Custom Comparator: Sort entries by count and hashtag name.
 */

public class Question_4_a {

    /**
     * Represents a hashtag with its occurrence count for easy result handling.
     */
    static class HashtagCount {
        String hashtag;
        int count;

        public HashtagCount(String hashtag, int count) {
            this.hashtag = hashtag;
            this.count = count;
        }

        @Override
        public String toString() {
            return hashtag + " " + count;
        }
    }

    /**
     * Main method to test the functionality with sample data.
     */
    public static void main(String[] args) {
        // Sample Input (February 2024 tweets)
        List<String[]> tweets = new ArrayList<>();
        tweets.add(new String[]{"135", "13", "2024-02-01", "Enjoying a great start to the day. #HappyDay #MorningVibes"});
        tweets.add(new String[]{"136", "14", "2024-02-03", "Another #HappyDay with good vibes! #FeelGood"});
        tweets.add(new String[]{"137", "15", "2024-02-04", "Productivity peaks! #WorkLife #ProductiveDay"});
        tweets.add(new String[]{"138", "16", "2024-02-04", "Exploring new tech frontiers. #TechLife #Innovation"});
        tweets.add(new String[]{"139", "17", "2024-02-05", "Gratitude for today's moments. #HappyDay #Thankful"});
        tweets.add(new String[]{"140", "18", "2024-02-07", "Innovation drives us. #TechLife #FutureTech"});
        tweets.add(new String[]{"141", "19", "2024-02-09", "Connecting with nature's serenity. #Nature #Peaceful"});

        // Find top 3 hashtags
        List<HashtagCount> result = findTopHashtags(tweets, 3);

        // Print formatted output
        System.out.println("+-----------+-------+");
        System.out.println("| hashtag   | count |");
        System.out.println("+-----------+-------+");
        for (HashtagCount hc : result) {
            System.out.printf("| %-10s| %-6d|%n", hc.hashtag, hc.count);
        }
        System.out.println("+-----------+-------+");
    }

    /**
     * Finds the top N trending hashtags from tweets posted in February 2024.
     * 
     * @param tweets List of tweets (each as a String array: [user_id, tweet_id, tweet_date, tweet]).
     * @param topN Number of top hashtags to return.
     * @return List of top N hashtags with counts, sorted appropriately.
     */
    public static List<HashtagCount> findTopHashtags(List<String[]> tweets, int topN) {
        // Step 1: Filter tweets to February 2024
        List<String[]> filteredTweets = tweets.stream()
                .filter(tweet -> isFebruary2024(tweet[2]))
                .collect(Collectors.toList());

        // Step 2: Extract and count hashtags
        Map<String, Integer> hashtagCounts = new HashMap<>();
        for (String[] tweet : filteredTweets) {
            String text = tweet[3];
            String[] words = text.split("\\s+"); // Split by spaces
            for (String word : words) {
                if (word.startsWith("#")) {
                    hashtagCounts.put(word, hashtagCounts.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Step 3: Sort by count (descending) and hashtag (descending)
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(hashtagCounts.entrySet());
        sortedEntries.sort((a, b) -> {
            int countCompare = b.getValue().compareTo(a.getValue()); // Descending count
            return (countCompare != 0) ? countCompare : b.getKey().compareTo(a.getKey()); // Descending hashtag if counts are equal
        });

        // Step 4: Select top N entries
        return sortedEntries.stream()
                .limit(topN)
                .map(entry -> new HashtagCount(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a date string falls within February 2024.
     * 
     * @param dateStr Date in "YYYY-MM-DD" format.
     * @return True if the date is in February 2024, False otherwise.
     */
    private static boolean isFebruary2024(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.getYear() == 2024 && date.getMonth() == Month.FEBRUARY;
        } catch (DateTimeParseException e) {
            return false; // Ignore invalid dates
        }
    }
}

/*
Summary:
- The code processes tweets to find the top 3 hashtags in February 2024.
- Input: Tweets with user IDs, tweet IDs, dates, and text.
- Output: Sorted list of hashtags with counts.
- Steps: Filtering by date, hashtag extraction, counting, sorting, and selection.
- The sample input produces the expected output, confirming correctness. The algorithm efficiently uses hashing and sorting to meet the problem requirements.
*/