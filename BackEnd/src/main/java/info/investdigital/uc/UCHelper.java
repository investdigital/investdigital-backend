package info.investdigital.uc;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author ccl
 * @time 2018-03-09 13:43
 * @name UCHelper
 * @desc:
 */
@Slf4j
public class UCHelper {
    public static String login(String username,String pwd){
        Client e = new Client();
        String result = e.uc_user_login(username,pwd);
        String $ucsynlogin = "";
        LinkedList<String> rs = XMLHelper.uc_unserialize(result);
        if(rs.size() > 0){
            int $uid = Integer.parseInt(rs.get(0));
            String $username = rs.get(1);
            String $password = rs.get(2);
            String email = rs.get(3);

            if($uid > 0){
                $ucsynlogin = e.uc_user_synlogin($uid);
            }else if($uid == -1){
                log.error("user not exist");
            }else if($uid == -2){
                log.error("Invalid password");
            }else{
                log.error("undefined");
            }
        }else{
            log.error("login failed");
            log.error(result);
        }
        return $ucsynlogin;
    }

    public static String logout(){
        Client uc = new Client();
        String $ucsynlogout = uc.uc_user_synlogout();
        log.info("logout success "+ $ucsynlogout);
        return $ucsynlogout;
    }

    public static void reg(String username ,String password, String email){
        Client uc = new Client();
        String $returns = uc.uc_user_register(username,password,email);
        int $uid = Integer.parseInt($returns);
        if($uid <= 0){
            if($uid == -1){
                log.error("Invalid username");
            }else if($uid == -2){
                log.error("Invalid phrase");
            }else if($uid == -3){
                System.out.printf("username exist");
            }else if($uid == -4){
                log.error("Invalid email");
            }else if($uid == -5){
                log.error("emial not allowed");
            }else if($uid == -6){
                log.error("email exist");
            }else{
                log.error("undefined");
            }
        }else {
            log.info("OK:---------"+$returns);
        }
    }

}
