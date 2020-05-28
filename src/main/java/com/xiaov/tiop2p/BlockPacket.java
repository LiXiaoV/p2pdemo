package com.xiaov.tiop2p;

import org.tio.core.intf.Packet;

/**
 * @author xiaov_li
 * @create 2020-05-25 6:03 上午
 * 区块链底层定制的packet
 */
public class BlockPacket extends Packet {
    //网络传输需序列化，这里采用Java自带序列化方式
    public static final long serialVersionUID = -172060606924066412L;
    //消息头的长度
    public static final int HEADER_LENGTH = 4;
    //字符编码类型
    public static final String CHARSET = "utf-8";
    //传输内容的字节
    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
