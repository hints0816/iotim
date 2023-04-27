package org.hints.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.checkerframework.checker.units.qual.A;
import org.hints.game.Card;
import org.hints.game.GamePropertis;
import org.hints.game.Lobby;
import org.hints.game.Player;
import org.hints.im.pojo.ReturnVo;
import org.hints.im.pojo.User;
import org.hints.im.utils.SessionUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
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

    @Autowired
    private Dao dao;

    @PostMapping("/properties")
    public ReturnVo setProperties(@RequestBody JSONObject properties, Principal principal) {
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

        User user = dao.fetch(User.class, Cnd.where("user_name", "=", principal.getName()));
        Player player = new Player();
        player.setId(user.getUserId());
        player.setName(user.getUserName());
        player.setAvater(user.getAvater());
        player.setIsOwner(true);
        players[0] = player;
        lobby.setPlayers(players);
        lobby.setStage(0);

        lobby.initCards();
        SessionUtil.bindLobby(groupId, lobby);

        return ReturnVo.success();
    }

    @GetMapping("/lobby/{groupid}")
    public ReturnVo getInfo(@PathVariable(value = "groupid") String groupId, Principal principal) {
        Lobby lobby = SessionUtil.getLobby(groupId);
        if(lobby == null){
            return ReturnVo.error("Room Is Not Exist!");
        }
        lobby.getPlayerCards();
        HashMap<String, Object> map = new HashMap<>();
        Player[] players = lobby.getPlayers();
        map.put("players", players);
        map.put("isOwner",false);
        for (int i = 0; i < lobby.getCards().length; i++) {
            lobby.getCards()[i].setCardName("");
            lobby.getCards()[i].setAvater("");
        }
        map.put("cards",lobby.getCards());
        map.put("cardNum",lobby.getCards().length);
        map.put("stage",lobby.getStage());
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player!=null&&player.getName().equals(principal.getName())) {
                if(player.getIsOwner()){
                    map.put("isOwner",true);
                    break;
                }
            }
        }
        return ReturnVo.success(map);
    }
}
