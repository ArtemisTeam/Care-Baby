<?php
 
//替换为自己的数据库名（可从管理中心查看到）
$dbname = 'aBnJSTIGPeDFPpZdnjTL';
 
//从环境变量里取出数据库连接需要的参数
$host = 'sqld.duapp.com';
$port =  4050;
$user = 'E6dzOOIGvUgmljYIy5TAXLn8';
$pwd  = 'yaa9HeuiHixNpPa33Fd4GplhGf0SwhdN';
 
//接着调用mysql_connect()连接服务器
global $link;
$link = @mysql_connect("{$host}:{$port}",$user,$pwd,true);
if(!$link) {
  die("Connect Server Failed: " . mysql_error());
}
/*连接成功后立即调用mysql_select_db()选中需要连接的数据库*/
if(!mysql_select_db($dbname,$link)) {
  die("Select Database Failed: " . mysql_error($link));
}

?>