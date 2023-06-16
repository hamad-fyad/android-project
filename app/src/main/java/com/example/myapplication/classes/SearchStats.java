package com.example.myapplication.classes;
public class SearchStats {
    private String userId;
    private String searchTerm;
    private long timestamp;
    private long count;

    public SearchStats() {}

    public SearchStats(String userId, String searchTerm, long timestamp, long count) {
        this.userId = userId;
        this.searchTerm = searchTerm;
        this.timestamp = timestamp;
        this.count = count;
    }

    public SearchStats(String userId, String searchTerm, long timestamp) {
        this(userId, searchTerm, timestamp, 0);
    }

    public long getCount() {
        return count;
    }

    public String getUserId() {
        return userId;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
