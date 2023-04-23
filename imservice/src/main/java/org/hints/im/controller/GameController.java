package org.hints.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.hints.game.GamePropertis;
import org.hints.game.Lobby;
import org.hints.game.Player;
import org.hints.im.pojo.ReturnVo;
import org.hints.im.utils.SessionUtil;
import org.springframework.web.bind.annotation.*;

import java.security.acl.LastOwnerException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/4/21 14:55
 */
@RestController
@RequestMapping(value = "/game")
public class GameController {

    @PostMapping("/properties")
    public ReturnVo setProperties(@RequestBody JSONObject properties) {
        String groupId = properties.get("groupid").toString();
        Lobby lobby = new Lobby();
        lobby.setGroupId(groupId);
        JSONObject obj = properties.getJSONObject("role");
        HashMap<String, Integer> stringIntegerMap = JSONObject.parseObject(obj.toJSONString(), new TypeReference<HashMap<String, Integer>>() {});
        GamePropertis propertis = new GamePropertis();
        propertis.setPropertisMap(stringIntegerMap);
        lobby.setGamePropertis(propertis);

        int total = Integer.valueOf(properties.get("total").toString());
        Player[] players = new Player[total];
        lobby.setPlayers(players);

        lobby.initCards();
        SessionUtil.bindLobby(groupId, lobby);

        return ReturnVo.success();
    }

    @GetMapping("/lobby/{groupid}")
    public ReturnVo getInfo(@PathVariable(value = "groupid") String groupId) {
        Lobby lobby = SessionUtil.getLobby(groupId);
        lobby.getPlayerCards();

        return ReturnVo.success(lobby.getPlayers());
    }
}
