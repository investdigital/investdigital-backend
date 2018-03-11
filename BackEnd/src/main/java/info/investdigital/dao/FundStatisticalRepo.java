package info.investdigital.dao;

import info.investdigital.entity.FundStatistical;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @Author: huohuo
 * Created in 21:27  2018/3/8.
 */
@Repository
public interface FundStatisticalRepo extends CrudRepository<FundStatistical,Long>{
    List<FundStatistical> findAllByFundCodeOrderByTimeAsc(BigInteger fundCode);
    Integer countByFundCode(BigInteger fundCode);
}
