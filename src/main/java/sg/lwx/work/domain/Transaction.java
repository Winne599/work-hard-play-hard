package sg.lwx.work.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private Integer id;

    private String network;

    private BigInteger nonce;

    private String transactionHash;

    private String fromAddress;

    private String toAddress;

    private String currencyId;

    private String currencyName;

    private String contractAddress;

    private BigDecimal balanceDelta;

    private BigDecimal amount;

    private BigDecimal gasFee;

    private Byte type;

    private Byte status;

    private BigInteger currBlockNo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}






