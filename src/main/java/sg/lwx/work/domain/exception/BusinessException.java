package sg.lwx.work.domain.exception;

import java.util.UUID;

/**
 * @author lianwenxiu
 */
public class BusinessException extends Throwable {
    private Integer code;
    private String message;
    private String id;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.id = UUID.randomUUID().toString();
    }

    public BusinessException(String message) {
        this.message = message;
        this.id = UUID.randomUUID().toString();
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

