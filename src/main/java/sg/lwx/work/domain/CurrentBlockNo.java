package sg.lwx.work.domain;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class CurrentBlockNo {

    private Integer id;
    private BigInteger blockNo;
    private String type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
