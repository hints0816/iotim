package org.hints.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/4/20 11:26
 */
@Data
public class Operate {

    private Card[] cards;

    private Card[] remaindCards;

    private Card[] playerCards;

    public Operate(Card[] cards){
        this.cards = cards;
    }

    public void init() {
        for (int i = cards.length - 1; i > 0; i--) {
            //随机数生成器，范围[0, i]
            int rand = (new Random()).nextInt(i + 1);
            Card temp = cards[i];
            cards[i] = cards[rand];
            cards[rand] = temp;
        }

        List<Card> cardList = Arrays.asList(this.cards);
        cardList = cardList.subList(0, cardList.size()-3);
        playerCards = cardList.toArray(new Card[cardList.size()]);

        cardList = cardList.subList(cardList.size()-3, cardList.size());
        remaindCards = cardList.toArray(new Card[cardList.size()]);
    }

    public Card pickUp(int i){
        Card playerCard = playerCards[i];
        playerCard.setIsPicked(true);
        return playerCard;
    }

    public Card checkPlayerCards(int i){
        Card card = playerCards[i];
        return card;
    }

    public ArrayList<Card> checkremaindCards(int[] indexs){
        ArrayList<Card> cards = new ArrayList<>();
        for (int index : indexs) {
            Card remaindCard = remaindCards[index];
            cards.add(remaindCard);
        }
        return cards;
    }

    public ArrayList<Integer> checkWolf(){
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < playerCards.length; i++) {
            if("WOLF".equals(playerCards[i].getCardName())){
                integers.add(i);
            }
        }
        return integers;
    }

    // transfer operate
    public void exchange(int i, int j){
        Card temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
        List<Card> cardList = Arrays.asList(this.cards);
        cardList = cardList.subList(0, cardList.size()-3);
        playerCards = cardList.toArray(new Card[cardList.size()]);

        cardList = cardList.subList(cardList.size()-3, cardList.size());
        remaindCards = cardList.toArray(new Card[cardList.size()]);
    }


}
