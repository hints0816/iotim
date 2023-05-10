function properties() {
    var total = document.getElementById("total").value;
    var civilian = document.getElementById("civilian").value;
    var wolf = document.getElementById("wolf").value;

    var dataProperties = JSON.stringify({
        "groupid": toGroup,
        "total": total,
        "role": {
            "WOLF": wolf,
            "PROPHET": $("#prophet").prop("checked") ? 1 : 0,
            "CIVILIAN": civilian,
            "TREATERS": $("#prophet").prop("checked") ? 1 : 0,
            "BANDIT": $("#prophet").prop("checked") ? 1 : 0,
            "INSOMNIA": $("#prophet").prop("checked") ? 1 : 0
        }
    });
    $.ajax({
        method: 'POST',
        url: '../game/properties',
        dataType: 'json',
        contentType: "application/json",
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

                var str = '<div style="text-align:right;margin: 3px;"><a href="#" onclick="openGame(\'' + toGroup + '\')">enter game</a>' + ' : ' + myNickName + '<img class="media-object" src="http://10.2.24.234:9004/gscm/' + myAvater + '" height="45" width="45" alt="..." style="display: inline-block;"></div>';
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
        url: '../game/lobby/' + groupId,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                $("#gameCavLeft").html("");
                $("#gameCavRight").html("");
                var length = data.data.players.length;
                var number = Math.round(length / 2);
                for (var i = 0; i < number; i++) {
                    if (data.data.players[i] == null) {
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><span class="iconfont">&#xe742;</span></a></div>')
                    } else {
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><img class="media-object" src="http://10.2.24.234:9004/gscm/' + data.data.players[i].avater + '" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }

                }
                for (var i = number; i < length; i++) {
                    if (data.data.players[i] == null) {
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><span class="iconfont">&#xe742;</span></a></div>')
                    } else {
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><img class="media-object" src="http://10.2.24.234:9004/gscm/' + data.data.players[i].avater + '" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }
                }
                if (data.data.isOwner) {
                    $("#gameCavMe").css("display", "block");
                }

                if (data.data.stage == 1) {
                    $("#gameCav").html("");
                    $("#gameCav").append('<div><span id="second"></span></div>');

                    inputTime = new Date(data.data.startTime);
                    countDown();
                    interval = setInterval(countDown, 1000);

                    var cardNum = data.data.cardNum;
                    for (var i = 0; i < cardNum; i++) {
                        if (data.data.cards[i].isPicked) {
                            $("#gameCav").append('<a href="#" style="background-color:#fd7fff" id="card-' + i + '" onclick="pickCard(' + i + ')" class="col-xs-4 card"></a>');
                        } else {
                            $("#gameCav").append('<a href="#" id="card-' + i + '" onclick="pickCard(' + i + ')" class="col-xs-4 card"></a>');
                        }
                    }
                }

                $("#game").css("height", "50%");
                $("#show").css("height", "50%");
            } else {
                alert(data.msg);
            }
        },
        error: function (data) {
            alert();
        }
    });

}

function enterNight(groupId) {
    $.ajax({
        type: 'GET',
        url: '../game/lobby/' + groupId,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                $("#gameCavLeft").html("");
                $("#gameCavRight").html("");
                var length = data.data.players.length;
                var number = Math.round(length / 2);
                for (var i = 0; i < number; i++) {
                    if (data.data.players[i] == null) {
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><span class="iconfont">&#xe742;</span></a></div>')
                    } else {
                        $("#gameCavLeft").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><img class="media-object" src="http://10.2.24.234:9004/gscm/' + data.data.players[i].avater + '" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }

                }
                for (var i = number; i < length; i++) {
                    if (data.data.players[i] == null) {
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><span class="iconfont">&#xe742;</span></a></div>')
                    } else {
                        $("#gameCavRight").append('<div style="margin: 3px;"><a href="#" onclick="enterGame(\'' + groupId + '\',' + i + ')"><img class="media-object" src="http://10.2.24.234:9004/gscm/' + data.data.players[i].avater + '" height="45" width="45" alt="..." style="display: inline-block;"></a></div>')
                    }
                }
                if (data.data.isOwner) {
                    $("#gameCavMe").css("display", "block");
                }

                if (data.data.stage == 1) {
                    $("#gameCav").html("");

                    var cardNum = data.data.cardNum;

                    var length1 = data.data.players.length;
                    var remindCard = cardNum-length1;

                    for (var i = 0; i < remindCard; i++) {
                        $("#gameCav").append('<a href="#" id="card-' + i + '" onclick="pickCard(' + i + ')" class="col-xs-4 card"></a>');
                    }
                }

                $("#game").css("height", "50%");
                $("#show").css("height", "50%");
            } else {
                alert(data.msg);
            }
        },
        error: function (data) {
            alert();
        }
    });
}

var inputTime;
var interval;

function countDown() {
    var nowTime = new Date();
    var times = (inputTime - nowTime) / 1000;
    if (times > 0) {
        $("#second").html(times);
    }else{
        $("#second").html("");
        clearInterval(interval);

        let object = {};
        var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
        object.groupId = toGroup;
        object.msgId = uuid;
        object.type = type;
        object.msg = "";
        object.fileType = "84";
        send(JSON.stringify(object));
    }

}

// 坐下
function enterGame(groupId, i) {
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

function exitGame() {
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
    $("#gameCavMe").css("display", "none");

    $("#game").css("height", "0%");
    $("#show").css("height", "100%");
}

function startGame() {
    let object = {};
    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
    object.groupId = toGroup;
    object.msgId = uuid;
    object.type = type;
    object.msg = "";
    object.fileType = "82";
    send(JSON.stringify(object));
}

function pickCard(index) {
    let object = {};
    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
    object.groupId = toGroup;
    object.msgId = uuid;
    object.type = type;
    object.msg = index;
    object.fileType = "83";
    send(JSON.stringify(object));
}

function pickedCard(index) {
    console.log(index);
}