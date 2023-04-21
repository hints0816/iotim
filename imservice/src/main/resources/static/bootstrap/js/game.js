function properties() {
    console.log(toGroup);
    var total = document.getElementById("total").value;

    $.ajax({
        method: 'POST',
        url: '../game/properties',
        dataType: 'json',
        data: {

        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Authorization", "Bearer " + access_token);
        },
        success: function (data) {
            if (data.code == 200) {
                console.log('123')
            } else {
                alert();
            }
        },
        error: function (data) {
            alert();
        }
    });
}