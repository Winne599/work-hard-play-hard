package sg.lwx.work.domain;

/**
 * @author lianwenxiu
 */
public enum TransactionStatusEnum {

    /**
     * 已上链，已被监控到
     */
    ON_CHAIN(Byte.valueOf("2"), "ON_Chain"),
    /**
     * 达到确认快数量
     */
    CONFIRM(Byte.valueOf("3"), "Confirm"),
    /**
     * 失败
     */
    FAILED(Byte.valueOf("4"), "Failed"),
    ;

    private Byte id;
    private String name;

    TransactionStatusEnum(Byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public Byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * 通过id 拿 name
     * @param id
     * @return
     */
    public static String getNameById(Byte id){
        for (TransactionStatusEnum statusEnum : TransactionStatusEnum.values()){
            if (id.equals(statusEnum.getId())){
                return statusEnum.getName();
            }
        }
        return null;
    }
}

