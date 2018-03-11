package info.investdigital.dao;

import info.investdigital.entity.FundReturnDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author oxchains
 * @time 2017-12-14 11:37
 * @name FundReturnDetailRepo
 * @desc:
 */
@Repository
public interface FundReturnDetailRepo extends CrudRepository<FundReturnDetail,Long> {
    List<FundReturnDetail> findByFundIdAndDateIsGreaterThanEqualOrderByDateAsc(Long fundId,Long date);
    List<FundReturnDetail> findFundReturnDetailByFundIdOrderByDateAsc(Long fundId);
}
