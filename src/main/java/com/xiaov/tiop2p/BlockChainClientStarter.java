package com.xiaov.tiop2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author xiaov_li
 * @create 2020-05-25 8:09 上午
 */
public class BlockChainClientStarter {
    //日志记录
    private static Logger logger = LoggerFactory.getLogger(BlockChainClientStarter.class);

    //服务端节点
    private Node serverNode;

    //handler,包括编码、解码、处理消息
    private ClientAioHandler clientAioHandler;

    //事件监听器，可以为null,但建议自己实现该接口，可以参考showcase了解接口
    private ClientAioListener clientAioListener = null;

    //断链后自动连接，若不想自动连接，请设为null
    private ReconnConf reconnConf = new ReconnConf(5000L);

    //一组连接公用的上下文对象
    private ClientTioConfig clientTioConfig;

    private TioClient tioClient = null;
//    private ArrayList<Node> nodes = new ArrayList<Node>();
    private ClientChannelContext clientChannelContext = null;

    public void start(){
        try {
            logger.info("客户端即将启动...");
            //初始化
            clientAioHandler = new BlockChainClientAioHandler();
            clientTioConfig = new ClientTioConfig(clientAioHandler,clientAioListener,reconnConf);

            clientTioConfig.setHeartbeatTimeout(Const.TIMEOUT);
            tioClient = new TioClient(clientTioConfig);

            logger.info("客户端启动完毕");
            //连上后，发消息测试

            logger.info("客户端开始向服务器发送消息...");
            sendMessage();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void sendMessage() throws Exception {
        BlockPacket packet = new BlockPacket();
        while (true){
            Scanner in = new Scanner(System.in);
            System.out.println("请输入所发送消息的服务器IP：");
            String serverIP = in.nextLine();
            System.out.println("请输入所发送消息的服务器端口：");
            int serverPort = in.nextInt();
            in.nextLine();
            System.out.println("请输入您想要发送的消息：");
            String message = in.nextLine();

            serverNode = new Node(serverIP,serverPort);
//            boolean flag = nodes.contains(serverNode);
//            if(!flag){
//                nodes.add(serverNode);
//            }
//            System.out.println("flag:"+false);
            clientChannelContext = tioClient.connect(serverNode);

            packet.setBody(message.getBytes(BlockPacket.CHARSET));
            Tio.send(clientChannelContext,packet);
        }

    }
}
