package javafxapplication1;

import java.io.Serializable;

public class LogRecord implements Serializable, Comparable<LogRecord> {
    private String date;
    private String time; 
    private long timestamp;
    private String IPAddress;
    private String Username;
    private String role;
    private String url;
    private String description;

    // Constructor
    public LogRecord(String date, String time, long timestamp, String IPAddress, String username, String role, String url, String description) {
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.IPAddress = IPAddress;
        this.Username = username;
        this.role = role;
        this.url = url;
        this.description = description;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public String getUsername() {
        return Username;
    }

    public String getRole() {
        return role;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(LogRecord other) {
        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%d-%s-%s-%s-%s-%s",
                date,
                time,
                timestamp,
                IPAddress != null ? IPAddress : "NA",
                		Username != null ? Username : "anonymous",
                				Username != null ? role : "NA",
                url,
                description != null ? description : "");
    }

   
}