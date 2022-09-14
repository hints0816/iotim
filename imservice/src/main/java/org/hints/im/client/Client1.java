package org.hints.im.client;

import java.util.Scanner;

/**
 * Created by hints on 2022/5/9 18:10
 */

public class Client1 {
    public static void main(String[] args) {
        try {

            Scanner scanner=new Scanner(System.in);
            System.out.println(">>用户名：");
            String userid=scanner.next();
            new NettyClient(10086, "localhost", userid);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
