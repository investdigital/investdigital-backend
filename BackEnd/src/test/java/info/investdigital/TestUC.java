package info.investdigital;

import info.investdigital.uc.Client;
import info.investdigital.uc.UCHelper;
import info.investdigital.uc.XMLHelper;

import java.util.LinkedList;

/**
 * @author ccl
 * @time 2018-03-09 11:04
 * @name TestUC
 * @desc:
 */
public class TestUC {
       public static void main(String[] args) {
        //reg("cherrish","123456","chcl@163.com");
        System.out.println(UCHelper.login("cherrish","123456"));
        System.out.println();
    }
}
