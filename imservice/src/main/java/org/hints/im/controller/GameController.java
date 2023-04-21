package org.hints.im.controller;

import org.hints.game.GamePropertis;
import org.hints.game.Lobby;
import org.hints.im.pojo.ReturnVo;
import org.hints.im.utils.SessionUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/4/21 14:55
 */
@RestController
@RequestMapping(value = "/game")
public class GameController {

    @PostMapping("/properties")
    public ReturnVo properties(@RequestBody HashMap<String, Object> properties) {
        String groupid = properties.get("groupid").toString();
        Lobby lobby = SessionUtil.getLobby(groupid);
        if (lobby == null) {
            return ReturnVo.error("CAN'T FIND GAME LOBBY");
        }
        Object o = properties.get("role");
        HashMap o1 = (HashMap) o;
        GamePropertis propertis = new GamePropertis();
        propertis.setPropertisMap(o1);
        lobby.setGamePropertis(propertis);

        SessionUtil.bindLobby(groupid, lobby);
        return ReturnVo.success();
    }
}
