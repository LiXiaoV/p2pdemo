package com.xiaov.tiop2p;

/**
 * @author xiaov_li
 * @create 2020-05-28 5:54 下午
 */
public class p2pNode {
    public static void main(String[] args) {
        BlockChainServerStarter server = new BlockChainServerStarter();
        server.start();
        BlockChainClientStarter client = new BlockChainClientStarter();
        client.start();
    }
}
