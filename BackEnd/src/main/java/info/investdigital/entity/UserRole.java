package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ccl
 * @create 2018-03-07 09:43
 **/
@Entity
@Data
@Table(name = "sys_user_role")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long roleId;

    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRole() {
    }
}
