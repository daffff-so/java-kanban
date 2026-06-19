package http;

public enum HttpStatus {
    OK(200),
    CREATED(201),
    NOT_FOUND(404),
    NOT_ACCEPTABLE(406),
    INTERNAL_ERROR(500);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}