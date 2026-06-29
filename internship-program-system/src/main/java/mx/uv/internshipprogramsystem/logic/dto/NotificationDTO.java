package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private int id;
    private int userId;
    private String message;
    private LocalDateTime date;
    private boolean isRead;

    public NotificationDTO() {
        this.date = LocalDateTime.now();
        this.isRead = false;
    }

    public NotificationDTO(int userId, String message) {
        this();
        this.userId = userId;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
}
