package info.investdigital.dao;

import info.investdigital.entity.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface UserRoleRepo extends CrudRepository<UserRole,Long> {

    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    UserRole findByUserIdAndRoleId(Long userId,Long roleId);

    int countByUserId(Long userId);
    int countByRoleId(Long roleId);
}
