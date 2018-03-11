package info.investdigital.dao;

import info.investdigital.entity.FundReturn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name FundReturnRepo
 * @desc:
 */
@Repository
public interface FundReturnRepo extends CrudRepository<FundReturn,Long> {
    @Query(value = "select s from FundReturn as s order by s.netAssetValue desc")
    Page<FundReturn> findFundReturns(Pageable pageable);

    @Query(value = "select s from FundReturn s where s.fundId in (:fundIds) order by s.netAssetValue desc")
    List<FundReturn> findFundReturnsByFundId(@Param("fundIds") List<Long> fundIds);

    FundReturn findByFundId(Long fundId);
}
