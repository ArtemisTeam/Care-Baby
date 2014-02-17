<?php
require '../system/connect.php';          
require '../fun/fun.php';          
if (isset($_COOKIE['user'])) {
    $url = "../php/user.php";
    echo "<script language='javascript' type='text/javascript'>";
    echo "window.location.href='$url'";
    echo "</script>";
}
$flag=0;
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $user = $_POST['user'];
    $pass = $_POST['password'];
    if ($user == '' || $pass == '') {
        $flag=1;
    } else {
        if( user_login($user,$pass) ){
            setcookie('user',$user,time()+600);
            $url = "../php/user.php";
            goto_page($url);
        } else {
            $flag = 1;
        }
    }
}

$title = '宝贝在哪';
$css   = '"../css/css.css"';
include ('../html/head.php');
?>
<body>
    <div id="box">
        <div id="bar">
            <ul >
                <li><a href="../php/signin.php" >注册</a></li>
            </ul>
        </div>

        <div id="space">
            
        </div>
        <div id="contain">
            <div id="mid">
                
                <div id="pic"><img src="../res/2.jpg" width="503" height="396" /></div>
                <div id="sprow"></div>
                <div id="login">
                    <div id="title"></div>
                    <form action="../php/login.php" method="post">
                        
                        <div class="sp"></div>
                        <input  type="text" class="inputs" name="user"/>
                        <div class="sp"></div>
                        <input  type="password" class="inputs" name="password"/>
                        <div class="sp">
                        <?php 
                            if($flag){
                                echo "<span class=\"wrong\">请填写正确的用户名密码</span>";
                            }
                         ?>
                        </div>
                        <input class="log_sub" type="submit" value="登  陆" >
                    </form>
                </div>

            </div>
        </div>
    </div>
</body>
</html>