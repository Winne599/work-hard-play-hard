package sg.lwx.work.model.request;

import lombok.Data;

import java.util.List;

@Data
public class GetColdWalletTxInfoReqeust {

    /**
     * 要监控的 地址
     */
    private String walletAddress;
    /**
     * 要监控的合约地址
     */
    private List<String> contractAddressList;
}
