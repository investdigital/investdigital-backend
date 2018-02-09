package com.oxchains.message.dao;

import com.oxchains.message.domain.TokenKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ccl
 * @time 2017-11-08 13:41
 * @name TokenKeyDao
 * @desc:
 */

@Repository
public interface TokenKeyDao extends CrudRepository<TokenKey,Long>,java.io.Serializable {
}
