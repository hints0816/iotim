package org.hints.game;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author 180686
 * @Date 2023/4/20 10:38
 */
@Slf4j
public class TestShuffle {


    @org.junit.Test
    public void setup() {

        // 是否都在线



        // 是否都准备



        Card[] cards = new Card[8];

        // 2W.2C.1P.1T.1B.1I
        Card card1 = new Card();
        card1.setCardName("WOLF");
        cards[0] = card1;
        Card card12 = new Card();
        card12.setCardName("WOLF");
        cards[1] = card12;
        Card card2 = new Card();
        card2.setCardName("PROPHET");
        cards[2] = card2;
        Card card3 = new Card();
        card3.setCardName("CIVILIAN");
        cards[3] = card3;
        Card card32 = new Card();
        card32.setCardName("CIVILIAN");
        cards[4] = card32;
        Card card4 = new Card();
        card4.setCardName("TREATERS");
        cards[5] = card4;
        Card card5 = new Card();
        card5.setCardName("BANDIT");
        cards[6] = card5;
        Card card6 = new Card();
        card6.setCardName("INSOMNIA");
        cards[7] = card6;

        Operate operate = new Operate(cards);
        operate.init();

        for (int i = 0; i < operate.getPlayerCards().length; i++) {
            System.out.println("player:"+operate.getPlayerCards()[i]);
        }

        // player index
        int index = 1;
        operate.pickUp(1);
        Player player = new Player();
        player.setName("hints");
        player.setIndex(index);

        for (int i = 0; i < operate.getPlayerCards().length; i++) {
            System.out.println("pickUp:"+operate.getPlayerCards()[i]);
        }

        // 1.WOLF
        ArrayList<Integer> integers = operate.checkWolf();
        if(integers.size()>1){
            System.out.println("your friend wolf:"+integers);
        }else if(integers.size()==1){
            int[] integers1 = new int[1];
            integers1[0] = 0;
            ArrayList<Card> cards1 = operate.checkremaindCards(integers1);
            System.out.println("you are the one; check the reminder: "+cards1.get(0).getCardName());
        }else{
            System.out.println("do nothing");
        }

        // 2.PROPHET
        // 2.1 checkPlayerCards
//        operate.checkPlayerCards(2);
        // 2.2 checkremaindCards
        int[] integers1 = new int[2];
        integers1[0] = 0;
        integers1[1] = 2;
        ArrayList<Card> cards1 = operate.checkremaindCards(integers1);
        for (int i = 0; i < cards1.size(); i++) {
            System.out.println("PROPHET check:"+cards1.get(i).getCardName());
        }
        System.out.println("--------------------");
        // 3.BANDIT
        operate.exchange(index, 2);
        for (int i = 0; i < operate.getPlayerCards().length; i++) {
            System.out.println("BANDIT:"+operate.getPlayerCards()[i].getCardName());
        }
        System.out.println("--------------------");
        // 4.TREATERS
        operate.exchange(3, 2);
        for (int i = 0; i < operate.getPlayerCards().length; i++) {
            System.out.println("TREATERS:"+operate.getPlayerCards()[i].getCardName());
        }
        System.out.println("--------------------");
        // 5.INSOMNIA
        Card card = operate.checkPlayerCards(index);
        for (int i = 0; i < operate.getPlayerCards().length; i++) {
            System.out.println("INSOMNIA:"+operate.getPlayerCards()[i].getCardName());
        }

    }

}
