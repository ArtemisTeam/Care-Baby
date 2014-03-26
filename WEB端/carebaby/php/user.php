<?php 
require '../fun/fun.php'; 
if (isset($_COOKIE['user'])) {
    $user = $_COOKIE['user'];
} else {
    $url = "../php/login.php";
    goto_page($url);
}
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $num  = $_REQUEST['num'];
    if ($num<=0) {
        $num=1;
    }
} else {
    $num = 5;
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=7ab31174cbd4356ea46ef2f885c6373a"></script>
  <script src="https://cn.avoscloud.com/scripts/lib/av-0.2.7.min.js"></script>
  <script src="../res/res.js"></script>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title><?php echo $user ?></title>
<link href="../css/user.css" rel="stylesheet" type="text/css" media="all" />
</head>
<body style="background-color: rgb(247, 247, 242);">
<div id="page" class="container">
    <div id="header" style="background-color: rgb(222, 222, 222); line-height: 44px;">
        <div id="logo" style="line-height: 31px;">
            <img class="avatar" alt="kang" src="http://wherebaby.duapp.com/res/avatar.png" style="box-shadow: rgba(102, 112, 89, 0.239216) 3px 9px 4px -2px;">
            <h1 style="color: rgb(0, 0, 0);"><a href="" style="color: rgb(0, 0, 0);"><?php echo $user ?></a></h1>
            <h1><a href="http://wherebaby.duapp.com/php/del.php" style="font-size: 22px; color: rgb(100, 107, 95);">退出登录</a></h1>
        </div>
        <div id="menu">
            <form action="http://wherebaby.duapp.com/php/user.php" method="post">
                <ul>
                    <li><a href="http://wherebaby.duapp.com/php/sms.php" class="" style="background-color: rgba(53, 59, 49, 0.341176); font-size: 18px; line-height: 0px; padding: 31px 22px;">查看短信</a></li>
                    <li><a href="http://wherebaby.duapp.com/php/call.php" style="line-height: 0px; font-size: 18px; background-color: rgba(53, 59, 49, 0.341176); opacity: 1; padding: 31px 22px;">查看通话</a></li>
                    <li style="height: 70px; font-size: 35px; line-height: 64px;"><input type="text" class="inputs" name="num" style="margin:25px 80px" placeholder="最近位置,请输入数字"></li>
                    <li><input class="log_sub" type="submit" value="确认" style="font-size: 20px; color: white;font-weight: bold;margin:25px 75px;
                                background:#2a2a2a; width: 160px;height: 35px;float: left;position: relative;"></li>
                </ul>
            </form> 
        </div>
    </div>
    <div id="main" style="padding: 4px 0px 0px 4px; width :800px;height: 800px;">
        <div id="banner">
            <div id="allmap" style="width:800px; height: 800px;overflow: hidden;margin:0;"></div>
        </div>
    </div>
</div>
</body>
</html>

<script type="text/javascript">


function changeWindow(){
    var w = document.documentElement.clientWidth;
    var h = document.documentElement.clientHeight; 
    var wM = w-320;
    var wC = wM-2;
    document.getElementById('main').style.width    = wM+"px";
    document.getElementById('banner').style.width  = wC+"px";
    document.getElementById('allmap').style.width  = wC+"px";

    document.getElementById('page').style.height   = h +"px";
    document.getElementById('header').style.height = h +"px";
    document.getElementById('main').style.height   = h+"px";
    document.getElementById('banner').style.height = h+"px";
    document.getElementById('allmap').style.height = h+"px";
}

init();
/*Baidu Map*/
var map = new BMap.Map("allmap");
var num =<?php echo $num ?>;

var user="<?php echo $_COOKIE['user']; ?>_child";
var Baidu_Location = AV.Object.extend("Location");
var query = new AV.Query(Baidu_Location);
query.equalTo("username", user);
query.descending("createdAt");
query.limit(num);
query.select("Latitude","Longtitude");


var point = new Array();

query.find({
  success: function(results) {

    if(results.length == 0){
        var point_default = new BMap.Point(116.404, 39.915);
        var marker = new BMap.Marker(point_default);
        marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
        map.addOverlay(marker);

        var infoWindow1 = new BMap.InfoWindow("当前坐标数为0");
        marker.addEventListener("click", function(){this.openInfoWindow(infoWindow1);});
        map.openInfoWindow(infoWindow1,point_default);
        map.centerAndZoom(point_default,11);  
        map.enableScrollWheelZoom();                            //启用滚轮放大缩小
        
    } else {
        var i = 0;
        var object = results[i];
        var x=object.get('Longtitude');
        var y=object.get('Latitude');
        point[i] = new BMap.Point(x,y);
        addMarkerJump(point[i],object.createdAt.toUTCString());
        for (i++; i < results.length; i++) {
            var object = results[i];
            var x=object.get('Longtitude');
            var y=object.get('Latitude');
            point[i] = new BMap.Point(x,y);
            addMarker(point[i],object.createdAt.toUTCString());   
        }
        map.centerAndZoom(point[0], 11);
        var polyline = new BMap.Polyline(point, {strokeColor:"blue", strokeWeight:3, strokeOpacity:0.5});
        map.addOverlay(polyline);
        map.enableScrollWheelZoom();
    }
  },
  error: function(error) {
    alert("Error: " + error.code + " " + error.message);
  }
});
window.onload=function(){  
    changeWindow();  
}  
            //当浏览器窗口大小改变时，设置显示内容的高度  
window.onresize=function(){  
    changeWindow();  
} 
setTimeout(function(){map.setZoom(14);}, 1000); 
setTimeout(function(){map.setZoom(16);}, 2000); 
setTimeout(function(){map.setZoom(18);}, 3000); 
changeWindow();




/***************************************************************************************************
 * 添加跳跃标点
 ***************************************************************************************************/
function addMarkerJump(point,_time){
    var marker = new BMap.Marker(point);
    map.addOverlay(marker);
    var infoWindow1 = new BMap.InfoWindow("宝贝在这!!!</br>位置更新时间:</br>"+_time);
    marker.addEventListener("click", function(){this.openInfoWindow(infoWindow1);});
    map.openInfoWindow(infoWindow1,point);
    marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
}

/***************************************************************************************************
 * 添加普通标点
 ***************************************************************************************************/
function addMarker(point,_time){
    var marker = new BMap.Marker(point);
    map.addOverlay(marker);
    var infoWindow = new BMap.InfoWindow("位置更新时间:</br>"+_time);
    marker.addEventListener("click", function(){this.openInfoWindow(infoWindow);});
}


</script>


