package info.investdigital.entity;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 11:30  2018/3/7.
 */
@Data
public class EthTransactionEntity {
    private String nonce;
    private String gasPrice = "0x0010";
    private String gasLimit = "0x47e7c4";
    private String from;
    private String to ;
    private String value;
    private String data;

    public EthTransactionEntity(String nonce, String from, String to, String value, String data) {
        this.nonce = nonce;
        this.from = from;
        this.to = to;
        this.value = value;
        this.data = data;
    }
    public EthTransactionEntity(String nonce, String from, String to, String value) {
        this.nonce = nonce;
        this.from = from;
        this.to = to;
        this.value = value;
    }
}
