package info.investdigital.AssetsAPI.binance.exception;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-03-29 10:44
 **/
@Data
public class BinanceException extends RuntimeException{
    private Integer code;
    private String msg;
    public BinanceException(Integer code, String msg){
        super(String.format("errorCode %s errorMsg %s", code ,msg));
        this.code = code;
        this.msg = msg;
    }
}
