package info.investdigital.dao.asset;

import info.investdigital.entity.asset.HuobiCurrency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-07 15:24
 **/
@Repository
public interface HuobiCurrencysDao extends CrudRepository<HuobiCurrency, Integer> {

    @Override
    List<HuobiCurrency> findAll();

    HuobiCurrency findByCurrency(String currency);
}
