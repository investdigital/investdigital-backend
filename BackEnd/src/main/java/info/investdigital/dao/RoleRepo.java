package info.investdigital.dao;

import info.investdigital.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface RoleRepo extends CrudRepository<Role,Long> {

    Role findByRoleName(String roleName);
    Role findByRoleSign(String roleSign);


    Page<Role> findAll(Pageable pageable);
    Page<Role> findByRoleNameLike(String roleName, Pageable pageable);

    List<Role> findByIdIn(List<Long> ids);
}
