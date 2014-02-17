<?php 

/*************************************************************************************************
 * 打开数据库
 * 引用函数文件
 *************************************************************************************************/
require '../system/connect.php';     
require '../fun/fun.php';  
session_start();

    $type = $_REQUEST['type'];
    switch ($type) {

// /*************************************************************************************************
//  * 儿童端发送坐标
//  *************************************************************************************************/
//         case 'child':   //
//             # code...
//             $x    = $_REQUEST['x'];
//             $y    = $_REQUEST['y'];
//             $info = $_REQUEST['info'];
//             $user = $_REQUEST['username'];
//             if( insert_x_y( $user , $info , $x , $y ) ){
//                 $_SESSION["temp"]='{"state":"1"}';                      //插入坐标成功
//                 echo $_SESSION['temp'];
//                 session_destroy();
//             } else {
//                 $_SESSION["temp"]='{"state":"0"}';                      //插入坐标失败
//                 echo $_SESSION['temp'];
//                 session_destroy();
//             }
//             break;

// /*************************************************************************************************
//  * 家长设置儿童端发送频率
//  *************************************************************************************************/
//         case '1':   //
//             # code...
//             $user = $_REQUEST['username'];
//             $set  = $_REQUEST['set'];
//             if( update_set($user,$set) ){
//                 $_SESSION["temp"]='{"state":"1"}';                      //插入坐标成功
//                 echo $_SESSION['temp'];
//                 session_destroy();
//             } else {
//                 $_SESSION["temp"]='{"state":"0"}';                      //插入坐标失败
//                 echo $_SESSION['temp'];
//                 session_destroy();
//             }
//             break;

/*************************************************************************************************
 * 新用户注册
 *************************************************************************************************/
        case 'signin':   //注册
            # code...
            $user = $_REQUEST['username'];
            $pass = $_REQUEST['password'];
            if( user_signin($user,$pass) ){
                $_SESSION["temp"] = '{"state":"1"}';
                echo $_SESSION['temp'];
                session_destroy();
            } else {
                $_SESSION["temp"] = '{"state":"0"}';
                echo $_SESSION['temp'];
                session_destroy();                
            }
            break;
/*************************************************************************************************
 * 用户登录
 *************************************************************************************************/
        case 'login':   //登陆
            # code...
            $user = $_REQUEST['username'];
            $pass = $_REQUEST['password'];
            if( user_login($user,$pass) ){
                $_SESSION["temp"] = '{"state":"1"}';
                echo $_SESSION['temp'];
                session_destroy();
            } else {
                $_SESSION["temp"] = '{"state":"0"}';
                echo $_SESSION['temp'];
                session_destroy();                
            }
            break;
/*************************************************************************************************
 * 儿童端发来位置等消息
 *************************************************************************************************/
        case 'location':   //接收儿童端info(bat/x/y)
            # code...
            $user = $_REQUEST['username'];
            $info = $_REQUEST['info'];
            $x    = $_REQUEST['x'];
            $y    = $_REQUEST['y'];
            if( insert_x_y( $user , $info , $x , $y ) ){
                $_SESSION["temp"] = '{"state":"1"}';
                echo $_SESSION['temp'];
                session_destroy();
            } else {
                $_SESSION["temp"] = '{"state":"0"}';
                echo $_SESSION['temp'];
                session_destroy();                
            }
            break;

/*************************************************************************************************
 * 家长查询儿童位置信息
 * 接收 用户名
 *************************************************************************************************/
        case 'request':   //查询位置
            # code...
            $num      = $_REQUEST['num'];
            $user     = $_REQUEST['username'];

            $json     = select_point($user,$num);                   //合成json
            $json_out ='{    "point":[';
            for($n = 0 ; $n<$num ; $n++){
                if( isset(  $json[$n][0]) ){                    //判断数组是否有值
                    $info = $json[$n][0];
                    $x    = $json[$n][1];
                    $y    = $json[$n][2];
                    $time = $json[$n][3];
                    $json_out.='{"flag":"1" ,"info":"'.$info.'" ,"x":"'.$x.'" , "y":"'.$y.'","timestamp":"'.$time.'"}';
                    
                    if( $n != ($num-1) ){
                        $json_out.=',';
                    }
                } else {
                    
                    $json_out.='{"flag":"0" ,"info":"0" ,"x":"0" , "y":"0","timestamp":"0"}';
                    
                    if( $n != ($num-1) ){
                        $json_out.=',';
                    }            
                }
            }
                
            $json_out.=']}';

            $_SESSION["temp"]=$json_out;
            echo $_SESSION['temp'];
                session_destroy(); 

            break;                
/*************************************************************************************************
 * 未知请求
 * 版本错误…/&&*$#@%^&@#%#……  
 *************************************************************************************************/
        default:
            # code...
            $_SESSION["temp"] = '{"state":"0"}';
            echo $_SESSION['temp'];
            session_destroy(); 
            break;
    }
 ?>