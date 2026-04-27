package mx.uv.internshipprogramsystem.logic.dto;

public class DocumentDTO {
    private int id;
    private String name;
    private String type;
    private String path;

    public DocumentDTO() {
    }

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
}
