package com.xiaov.tiop2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * @author xiaov_li
 * @create 2020-05-25 7:50 上午
 * 基于t-io的区块链P2P网络平台的客户端Handler
 */
public class BlockChainClientAioHandler implements ClientAioHandler {
    //日志记录
    private static Logger logger = LoggerFactory.getLogger(BlockChainClientAioHandler.class);

    private static BlockPacket heartbeatPacket = new BlockPacket();

    public Packet heartbeatPacket(ChannelContext channelContext) {
        return heartbeatPacket;
    }


    public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {

        if(readableLength < BlockPacket.HEADER_LENGTH){
            return null;
        }

        //读取消息体的长度
        int bodyLength = buffer.getInt();

        //数据不正确，则抛出AioDecodeException异常
        if(bodyLength < 0){
            throw new AioDecodeException("bodyLength["+bodyLength+"] is not right,remote:"+channelContext.getClientNode());
        }

        //计算本次需要的数据长度
        int neededLength = BlockPacket.HEADER_LENGTH + bodyLength;
        //收到的数据是否足够组包
        int isDataEnough = readableLength - neededLength;
        //不够消息体长度（剩下的buffer组不了消息体）
        if (isDataEnough < 0){
            return null;
        }else { //组包成功
            BlockPacket imPacket = new BlockPacket();
            if (bodyLength >0){
                byte[] dst = new byte[bodyLength];
                buffer.get(dst);
                imPacket.setBody(dst);
            }
            return imPacket;
        }
    }

    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        BlockPacket helloPacket = (BlockPacket) packet;
        byte[] body = helloPacket.getBody();
        int bodyLen = 0;
        if (body != null){
            bodyLen = body.length;
        }

        //bytebuffer的总长度 = 消息头的长度 + 消息体的长度
        int allLen = BlockPacket.HEADER_LENGTH + bodyLen;
        //创建一个新的bufferbyte
        ByteBuffer buffer = ByteBuffer.allocate(allLen);
        //设置字节序
        buffer.order(tioConfig.getByteOrder());

        //写入消息头，消息头的内容就是消息体的长度
        buffer.putInt(bodyLen);
        if (body != null){
            buffer.put(body);
        }
        return buffer;
    }

    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        BlockPacket helloPacket = (BlockPacket) packet;
        byte[] body = helloPacket.getBody();
        if (body != null){
            String str = new String(body,BlockPacket.CHARSET);
            logger.info("客户端收到消息:"+str);

        }
        return;
    }
}
