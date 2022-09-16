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

            }
        },
    });
});

