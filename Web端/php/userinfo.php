<?php 
$css = '"../css/user.css"';
include ('../html/head.php');
?>
 <body>  
    <div id="box">
        <div id="bar">
            <ul >
                <li>
                    <a href="del.php" target="_top">退出登录 </a>
                </li>
                <li>
                    <a href="#" >当前用户:<?php echo $_COOKIE['user']; ?> </a>
                </li>
            </ul>
        </div>
        <div id="space">
            <div id="pic">
                <img src="../res/logo.png"/>
            </div>
        </div>
    </div>
 </body>
 </html>
