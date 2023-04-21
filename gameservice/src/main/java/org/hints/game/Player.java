package org.hints.game;

import lombok.Data;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/4/20 15:11
 */
@Data
public class Player {

    private Long id;

    private String name;

    private Card origanCard;

    private int index;

    private String avater;

    private Boolean isReady;

    private Lobby lobby;

}
