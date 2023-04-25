function properties() {
    var total = document.getElementById("total").value;
    var civilian = document.getElementById("civilian").value;
    var wolf = document.getElementById("wolf").value;

    var dataProperties = JSON.stringify({
        "groupid":toGroup,
        "total":total,
        "role":{
            "WOLF":wolf,
            "PROPHET":$("#prophet").prop("checked")?1:0,
            "CIVILIAN":civilian,
            "TREATERS":$("#prophet").prop("checked")?1:0,
            "BANDIT":$("#prophet").prop("checked")?1:0,
            "INSOMNIA":$("#prophet").prop("checked")?1:0
        }
    });
    $.ajax({
        method: 'POST',
        url: '../game/properties',
        dataType: 'json',
        contentType:"application/json",
        data: dataProperties,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            console.log(data);
            if (data.code == 200) {
                let object = {};
                var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
                object.groupId = toGroup;
                object.msgId = uuid;
                object.type = type;
                object.msg = "";
                object.fileType = "90";
                send(JSON.stringify(object));

                var str = '<div style="text-align:right;margin: 3px;"><a href="#" onclick="openGame(\''+toGroup+'\')">enter game</a>' + ' : ' + myNickName + '<img class="media-object" src="http://10.2.24.234:9004/gscm/' + myAvater + '" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                $("#show").append(str);

                var chatlist = document.getElementById('show');
                chatlist.scrollTop = chatlist.scrollHeight;

                $('#gameProperties').modal('toggle');
            } else {
                alert();
            }
        },
        error: function (data) {
            alert();
        }
    });
}



function openGame(groupId) {
    $.ajax({
        type: 'GET',
        url: '../game/lobby/'+groupId,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                $("#gameCavLeft").html("");
                $("#gameCavRight").html("");
                var length = data.data.players.length;
                var number = Math.round(length/2);
                for (var i = 0; i < number; i++) {
                    if(data.data.players[i] == null){
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\''+groupId+'\','+i+')"><span class="iconfont">&#xe742;</span></a></div>')
                    }else{
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\''+groupId+'\','+i+')"><img class="media-object" src="http://10.2.24.234:9004/gscm/'+data.data.players[i].avater+'" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }

                }
                for (var i = number; i < length; i++) {
                    if(data.data.players[i] == null){
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\''+groupId+'\','+i+')"><span class="iconfont">&#xe742;</span></a></div>')
                    }else{
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\''+groupId+'\','+i+')"><img class="media-object" src="http://10.2.24.234:9004/gscm/'+data.data.players[i].avater+'" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }
                }
                if(data.data.isOwner){
                    $("#gameCavMe").css("display","block");
                }

                $("#game").css("height","50%");
                $("#show").css("height","50%");
            } else {
                alert();
            }
        },
        error: function (data) {
            alert();
        }
    });

}

// 坐下
function enterGame(groupId,i) {
    let object = {};
    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
    object.groupId = toGroup;
    object.msgId = uuid;
    object.type = type;
    object.msg = i;
    object.fileType = "80";
    send(JSON.stringify(object));

    // 刚发送，是否考虑同步
    openGame(groupId);
}

function exitGame(){
    let object = {};
    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
    object.groupId = toGroup;
    object.msgId = uuid;
    object.type = type;
    object.msg = "";
    object.fileType = "81";
    send(JSON.stringify(object));

    // 刚发送，是否考虑同步
    $("#gameCavLeft").html("");
    $("#gameCavRight").html("");
    $("#game").css("height","0%");
    $("#show").css("height","100%");
}