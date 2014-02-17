<?php 
    session_start();
    include 'connect.php';
    include '../fun/fun.php';
    $user = 'admin';
    $num      = 5;
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
 ?>