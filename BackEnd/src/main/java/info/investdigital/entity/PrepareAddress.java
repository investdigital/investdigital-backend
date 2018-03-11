package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: huohuo
 * Created in 18:23  2018/3/7.
 */
@Data
@Entity
@Table(name = "prepare_address")
public class PrepareAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String address;
}
