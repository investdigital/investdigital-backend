package info.investdigital.dao.asset;

import info.investdigital.entity.asset.HuobiSymbol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-04 14:12
 **/
@Repository
public interface HuobiSymbolsDao extends CrudRepository<HuobiSymbol, Long> {
    @Override
    List<HuobiSymbol> findAll();

    HuobiSymbol findByBasecurrencyAndQuotecurrency(String baseCoin, String quoteCoin);

}
