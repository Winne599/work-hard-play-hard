package sg.lwx.work.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author lianwenxiu
 */
@Data
public class TransferErc20Request {

    private String from;

    private String to;

    private BigDecimal amount;

    private String contractAddress;

    private BigInteger gasLimit;

    private BigInteger maxFeePerGas;

    private BigInteger maxPriorityFeePerGas;

    /**
     * speedup 使用
     */
    private Integer id;
    private String transactionHash;


}
