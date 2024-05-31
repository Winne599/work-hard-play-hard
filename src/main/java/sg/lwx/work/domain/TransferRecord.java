package sg.lwx.work.domain;

import groovy.lang.DelegatesTo;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author lianwenxiu
 */
@Data
public class TransferRecord {

    private Integer id;
    private String from;
    private String to;
    private BigDecimal amount;
    private BigInteger nonce;
    private String contractAddress;
    private BigInteger gasLimit;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private String transactionHash;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
