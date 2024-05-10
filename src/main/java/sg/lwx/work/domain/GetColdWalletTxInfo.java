package sg.lwx.work.domain;

import lombok.Data;

import java.math.BigInteger;

@Data
public class GetColdWalletTxInfo {

    /**
     * type = 1 send
     * type = 2 received
     */
    private  Integer type;

    private BigInteger amount;




}
