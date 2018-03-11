package info.investdigital.dao.DigitalCurrency;

import info.investdigital.entity.DigitalCurrency.EthUsdMin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: huohuo
 * Created in 23:22  2018/3/9.
 */
@Repository
public interface EthUsdMinRepo extends CrudRepository<EthUsdMin,Long>{
    List<EthUsdMin> findAllByIdGreaterThanEqualAndIdLessThanEqual(Long startTime,Long endTime);
}
