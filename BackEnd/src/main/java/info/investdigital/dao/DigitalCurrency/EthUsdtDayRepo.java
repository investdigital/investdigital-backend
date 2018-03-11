package info.investdigital.dao.DigitalCurrency;

import info.investdigital.entity.DigitalCurrency.EthUsdtDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: huohuo
 * Created in 20:55  2018/3/8.
 */
@Repository
public interface EthUsdtDayRepo extends CrudRepository<EthUsdtDay,Long>{
    List<EthUsdtDay> findByIdGreaterThanEqualAndIdLessThanEqualOrderByIdAsc(Long startTime,Long endTime);
}
