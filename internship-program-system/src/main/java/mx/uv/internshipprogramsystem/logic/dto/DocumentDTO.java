package mx.uv.internshipprogramsystem.logic.dto;

public class DocumentDTO {
    
    private int id;
    private String name;
    private String type;
    private String path;
    
    // Agregados para la compatibilidad con JavaFX TableView
    private String uploadDate;
    private String status;

    public DocumentDTO() {
    }

    // ==============================================================
    // CONSTRUCTORES ORIGINALES (Para que el DAO funcione sin errores)
    // ==============================================================
    
    public DocumentDTO(int id, String name, String type, String path) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public DocumentDTO(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    // ==============================================================
    // CONSTRUCTORES NUEVOS (Para la tabla de la interfaz gráfica)
    // ==============================================================
    
    public DocumentDTO(int id, String name, String type, String path, String uploadDate, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.uploadDate = uploadDate;
        this.status = status;
    }

    public DocumentDTO(String name, String type, String path, String uploadDate, String status) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.uploadDate = uploadDate;
        this.status = status;
    }

    // ==============================================================
    // GETTERS Y SETTERS
    // ==============================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}