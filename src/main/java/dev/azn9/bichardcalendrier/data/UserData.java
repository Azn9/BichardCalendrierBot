package dev.azn9.bichardcalendrier.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "user_data")
public class UserData {

    @Id
    Long userId;

    Long threadId;
    int points = 0;
    boolean registered = true;

    public UserData() {
    }

    public UserData(Long userId) {
        this.userId = userId;
    }

    public UserData(long userId, long threadId) {
        this.userId = userId;
        this.threadId = threadId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
