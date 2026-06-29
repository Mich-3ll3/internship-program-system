package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String message;
    private LocalDateTime creationDate;
    private Boolean isRead;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer userId, String message) {
        this.userId = userId;
        this.message = message;
        this.creationDate = LocalDateTime.now();
        this.isRead = false;
    }

    public NotificationDTO(Integer id, Integer userId, String message, LocalDateTime creationDate, Boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.creationDate = creationDate;
        this.isRead = isRead;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
