var EventUtil = {
    addHandler : function(element , type , handler){
        if(element.addEventListener){
            element.addEventListener(type , handler , true);
        }else if(element.attachEvent){
            element.attachEvent('on' + type , handler);
        }else{
            element['on' + type] = handler;
        }
    },
    removeHandler : function(element , type , handler){
        if(element.removeEventListener){
            element.removeEventListener(type , handler ,false);
        }else if(element.detatchEvent){
            element.detatchEvent('on' + type , handler);
        }else{
            element['on' +type] = null;
        }
    },
    getEvent:function(event){
        return event ? event : window.event;
    },
    getTarget:function(event){
        return event.target || event.srcElement;
    },
    preventDefault:function(event){
        if(event.preventDefault){
            event.preventDefault();
        }else{
            event.returnValue = false;
        }
    },
    stopPropagation:function(event){
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble = true;
        }
    },
    getRelatedTarget:function(event){
        if(event.relatedTarget){
            return event.relatedTarget;
        }else if(event.toElement){
            return event.toElement;
        }else if(event.fromElement){
            return event.fromElement;
        }else{
            return null;
        }
    },
    gutButton:function(event){
        if(document.implementation.hasFeature("MouseEvents" , "2.0")){
            return event.button;
        }else{
            switch(event.button){
                case 0:
                case 1:
                case 2:
                case 3:
                case 5:
                case 7:
                    return 0;
                case 2:
                case 6:
                    return 2;
                case 7:
                    return 1;
            }
        }
    },
    getWheelDelta:function(event){
        if(event.wheelDelta){
            return (client.engine.opera && client.engine.opera < 9.5 ? -event.wheelDelta : event.wheelDelta);
        }else{
            return -event.detail * 40;
        }
    },
    getCharCode:function(event){
        if(typeof event.charCode == "number"){
            return event.charCode;
        }else{
            return event.keyCode;
        }
    }
};

EventUtil.addHandler(window , "load" , function(event){
    var div = document.getElementById("navleft");
    EventUtil.addHandler(div , "contextmenu" , function(event){

        event = EventUtil.getEvent(event);
        EventUtil.preventDefault(event);

        showRightSetting(event);
    });

    EventUtil.addHandler(document , "click" , function(event){
        document.getElementById("channelSetting").style.display = "none";
    });
});

function f1(pageSize) {
    console.log(pageSize)
    var scrolldiv = document.getElementById("show");
    if(scrolldiv.scrollHeight > scrolldiv.clientHeight) {
        setTimeout(function(){
            //设置滚动条到最底部
            scrolldiv.scrollTop = scrolldiv.scrollHeight;
        },0);
    }
    EventUtil.addHandler(scrolldiv , "scroll" , function(event){
        const scrollTop = scrolldiv.scrollTop;
        console.log("EventUtil:"+scrollTop);
        if(scrollTop < 50){
            refreshScroll();
        }
    });
}

var channelSettingID = "";
function showRightSetting(event) {
    if(event.target.id!=""){
        $("#channelSetting").css("top", event.clientY);
        $("#channelSetting").css("left", event.clientX);
        $("#channelSetting").css("display", "block");
        channelSettingID = event.target.id.split("_")[1];
    }
}

