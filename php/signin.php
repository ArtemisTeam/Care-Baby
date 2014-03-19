<?php    
require '../fun/fun.php'; 

$title = '注册';
$css   = '"../css/rescss.css"';
include ('../html/head.php');

$user ='';
$pass ='';

$flag = 0;
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    $user =$_POST["user"];
    $pass =$_POST["password"];
    $pass1=$_POST["password1"];
    if ( $pass1 != $pass ) {
        $flag = 1;
    }
}
?>
<body>
<script type="text/javascript">
    var username = "<?php echo $user ?>";
    var password = "<?php echo $pass ?>";
    init();
    if(username && password){
        var user_p = new AV.User();
        user_p.set("username", username+"_parent");
        user_p.set("password", password);
        user_p.signUp(null, {
          success: function(user) {
          },
          error: function(user, error) {
          }
        });  

        var user = new AV.User();
        user.set("username", username+"_child");
        user.set("password", password);
        user.signUp(null, {
          success: function(user) {
            setCookie("user",username,1);
            window.location.href="../php/user.php";
            // Hooray! Let them use the app now.
          },
          error: function(user, error) {
            // Show the error message somewhere and let the user try again.
            alert("Error: " + error.code + " " + error.message);
          }
        });    
    }
    
    function setCookie(c_name,value,expiredays)
    {
        var exdate=new Date()
        exdate.setDate(exdate.getDate()+expiredays)
        document.cookie=c_name+ "=" +escape(value)+
        ((expiredays==null) ? "" : "; expires="+exdate.toGMTString())
    }
</script>
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
                        <input  type="text" class="inputs" name="user" placeholder="用户名"/>
                        <div class="sp">
                        <?php 
                        if($flag == -1){
                            echo "<span class=\"wrong\">用户名已存在!</span>";
                        }?>
                        </div>
                        <input  type="password" class="inputs" name="password" placeholder="密码"/>
                        <div class="sp"></div>
                        <input  type="password" class="inputs" name="password1" placeholder="重复密码"/>
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