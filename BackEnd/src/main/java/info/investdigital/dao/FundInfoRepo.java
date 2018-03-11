package info.investdigital.dao;

import info.investdigital.entity.FundInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oxchains
 * @time 2017-12-15 13:48
 * @name FundInfoRepo
 * @desc:
 */
@Repository
public interface FundInfoRepo extends CrudRepository<FundInfo,Long> {

    FundInfo findByFundId(Long fundId);
}
