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
  <script src="https://cn.avoscloud.com/scripts/lib/av-0.2.7.min.js"></script>
  <script src="../res/res.js"></script>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title><?php echo $user ?></title>
<link href="../css/call.css" rel="stylesheet" type="text/css" media="all" />
</head>
<body >
<div id="page" class="container">
    <div id="header" style="background-color: rgb(222, 222, 222); line-height: 44px;">
        <div id="logo" style="line-height: 31px;">
            <img class="avatar" alt="kang" src="http://wherebaby.duapp.com/res/avatar.png" style="box-shadow: rgba(102, 112, 89, 0.239216) 3px 9px 4px -2px;">
            <h1 style="color: rgb(0, 0, 0);"><a href="" style="color: rgb(0, 0, 0);"><?php echo $user ?></a></h1>
            <h1><a href="http://wherebaby.duapp.com/php/del.php" style="font-size: 22px; color: rgb(100, 107, 95);">退出登录</a></h1>
        </div>
        <div id="menu">
            <form action="http://wherebaby.duapp.com/php/call.php" method="post">
                <ul>
                    <li><a href="http://wherebaby.duapp.com/php/user.php" class="" style="background-color: rgba(53, 59, 49, 0.341176); font-size: 18px; line-height: 0px; padding: 31px 22px;">查看位置</a></li>
                    <li><a href="http://wherebaby.duapp.com/php/sms.php" style="line-height: 0px; font-size: 18px; background-color: rgba(53, 59, 49, 0.341176); opacity: 1; padding: 31px 22px;">查看短信</a></li>
                    <li style="height: 70px; font-size: 35px; line-height: 64px;"><input type="text" class="inputs" name="num" style="margin:25px 80px" placeholder="最近短信,请输入数字"></li>
                    <li><input class="log_sub" type="submit" value="确认" style="font-size: 20px; color: white;font-weight: bold;margin:25px 75px;
                                background:#2a2a2a; width: 160px;height: 35px;float: left;position: relative;"></li>
                </ul>
            </form> 
        </div>
    </div>
    <div id="main" style="padding: 4px 0px 0px 4px; width :800px;">
        <div id="banner">
            <div class="content" id="callList">
                <div class="contacts-head-box">
                    <table class="tb-contacts-list" cellspacing="0">
                        <thead id="callListHead">
                            <tr>
                                <th class="col0">&nbsp;</th>
                                <th class="col1">&nbsp;</th>
                                <th class="col3">&nbsp;</th>
                                <th class="col4">姓名</th>
                                <th class="col5">号码</th>
                                <th class="col3">&nbsp;</th>
                                <th class="col3">&nbsp;</th>
                                <th class="col6">通话时间</th>
                                <th class="col3">&nbsp;</th>
                                <th class="col3">&nbsp;</th>
                                <th class="col7">状态信息</th>
                            </tr>
                        </thead>
                    </table>
                </div>
                <div id="callListBox" class="over-scroll ie6" style="height: 866px;">
                    <table class="tb-contacts-list logClass" cellspacing="0" name="call_select">
                        <colgroup><col class="col1">
                        <col class="col2">
                        <col class="col3">
                        <col class="col4">
                        <col class="col5">
                        <col class="col6">
                        <col class="col7">
                        </colgroup>

                        <tbody id="callListBody" class="ui-selectable">

                        </tbody>
                    </table>
                </div>
            </div>
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
    // document.getElementById('allmap').style.width  = wC+"px";

    // document.getElementById('page').style.height   = h +"px";
    // document.getElementById('header').style.height = h +"px";
    // document.getElementById('main').style.height   = h+"px";
    // document.getElementById('banner').style.height = h+"px";
    // document.getElementById('allmap').style.height = h+"px";
}

init();
/*Baidu Map*/
// var map = new BMap.Map("allmap");
var num =<?php echo $num ?>;

var user="<?php echo $_COOKIE['user']; ?>";
var call_query = AV.Object.extend("CallHistory");
var query = new AV.Query(call_query);
query.equalTo("username", user);
query.descending("createdAt");
query.limit(num);
query.select("caller_name","calltime","phonenumber","FLAG");


// var point = new Array();

query.find({
  success: function(results) {

    if(results.length == 0){
        document.getElementById('callListBody').innerHTML = "NULL";
        
    } else {
        var i = 0;
        for (i=0 ; i < results.length; i++) {
            var object = results[i];
            var w=object.get('caller_name');
            var x=object.get('calltime');
            var y=object.get('phonenumber');
            var z=object.get('FLAG');
            if(z == 1){
                z = "呼入";
            } else if( z == 2){
                z = "呼出";
            } else {
                z = "未接"; 
            }

// 准备sms
            var callListBody = document.getElementById('callListBody');
            var call_List_tr = document.createElement('tr');
            call_List_tr.class="ui-selectee selected ui-selected";
            call_List_tr.dataid='12';
            call_List_tr.innerHTML = "<td>&nbsp;</td><td><i class='icons icon-check logClass icon-checked'></i></td><td><i class='icons icon-call-in'></i></td><td><span class='no-drag'>"+w+"</span></td><td><span class='no-drag'>"+y+"</span></td><td>"+x+"</td><td>"+z+"</td>";
            
            callListBody.appendChild(call_List_tr);
        }
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
changeWindow(); 




</script>


