<?php
require '../system/connect.php';     
require '../fun/fun.php';     
$flag = 0;
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $user =$_POST["user"];
    $pass =$_POST["password"];
    $pass1=$_POST["password1"];
    if ( $pass1 == $pass ) {
        if( user_signin($user,$pass) ){
            setcookie('user',$user,time()+600);
            $url = "../php/user.php";
            goto_page($url);
        } else {
            $flag = -1;
        }
    } else {
        $flag = 1;
    }
}

$title = '注册';
$css   = '"../css/rescss.css"';
include ('../html/head.php');
?>
<body>
    <div id="box">
        <div id="bar">
        <ul >
        <li>
        <a href="../php/login.php" >登陆</a>
        </li>
        </ul>
        </div>
        <div id="space"></div>
        <div id="contain">
                <div id="login">
                    <div id="title"><span class="_signin">注册</span></div>
                    <form action="../php/signin.php" method="post">
                        <div class="sp"></div>
                        <input  type="text" class="inputs" name="user"/>
                        <div class="sp">
                        <?php 
                        if($flag == -1){
                            echo "<span class=\"wrong\">用户名已存在!</span>";
                        }?>
                        </div>
                        <input  type="password" class="inputs" name="password"/>
                        <div class="sp"></div>
                        <input  type="password" class="inputs" name="password1"/>
                        <div class="sp">
                        <?php 
                            if($flag == 1){
                            echo "<span class=\"wrong\">请确认密码一致!</span>";
                        }?>
                        </div>
                        <input class="log_sub" type="submit" value="确认信息" >
                    </form>
                </div>
        </div>
    </div>
</body>
</html>