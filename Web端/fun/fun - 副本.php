<?php
/**************************************************************************************************
 * 用户登陆
 * 失败返回false
 **************************************************************************************************/
function user_login($user,$pass)
{
    global $link;
    $sql      = "Select pass from users where username='$user'";
    $result   = mysql_query($sql,$link);
    $sql_pass = mysql_fetch_array($result);
    if( sha1($pass) == $sql_pass['0'] ){                        //判断密码一致          
        return true;
    } else {
        return false;
    }
}

/**************************************************************************************************
 * 用户注册
 * 失败返回false
 **************************************************************************************************/
function user_signin($user,$pass)
{
    global $link;
    $sql    = "SELECT pass FROM users WHERE username='$user'";
    $result = mysql_query($sql,$link);
    if (mysql_num_rows($result)>0) {
            return false;                                       //用户名存在
    } else {
        $sql = "INSERT INTO users VALUES(NULL,'$user','email',sha1('$pass'),now())";
        mysql_query($sql,$link);

        $id  = get_id($user);                                    //获取id

        $sql = "CREATE TABLE _info$id(
                    info  CHAR(4),
                    x     CHAR(20),
                    y     CHAR(25),
                    _time TIMESTAMP(14)
                )";
        mysql_query($sql,$link);                                //用id建 两个 表    _set$id 储存设置    _info$id 手机/位置信息

        $sql = "CREATE TABLE _set$id(
                    _set  CHAR(5) ,
                    _time TIMESTAMP(14)
                )";
        mysql_query($sql,$link);                                

        $time = get_time();                                     //为 _set$id 初始化 默认设置刷新时间300s
        $sql  = "insert into _set$id values('300','".$time."')";
        mysql_query($sql,$link);

        return true;
    }  
}

/**************************************************************************************************
 * 获取id
 **************************************************************************************************/
function get_id($user)
{
    global $link;                                               //查询id
    $sql      = "SELECT user_id FROM users WHERE username='$user'";       
    $result   = mysql_query($sql,$link);
    $sql_pass = mysql_fetch_array($result);
    return $sql_pass['0'];
}

/**************************************************************************************************
 * 页面跳转
 **************************************************************************************************/
function goto_page($url)
{
        echo "<script language='javascript' type='text/javascript'>";
        echo "window.location.href='$url'";
        echo "</script>";
}

/**************************************************************************************************
 * 返回时间戳 
 * 方便数据库操作
 **************************************************************************************************/
function get_time()
{
    return date('Y-m-d H:i:s');
}

/**************************************************************************************************
 * 插入坐标
 * @param  $user 用户名
 * @param  $info 电量
 * @param  $x
 * @param  $y
 * @return 成功返回:true 失败返回:false
 **************************************************************************************************/
function insert_x_y( $user , $info , $x , $y )
{
    global $link; 
    $id   = get_id($user);                                      
    $time = get_time();        
    $sql  = "INSERT INTO _info$id VALUES( '$info','$x','$y','$time' )";  
                                                        //echo $sql;     
    if( mysql_query($sql,$link) ){
        return true;
    } else {
        return false;
    }
}

/**************************************************************************************************
 * 移动端获取坐标
 * 参数1:用户名
 * 参数2:需求点坐标数量
 * sql_result[][]
 **************************************************************************************************/
function select_point($user,$num)
{
    global $link; 
    $id   = get_id($user);                                      
                                                                // $time = get_time();        
    $sql  = "SELECT *FROM _info$id ORDER BY _time DESC limit 0,$num";  
                                                                // echo $sql;     
    if( mysql_query($sql,$link) ){
        $result   = mysql_query($sql,$link);
        for($n=0;$n<$num;$n++){
            $sql_result[] = mysql_fetch_array($result);
        }
                                                                // print_r($sql_result);
                                                                // echo "</br>";
        return $sql_result;
    } else {
        return false;                                           //待商议  可加入特定json代表查询失败
    }   
}

/**************************************************************************************************
 * web获取坐标
 * 参数1:用户名
 * 参数2:需求点坐标数量
 * sql_result[][]
 **************************************************************************************************/
function select_point_web($user,$num)
{
    global $link; 
    $id   = get_id($user);                                      
    $sql  = "SELECT x,y,_time FROM _info$id ORDER BY _time DESC limit 0,$num";  
    if( mysql_query($sql,$link) ){
        $result   = mysql_query($sql,$link);
        for($n=0;$n<$num;$n++){
            $sql_result[] = mysql_fetch_array($result);
        }
        return $sql_result;
    } else {
        return false;                                          
    }   
}

/**************************************************************************************************
 * 修改 _set 刷新频率
 * 参数 1:用户名
 * 参数 2:设置刷新间隔
 * 返回值:bool
 **************************************************************************************************/
function update_set($user,$set)
{
    global $link; 
    $id   = get_id($user);                                      
    $time = get_time();        
    $sql  = 'update _set'.$id.' set _set='.$set.',_time="'.$time.'"';  
    echo $sql;
    if( mysql_query($sql,$link) ){
        return true;
    } else {
        return false;
    }
}

?>

   