package org.hints.im.client;

/**
 * Created by hints on 2022/5/9 18:10
 */

public class Client3 {
    public static void main(String[] args) {
        try {
            new NettyClient(10086, "localhost", "hints");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
