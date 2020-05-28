package com.xiaov.tiop2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * @author xiaov_li
 * @create 2020-05-25 6:12 上午
 * 基于t-io的区块链的底层P2P网络平台的服务端Handler
 */
public class BlockChainServerAioHandler implements ServerAioHandler {

    //日志记录
    private static Logger logger = LoggerFactory.getLogger(BlockChainServerAioHandler.class);
    /**
     * 解码：把接收到的ByteBuffer解码成应用可以识别的业务消息包
     * 总的消息结构：消息头+消息体
     * 消息头结构：4个字节，存储消息体的长度
     * 消息体结构：对象的JSON串的byte[]
     * @param buffer
     * @param limit
     * @param position
     * @param readableLength
     * @param channelContext
     * @return
     * @throws AioDecodeException
     */
    public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
        //提醒：buffer的开始位置不一定是0，应用需要从buffer.position（）开始读取数据
        //若收到的数据无法组成业务包BlockPacket，则返回null以表示数据长度不够
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

    /**
     * 编码：把业务消息包编码为可以发送的ByteBuffer
     * 总的消息结构：消息头+消息体
     * 消息头结构：4个字节，存储消息体的长度
     * 消息体结构：对象的JSON串的byte[]
     * @param packet
     * @param tioConfig
     * @param channelContext
     * @return
     */
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

    /**
     * 处理消息
     * @param packet
     * @param channelContext
     * @throws Exception
     */
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        BlockPacket helloPacket = (BlockPacket) packet;
        byte[] body = helloPacket.getBody();
        if (body != null){
            String str = new String(body,BlockPacket.CHARSET);
            logger.info("服务端收到消息:"+str);

            BlockPacket respacket = new BlockPacket();
            respacket.setBody(("服务端收到了你的消息，你的消息是:"+str).getBytes(BlockPacket.CHARSET));
            Tio.send(channelContext,respacket);
        }
        return;
    }
}
