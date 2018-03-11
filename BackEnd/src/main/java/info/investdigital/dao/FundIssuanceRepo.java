package info.investdigital.dao;

import info.investdigital.entity.FundIssuance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface FundIssuanceRepo extends CrudRepository<FundIssuance,Long> {

    FundIssuance findByInvestDigitalNo(String investNo);

}
