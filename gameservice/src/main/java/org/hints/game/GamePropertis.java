package org.hints.game;

import lombok.Data;

import java.util.HashMap;

/**
 * @Description
 * @Author 180686
 * @Date 2023/4/21 10:06
 */
@Data
public class GamePropertis {

    private HashMap<String, Integer> propertisMap;

    public int getPlayNum(){
        int i = 0;
        for (Integer value : this.propertisMap.values()) {
            i+=value;
        }
        return i;
    }
}
