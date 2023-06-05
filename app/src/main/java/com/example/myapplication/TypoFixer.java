package com.example.myapplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypoFixer {
    private List<String> dictionary;


    public TypoFixer(List<String> dictionary) {
        this.dictionary = new ArrayList<>(dictionary);
    }

    public String fixTypos(String text) {
        String[] words = text.split(" ");
        List<String> correctedWords = new ArrayList<>();

        for (String word : words) {
            if (!dictionary.contains(word.toLowerCase())) {
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

        return dp[m][n];
    }
}
