function properties() {
    console.log(toGroup);
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

                var str = '<div style="text-align:right;margin: 3px;"><a href="#" onclick="openGame(\''+toGroup+'\')">enter game</a>' + ' : ' + myNickName + '<img class="media-object" src="http://10.2.24.234:9003/gscm/' + myAvater + '" height="45" width="45" alt="..." style="display: inline-block;"></div>';
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

                var length = data.data.length-1;
                var number = Math.round(length/2);
                for (var i = 0; i < number; i++) {
                    if(data.data[i] == null){
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame("'+groupId+'",+i+)"><span class="iconfont">&#xe742;</span></a></div>')
                    }else{
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame("'+groupId+'",+i+)"><img class="media-object" src="http://10.2.24.234:9003/gscm"'+data.data[i].avater+' height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }

                }
                for (var i = number; i < length; i++) {
                    if(data.data[i] == null){
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame("'+groupId+'",+i+)"><span class="iconfont">&#xe742;</span></a></div>')
                    }else{
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame("'+groupId+'",+i+)"><img class="media-object" src="http://10.2.24.234:9003/gscm"'+data.data[i].avater+' height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }
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


function enterGame(groupId,i) {
    let object = {};
    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
    object.groupId = toGroup;
    object.msgId = uuid;
    object.type = type;
    object.msg = i;
    object.fileType = "80";
    send(JSON.stringify(object));
}