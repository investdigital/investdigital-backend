package com.oxchains.message.dao;

import com.oxchains.message.domain.PushData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author luoxuri
 * @create 2018-02-06 11:09
 **/
@Repository
public interface PushDataDao extends CrudRepository<PushData, Long> {
}
