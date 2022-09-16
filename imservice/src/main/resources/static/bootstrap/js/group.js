$(document).on("click", ".modifyGroupName", function () {
    $.ajax({
        url: '../channel/updatename',
        dataType: 'json',
        type: 'post',
        data: {
            'groupId':toGroup,
            'name':$("#groupNickName").val()
        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                $('#modifyGroupName').modal('toggle');
                channellist();
                userlist();
            }
        },
    });
});

$(document).on("click", ".inviteFriendClick", function () {
    $.ajax({
        url: '../channel/invitefriend',
        dataType: 'json',
        type: 'post',
        data: {
            'user_name':$("#inviteFriendName").val(),
            'group_id':toGroup
        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                $('#invitefriend').modal('toggle');
                channellist();
                userlist();
            }
        },
    });
});

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
                    var str = '<div style="text-align:right;margin: 3px;">' + item.content + ' : ' + item.nick_name + '<img class="media-object" src="http://10.2.24.234:9003/gscm/'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;"></div>';
                    $("#show").append(str);
                } else if(item.from_id == 0){
                    var str = '<div style="text-align: center;\n' +
                        '    font-size: x-large;\n' +
                        '    font-weight: 800;">' + item.content + '</div>';
                    $("#show").append(str);
                }else {
                    var str = '<div style="text-align:left;margin: 3px;"><img class="media-object" src="http://10.2.24.234:9003/gscm/'+item.avater+'" height="45" width="45" alt="..." style="display: inline-block;">' + item.nick_name + ' : ' + item.content + '</div>';
                    $("#show").append(str);
                }
                var chatlist1 = document.getElementById('show');
                chatlist1.scrollTop = chatlist1.scrollHeight;
            })
            $("#member").html('<li style="display: flex"><input type="text" class="form-control" placeholder="Search..."></li>');
            $("#member").append('<li style="display: flex">MANAGER</li>');
            data.data.group.forEach((item, index, data) => {
                var str = '<li style="display: flex"><img class="media-object" src="http://10.2.24.234:9003/gscm/'+item.avater+'" height="45" width="45" alt="..."><span>' + item.nick_name + '</span></li>';
                $("#member").append(str);
            })
            $("#member").append('<li style="display: flex">MEMBERS</li>');
            data.data.members.forEach((item, index, data) => {
                var str = '<li style="display: flex"><img class="media-object" src="http://10.2.24.234:9003/gscm/'+item.avater+'" height="45" width="45" alt="..."><span>' + item.nick_name + '</span></li>';
                $("#member").append(str);
            })
        }
    });
}