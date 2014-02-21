<?php 
    if($_SERVER['REQUEST_METHOD'] == 'POST'){
        $num  = $_REQUEST['num'];
    } else {
        $num = 5;
    }
 ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<link href="../css/map.css" type="text/css" rel="Stylesheet"/>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=7ab31174cbd4356ea46ef2f885c6373a"></script>
<script src="https://cn.avoscloud.com/scripts/lib/av-0.2.7.min.js"></script>
  <script src="../res/res.js"></script>
</head>
<body>
    <div id = "allmap"></div>
    <div id = "option">
        <form action="../php/map.php" method="post">
            <input  type="text" class="inputs" name="num"/>
            <input class="log_sub" type="submit" value="确认信息" >
        </form>
    </div>
</body>
</html>

<script type="text/javascript">
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
    var i = 0;
    var object = results[i];
    var x=object.get('Longtitude');
    var y=object.get('Latitude');
    point[i] = new BMap.Point(x,y);
    addMarkerJump(point[i],object.updatedAt);

    for (i++; i < results.length; i++) {
        var object = results[i];
        var x=object.get('Longtitude');
        var y=object.get('Latitude');
        point[i] = new BMap.Point(x,y);
        addMarker(point[i],object.updatedAt);   
    }
    map.centerAndZoom(point[0], 17);
    var polyline = new BMap.Polyline(point, {strokeColor:"blue", strokeWeight:3, strokeOpacity:0.5});
    map.addOverlay(polyline);
    map.enableScrollWheelZoom();
  },
  error: function(error) {
    alert("Error: " + error.code + " " + error.message);
  }
});





/***************************************************************************************************
 * 添加跳跃标点
 ***************************************************************************************************/
function addMarkerJump(point,_time){
    var marker = new BMap.Marker(point);
    map.addOverlay(marker);
    var infoWindow1 = new BMap.InfoWindow("宝贝在这!!!</br>"+_time);
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
    var infoWindow = new BMap.InfoWindow("宝贝在这!!!</br>"+_time);
    marker.addEventListener("click", function(){this.openInfoWindow(infoWindow);});
}


</script>


