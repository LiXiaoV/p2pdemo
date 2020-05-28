package com.xiaov.tiop2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;


/**
 * @author xiaov_licd ..
 * @create 2020-05-25 7:13 上午
 * 基于t-io的区块链底层P2P网络平台的服务端
 */
public class BlockChainServerStarter {
    //日志记录
    private static Logger logger = LoggerFactory.getLogger(BlockChainServerStarter.class);

    //handler,包括编码、解码、消息处理
    public static ServerAioHandler aioHandler = new BlockChainServerAioHandler();

    //事件监听器，可以为null,但建议自己实现该接口，可以参考showcase了解接口
    public static ServerAioListener aioListener = null;

    //一组连接公用的上下文对象
    public static ServerTioConfig  serverTioConfig = new ServerTioConfig("hello-tio-server",aioHandler,aioListener);

    //tioServer对象
    public static TioServer tioServer = new TioServer(serverTioConfig);

    //有时候需要绑定IP，不需要则为null
    public static String serverIP = null; //Const.SERVER

    //监听的端口
    public static int serverPort = Const.PORT;

    public void start(){
        try {
            logger.info("服务器即将启动...");

            serverTioConfig.setHeartbeatTimeout(Const.TIMEOUT);
            Scanner in = new Scanner(System.in);
            serverIP = Const.SERVER;
            System.out.println("请输入服务器端口：");
            serverPort = in.nextInt();
            tioServer.start(serverIP,serverPort);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
