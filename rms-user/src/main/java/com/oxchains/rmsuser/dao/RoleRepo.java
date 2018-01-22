package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface RoleRepo extends CrudRepository<Role,Long> {


}
