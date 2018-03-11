package info.investdigital.dao;

import info.investdigital.entity.FundTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface FundTagRepo extends CrudRepository<FundTag,Long> {

}
