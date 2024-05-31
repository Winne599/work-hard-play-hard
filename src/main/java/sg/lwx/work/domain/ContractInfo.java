package sg.lwx.work.domain;

import lombok.Data;

@Data
public class ContractInfo {

    private Integer id;

    private String contractAddress;

    private String tokenSymbol;

    private String tokenName;

    private Integer tokenDecimals;

    private String protocolType;

    private String marketStatus;

    private String network;

}
