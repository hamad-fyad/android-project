package com.example.myapplication.Utilitys;

import java.util.*;

public class TypoFixer {
    private List<String> dictionary;
    private Map<List<String>, Integer> distanceCache;

    public TypoFixer(List<String> dictionary) {
        this.dictionary = new ArrayList<>(dictionary);
        this.distanceCache = new HashMap<>();
    }

    public String fixTypos(String text) {
        String[] words = text.split(" ");
        List<String> correctedWords = new ArrayList<>();

        for (String word : words) {
            if (word.matches("-?\\d+(\\.\\d+)?")) {
                correctedWords.add(word);
            } else if (!dictionary.contains(word.toLowerCase())) {
                String suggestedCorrection = getCorrection(word);
                correctedWords.add(suggestedCorrection);
            } else {
                correctedWords.add(word);
            }
        }

        return String.join(" ", correctedWords);
    }

    private String getCorrection(String word) {
        String suggestedCorrection = word;
        int minDistance = Integer.MAX_VALUE;
        for (String dictWord : dictionary) {
            int distance = calculateLevenshteinDistance(word, dictWord);
            if (distance < minDistance) {
                minDistance = distance;
                suggestedCorrection = dictWord;
            }
        }

        return suggestedCorrection;
    }

    private int calculateLevenshteinDistance(String word1, String word2) {
        // Use previously calculated value if available
        if (distanceCache.containsKey(Arrays.asList(word1, word2))) {
            return distanceCache.get(Arrays.asList(word1, word2));
        }

        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        // Store result in cache
        distanceCache.put(Arrays.asList(word1, word2), dp[m][n]);

        return dp[m][n];
    }
}
