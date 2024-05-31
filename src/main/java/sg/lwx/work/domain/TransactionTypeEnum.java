package sg.lwx.work.domain;

/**
 * @author lianwenxiu
 */

public enum TransactionTypeEnum {
    /**
     * 发送
     */
    SEND(Byte.valueOf("1"), "Send"),
    /**
     * 接收
     */
    RECEIVE(Byte.valueOf("2"), "Receive"),
    ;

    /**
     *
     */
    private Byte id;
    /**
     *
     */
    private String name;

    TransactionTypeEnum(Byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public Byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}