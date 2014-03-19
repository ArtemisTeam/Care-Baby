<?php        
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
} else {
    $user = "";
    $pass = "";
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
  <meta name="baidu-tc-cerfication" content="32661ceb57411a61bd61aa296a75489b" />
  <link href="../css/css.css" type="text/css" rel="Stylesheet"/>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
  <script src="https://cn.avoscloud.com/scripts/lib/av-0.2.7.min.js"></script>
  <script src="../res/res.js" ></script>
  <title>宝贝在哪</title>
<script type="text/javascript">
    init();
    var username="<?php echo $user ?>";
    var psw ="<?php echo $pass ?>"
    AV.User.logIn(username+"_child",psw , {
        success: function(user) {
        // Do stuff after successful login.
        setCookie("user",username,1);
        window.location.href='../php/user.php';
    },
        error: function(user, error) {
            if(username!="" || psw!=""){
                alert("用户名或密码错误");
            }
        // The login failed. Check error to see why.
    }
    });

    function setCookie(c_name,value,expiredays)
        {
        var exdate=new Date()
        exdate.setDate(exdate.getDate()+expiredays)
        document.cookie=c_name+ "=" +escape(value)+
        ((expiredays==null) ? "" : "; expires="+exdate.toGMTString())
    }
</script>
</head>
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
                        <input  type="text" class="inputs" name="user" placeholder="用户名"/>
                        <div class="sp"></div>
                        <input  type="password" class="inputs" name="password" placeholder="密码"/>
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