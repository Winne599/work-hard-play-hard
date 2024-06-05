package sg.lwx.work.domain.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lianwenxiu
 */

public enum ChainIdEnum {
    /**
     *
     */
    SEPOLIA("sepolia",11155111L),
    /**
     *
     */
    MAINNET("mainnet",1L),
    ;

    /**
     * 网络
     */
    private String network;
    /**
     * chainId（网络标识）
     */
    private Long chainId;


    /**
     * 一定要写 有参构造方法，否则会报错!!!
     * @param network
     * @param chainId
     */
    ChainIdEnum(String network, Long chainId) {
        this.network = network;
        this.chainId = chainId;
    }


    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    /**
     * 通过 network 找 chainId
     * @param network
     * @return
     */
    public static Long getChainIdByNetwork(String network){
        if (StringUtils.isEmpty(network)){
            return null;
        }
        for (ChainIdEnum chainIdEnum: ChainIdEnum.values()){
            if (chainIdEnum.getNetwork().equals(network)){
                return chainIdEnum.getChainId();
            }
        }
        return null;
    }

}
