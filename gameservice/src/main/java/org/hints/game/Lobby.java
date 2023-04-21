package org.hints.game;

import lombok.Data;

import java.util.*;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/4/20 17:21
 */
@Data
public class Lobby {

    private Card[] cards;

    private Card[] remaindCards;

    private Card[] playerCards;

    private String groupId;

    private Player[] players;

    private GamePropertis gamePropertis;

    public void initCards(){
        GamePropertis gamePropertis = this.gamePropertis;

        this.cards = new Card[players.length+3];

        HashMap<String, Integer> propertisMap = gamePropertis.getPropertisMap();

        int i = 0;
        for (String s : propertisMap.keySet()) {
            Integer integer = propertisMap.get(s);
            Card card1 = new Card();
            card1.setCardName(s);
            for (int j = 0; j < integer; j++) {
                cards[i] = card1;
                i++;
            }
        }
    }

    public void init(){
        initCards();
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

    public void setPlayer(Player player, int i) {
        synchronized(this) {
            if (players[i] == null) {
                players[i] = player;
                players[i].setIsReady(false);
            }
        }
    }

    public void ready(Long id) {
        List<Player> playerList = Arrays.asList(this.players);
        Optional<Player> first = playerList.stream().filter(v -> v.getId().equals(id)).findFirst();
        Player player = first.get();
        int i = playerList.indexOf(player);
        this.players[i].setIsReady(true);
    }

    public boolean isFullPlayer() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFullReady() {
        for (int i = 0; i < players.length; i++) {
            if (!players[i].getIsReady()) {
                return false;
            }
        }
        return true;
    }

}
