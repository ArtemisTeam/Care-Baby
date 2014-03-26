<?php 
    require '../fun/fun.php';
    setcookie("user", "", time()-600);
    $url = '../php/login.php';
    goto_page($url);
?>