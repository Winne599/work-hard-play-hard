package sg.lwx.work.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author lianwenxiu
 */
@Data
public class Credentials {
    private Integer id;
    private String address;
    private String privateKey;
    private LocalDateTime createTime;

}


