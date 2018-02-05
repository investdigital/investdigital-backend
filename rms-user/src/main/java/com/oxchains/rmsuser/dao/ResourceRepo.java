package com.oxchains.rmsuser.dao;

import com.oxchains.rmsuser.entity.Menu;
import com.oxchains.rmsuser.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2018-02-02 15:10
 * @name ResourceRepo
 * @desc:
 */
@Repository
public interface ResourceRepo extends CrudRepository<Resource,Long> {


    Page<Resource> findAll(Pageable pageable);
    List<Resource> findByPid(Long pid);

    Resource findByResourceSign(String resourceSign);

    Page<Resource> findByIdIn(List<Long> ids,Pageable pageable);
    List<Resource> findByIdIn(List<Long> ids);
}
