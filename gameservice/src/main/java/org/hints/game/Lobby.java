package org.hints.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // 游戏阶段0未开始1选牌阶段
    private Integer stage;

    private Player[] players;

    private GamePropertis gamePropertis;

    private Date startTime;

    public void overTimePick() {
        synchronized (this) {
            for (Player player : this.players) {
                if (player != null && player.getOriganCard() == null) {
                    for (Card card : this.cards) {
                        if (!card.getIsPicked()) {
                            card.setIsPicked(true);
                            player.setOriganCard(card);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void initCards() {
        GamePropertis gamePropertis = this.gamePropertis;

        this.cards = new Card[players.length + 3];

        HashMap<String, Integer> propertisMap = gamePropertis.getPropertisMap();

        int i = 0;

        for (String s : propertisMap.keySet()) {
            int integer = Integer.parseInt(propertisMap.get(s).toString());
            Card card1 = new Card();
            card1.setCardName(s);
            for (int j = 0; j < integer; j++) {
                cards[i] = card1;
                i++;
            }
        }
    }

    public void init() {
        initCards();
        for (int i = cards.length - 1; i > 0; i--) {
            //随机数生成器，范围[0, i]
            int rand = (new Random()).nextInt(i + 1);
            Card temp = cards[i];
            cards[i] = cards[rand];
            cards[rand] = temp;
        }

    }

    public void splitCard() {
        List<Card> cardList = Arrays.asList(this.cards);
        cardList = cardList.subList(0, cardList.size() - 3);
        playerCards = cardList.toArray(new Card[cardList.size()]);

        cardList = cardList.subList(cardList.size() - 3, cardList.size());
        remaindCards = cardList.toArray(new Card[cardList.size()]);
    }


    public Card pickUp(int i) {
        synchronized (this) {
            Card card = cards[i];
            card.setIsPicked(true);
            return card;
        }
    }

    public Player findPlayer(Long id) {
        for (int i1 = 0; i1 < players.length; i1++) {
            if (players[i1] != null) {
                if (players[i1].getId().equals(id)) {
                    return players[i1];
                }
            }
        }
        return null;
    }

    public Boolean isAllPicked() {
        for (int i1 = 0; i1 < players.length; i1++) {
            if (players[i1] == null) {
                return false;
            }
            if (players[i1].getOriganCard() == null) {
                return false;
            }
        }
        return true;
    }


    public void setPlayer(Player player, int i) {
        synchronized (this) {
            if (players[i] == null) {
                for (int i1 = 0; i1 < players.length; i1++) {
                    if (players[i1] != null) {
                        if (players[i1].getId().equals(player.getId())) {
                            players[i1] = null;
                        }
                    }
                }
                players[i] = player;
                players[i].setIsReady(false);
            }
        }
    }

    public void offPlayer(Player player) {
        for (int i1 = 0; i1 < players.length; i1++) {
            if (players[i1] != null) {
                if (players[i1].getId().equals(player.getId())) {
                    players[i1] = null;
                }
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
