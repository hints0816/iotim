package org.hints.im.client;

/**
 * Created by 180686 on 2022/5/9 18:10
 */

public class Client2 {
    public static void main(String[] args) {
        try {
            new NettyClient(10086, "localhost", "Peter");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
