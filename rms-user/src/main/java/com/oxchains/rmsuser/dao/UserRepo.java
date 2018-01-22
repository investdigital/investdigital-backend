package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface UserRepo extends CrudRepository<User,Long> {
    /**
     * find by loginname
     * @param loginname
     * @return User
     */
    User findByLoginname(String loginname);

    User findByEmail(String email);

    /**
     * 通过手机查找
     * @param mobilephone
     * @return User
     */
    User findByMobilephone(String mobilephone);

    /**
     * find by login and password
     * @param loginname
     * @param password
     * @return Optional<User>
     */
    Optional<User> findByLoginnameAndPassword(String loginname, String password);
    Optional<User> findByLoginnameAndPasswordAndEnabled(String loginname, String password, Integer enabled);

    /**
     * find by email and password
     * @param loginname
     * @param password
     * @return Optional<User>
     */
    Optional<User> findByEmailAndPassword(String loginname, String password);
    Optional<User> findByEmailAndPasswordAndEnabled(String loginname, String password, Integer enabled);

    /**
     * find by phone and password
     * @param loginname
     * @param password
     * @return Optional<User>
     */
    Optional<User> findByMobilephoneAndPassword(String loginname, String password);
    Optional<User> findByMobilephoneAndPasswordAndEnabled(String loginname, String password, Integer enabled);

    /**
     * findByRoleId
     * @return
     */
    //List<User> findByRoleId(Long roleId);

}
