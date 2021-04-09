package model.user;

import java.time.LocalDateTime;

public class TwoFaToken {
    private long userId;
    private String token;
    private LocalDateTime expiryTime;
    private String externalUserId;

    public TwoFaToken(long userId, String token, LocalDateTime expiryTime, String externalUserId) {
        this.userId = userId;
        this.token = token;
        this.expiryTime = expiryTime;
        this.externalUserId = externalUserId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }
}
