function showMess(contact, message) {
    setTimeout(function() {
        if (window.Notification && Notification.permission !== "denied") {
            Notification.requestPermission(function(status) {
                if (status === "granted") {
                    var m = new Notification('you have new message!', {
                        body: contact + ' : '+ message
                    });
                    m.onclick = function() {
                        window.focus();
                    }
                } else {
                    alert('当前浏览器不支持弹出消息')
                }
            });
        }
    }, 1000)
}

$(document).ready(function () {
    getInfo();
    connect();

    userlist();

    channellist();
});

$('#myModal').on('show.bs.modal', function (event) {
    $.ajax({
        url: '../userlist',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                var str = "";

                data.data.users.forEach((item, index, data) => {
                    str += '<div class="checkbox">' +
                        '<label>' +
                        '<input  name="usercheckbox" type="checkbox" value="' + item.user_name + '">' +
                        item.nick_name +
                        '</label>' +
                        '</div>';
                })
                $(".group_member").html(str);
            }
        },
    });
})

$(document).on("click", ".datasourcelink", function () {
    var list = [];

    $(":checkbox[name='usercheckbox']:checked").each(function () {
        list.push($(this).val());
    });

    let object = {};
    object.name = $('#group_name').val();
    object.userlist = list;
    object.type = 4;
    send(JSON.stringify(object));
});

$(document).on('change', '.toUser', function () {
    toUser = $('input:radio:checked').val();
    $("#show").html("");
    history(toUser);
});

function selectChat(type,username,nickname, e) {
    if(type == 1){
        selectGroup(username, nickname, e)
    }else{
        selectUser(username,nickname, e)
    }

}

function clickMessage(e) {
    userlist();
    $("#navleft li").removeClass("active");
    $(e).parent().addClass("active")
}

function selectUser(username,nickname, e) {
    toUser = username;
    $("#show").html("");
    userhistory(username);
    type = 2;
    $("#userlist li").removeClass("active");
    $(e).addClass("active");

    $("#usergroup").html(nickname+'<a href="#" style=""><span class="iconfont">&#xe78e;</span></a>');
}

function selectGroup(groupid, groupname, e) {
    toGroup = groupid;
    $("#show").html("");
    $("#redpoint-" + groupid).css("display", "none");
    grouphistory(groupid);
    type = 3;
    $("#userlist li").removeClass("active");

    $(e).addClass("active");

    $("#usergroup").html(groupname+'<a href="#" style=""><span class="iconfont">&#xe78e;</span></a>');

    viewChannel(groupid);
}

function showFriends(e){
    $.ajax({
        url: '../showfriends',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                var str = "";
                data.data.users.forEach((item, index, data) => {
                    str += '<li onclick="selectChat(2,\'' + item.userName + '\',\'' + item.nickName + '\', this)" style="display: flex;padding: 5px"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="...">' +
                        '<div style=" height:45px;vertical-align:middle;font-size:15px;margin-left: 8px;">' +
                        '<a style="font-size: 15px" href="javascript:void(0)">' + item.nickName + '</a>' +
                        '</div></li>';
                })
                $("#userlist").html(str);

                $("#myhead").html('<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;">');
                $("#navleft li").removeClass("active");
                $(e).parent().addClass("active")
            }
        },
    });
}

$(document).on('change', '#exampleInputFile', function () {
    var files = this.files;
    var formData = new FormData();
    formData.append("file", files[0]);
    $.ajax({
        method: 'POST',
        url: 'upload',
        dataType: 'json',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                if (type == 2) {
                    let object = {};
                    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
                    object.msgId = uuid;
                    object.type = type;
                    object.fileType = 1;
                    object.toUserId = toUser;
                    object.msg = data.msg;

                    console.log(object);
                    send(JSON.stringify(object));

                    var str = '<div style="text-align:right;margin: 3px;">' + object.msg + ' : ' + myNickName + '<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    $("#show").append(str);

                    var chatlist = document.getElementById('show');
                    chatlist.scrollTop = chatlist.scrollHeight;


                    $('#msg').val("");
                } else {
                    let object = {};
                    var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
                    object.groupId = toGroup;
                    object.msgId = uuid;
                    object.type = type;
                    object.fileType = 1;
                    object.msg = data.msg;
                    send(JSON.stringify(object));

                    var str = '<div style="text-align:right;margin: 3px;">' + object.msg + ' : ' + myNickName + '<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    $("#show").append(str);

                    var chatlist = document.getElementById('show');
                    chatlist.scrollTop = chatlist.scrollHeight;

                    $('#msg').val("");
                }
            } else {
                alert();
            }
        },
        error: function (data) {
            alert();
        }
    });
});


let url = "ws://localhost:9091";
let access_token = getCookie();
let ws;
let toUser;
let myNickName;
let myAvater;
let toGroup;
let type;

function userhistory(toUser1) {
    $.ajax({
        url: '../history',
        dataType: 'json',
        type: 'get',
        data: {
            "fromUsername": toUser1
        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            $("#member").html("");
            data.data.forEach((item, index, data) => {
                var str = "";
                if ($("#name").val() == item.from_id) {
                    if(item.file_type == 1){
                        str = '<div style="text-align:right;margin: 3px;"><span><img class="media-object" src="img/'+item.content+'" height="45" width="45" alt="...">'+ ' : ' + item.nick_name + '</span><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    }else{
                        str = '<div style="text-align:right;margin: 3px;"><span>' + item.content + ' : ' + item.nick_name + '</span><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    }
                    $("#show").append(str);
                } else {
                    str = '<div style="text-align:left;margin: 3px;"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;"><span>' + item.nick_name + ' : ' + item.content + '</span></div>';
                    $("#show").append(str);
                }
            })
        }
    });
}

function grouphistory(toGroup) {
    $.ajax({
        url: '../message/history',
        dataType: 'json',
        type: 'get',
        data: {
            "groupId": toGroup
        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            data.data.history.forEach((item, index, data) => {
                if ($("#name").val() == item.from_id) {
                    var str = '<div style="text-align:right;margin: 3px;">' + item.content + ' : ' + item.nick_name + '<img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    $("#show").append(str);
                } else {
                    var str = '<div style="text-align:left;margin: 3px;"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;">' + item.nick_name + ' : ' + item.content + '</div>';
                    $("#show").append(str);
                }
                var chatlist1 = document.getElementById('show');
                chatlist1.scrollTop = chatlist1.scrollHeight;
            })
            $("#member").html('<li style="display: flex"><input type="text" class="form-control" placeholder="Search..."></li>');
            $("#member").append('<li style="display: flex">MANAGER</li>');
            data.data.group.forEach((item, index, data) => {
                var str = '<li style="display: flex"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..."><span>' + item.nick_name + '</span></li>';
                $("#member").append(str);
            })
            $("#member").append('<li style="display: flex">MEMBERS</li>');
            data.data.members.forEach((item, index, data) => {
                var str = '<li style="display: flex"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..."><span>' + item.nick_name + '</span></li>';
                $("#member").append(str);
            })
        }
    });
}

function getInfo() {
    $.ajax({
        url: '../getInfo',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            $("#name").val(data.userName);
            myNickName = data.nickName;
            myAvater = data.avater;
        },
        error: function (data) {
            if (data.status == 401) {
                alert("请登录");
                window.location.href = 'http://localhost:8084/login';
            }
        }
    });
}

function userlist() {
    $.ajax({
        url: '../userlist',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                var str = "";
                data.data.users.forEach((item, index, data) => {
                    str += ' <li onclick="selectChat('+item.msgtype+',\'' + item.target + '\',\'' + item.name + '\', this)" style="display: flex;padding: 5px"><img class="media-object" src="'+item.avater+'" height="45" width="45" alt="...">' +
                        '<div style=" height:45px;vertical-align:middle;font-size:15px;margin-left: 8px;">' +
                        '<a style="font-size: 15px" href="javascript:void(0)">' + item.name + '</a>' +
                        '<div style="font-size: 10px">' + item.content + '</div>' +
                        '</div></li>';
                })
                $("#userlist").html(str);

                $("#myhead").html('<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;">');

            }
        },
    });
}

function channellist() {
    $.ajax({
        url: '../channellist',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                var str = "";
                data.data.users.forEach((item, index, data) => {
                    str += '<li role="presentation">\n' +
                        '<a href="#" style="padding: 10px 8px;" onclick="selectGroup(\''+item.target+'\',\''+item.name+'\',this);">' +
                        '<img class="media-object" src="'+item.avater+'" height="45" width="45" alt="..."><i style="display: none" id="redpoint-'+item.target+'" class="toolbar-msg-count"></i>' +
                        '</a>' +
                        '</li>';
                })
                $("#navleft").html($("#navleft").html()+str);
            }
        },
    });
}

function viewChannel(groupId) {
    $.ajax({
        url: '../channel/info/'+groupId,
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                var str = "";
                str += '<li onclick="" style="display: flex;padding: 5px"><img class="media-object" src="'+data.data.img+'" height="45" width="45" alt="...">' +
                    '<div style=" height:45px;vertical-align:middle;font-size:15px;margin-left: 8px;">' +
                    '<a style="font-size: 15px" href="javascript:void(0)">' + data.data.name + '</a>' +
                    '<div style="font-size: 10px"> </div>' +
                    '</div></li>';
                $("#userlist").html(str);


            }
        },
    });
}

function sendHeartBeat(){
    setTimeout(
        function(){
            let object = {};
            object.type = 11;
            send(JSON.stringify(object));
        },30000
    );
}

function init() {
    ws.onopen = function (evt) {
        console.log("WebSocket 连接成功!!");
        let object = {};
        object.type = 1;
        send(JSON.stringify(object));
    };

    ws.onmessage = function (event) {
        let data = JSON.parse(event.data);

        var show = document.getElementById("show");
        if (data.status == 200) {
            if (data.type == 1) {
                show.innerHTML += data.params.message + "<br/>";
            } else if (data.type == 2) {
                show.innerHTML += '<div style="text-align:left;margin: 3px;"><img class="media-object" src="'+ data.params.avater+'" height="45" width="45" alt="..." style="display: inline-block;">'+data.params.nickName + ' : ' + data.params.message + "</div><br/>";
                var chatlist = document.getElementById('show');
                chatlist.scrollTop = chatlist.scrollHeight;
            } else if (data.type == 4){
                $('#myModal').modal('toggle');
                userlist();
            }else if (data.type == 12){
                sendHeartBeat();
            }else{
                if(toGroup == data.params.groupId){
                    show.innerHTML += '<div style="text-align:left;margin: 3px;"><img class="media-object" src="'+ data.params.avater+'" height="45" width="45" alt="..." style="display: inline-block;">'+data.params.fromUser + ' : ' + data.params.message + "<br/>";
                }else{
                    updateRedPoint(data.params.groupId);
                }
                showMess(data.params.fromUser, data.params.message);
            }
        }
    }

    sendHeartBeat();
}

function updateRedPoint(groupId) {
    console.log(groupId);
    $("#redpoint-" + groupId).css("display", "block");
}

function send(message) {
    if (!window.WebSocket) {
        return;
    }
    if (ws.readyState === ws.OPEN) {
        ws.send(message);
    } else {
        alert("WebSocket连接没有建立成功！！");
    }
}

function creategroup() {
    $.ajax({
        url: '../getInfo',
        dataType: 'json',
        type: 'get',
        data: {},
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            $("#name").val(data.name);
        },
        error: function (data) {
            if (data.status == 401) {
                alert("请登录");
                window.location.href = 'http://localhost:8084/login';
            }
        }
    });
}

//点击连接 时候触发的事件 【这个函数 除了更改自己url 或者添加自己的业务逻辑  一般不用修改 】
function connect() {
    try {
        ws = new WebSocket(url + "/" + access_token);
        init();
    } catch (e) {
        console.log(e);
    }
}

function getCookie() {
    var token = "";
    var cookie_name = "access_token";
    var allcookies = document.cookie;
    var cookie_pos = allcookies.indexOf(cookie_name);
    if (cookie_pos != -1) {
        cookie_pos += cookie_name.length + 1;
        var cookie_end = allcookies.indexOf(";", cookie_pos);
        if (cookie_end == -1) {
            cookie_end = allcookies.length;
        }
        token = unescape(allcookies.substring(cookie_pos, cookie_end));
    }
    return token
}

//断开连接
function disconnect() {
    ws.close()
    document.getElementById("show").innerHTML = "";
    document.getElementById("show").innerHTML += "断开连接<br/>";
    document.getElementById("msg").value = "";
}

//发送私聊
function sendName(event) {
    var evt = window.event || e;
    if (evt.keyCode == 13) {

        if (type == 2) {
            let object = {};
            var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
            object.msgId = uuid;
            object.type = type;
            object.toUserId = toUser;
            object.msg = $("#msg").val();
            send(JSON.stringify(object));

            var str = '<div style="text-align:right;margin: 3px;">' + object.msg + ' : ' + myNickName + '<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
            $("#show").append(str);

            var chatlist = document.getElementById('show');
            chatlist.scrollTop = chatlist.scrollHeight;


            $('#msg').val("");
        } else {
            let object = {};
            var uuid = new Snowflake(1n, 1n, 0n).nextId().toString();
            object.groupId = toGroup;
            object.msgId = uuid;
            object.type = type;
            object.msg = $("#msg").val();
            send(JSON.stringify(object));

            var str = '<div style="text-align:right;margin: 3px;">' + object.msg + ' : ' + myNickName + '<img class="media-object" src="'+ myAvater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
            $("#show").append(str);

            var chatlist = document.getElementById('show');
            chatlist.scrollTop = chatlist.scrollHeight;

            $('#msg').val("");
        }
    }

}

//展示 返回的消息
function showResponse(message) {
}