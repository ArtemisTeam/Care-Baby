<?php 
require '../system/connect.php';     
include '../fun/fun.php';
if (isset($_COOKIE['user'])) {
    $user = $_COOKIE['user'];
} else {
    $url = "../php/login.php";
    goto_page($url);
}
$title = $user;
include ('../html/head.php');
?>

<frameset rows="19%,81%">
    <frame src="userinfo.php" frameborder="0" noresize="noresize">
    <frame id = "map" src="../php/map.php" frameborder="0" noresize="noresize">
</frameset>
</html>