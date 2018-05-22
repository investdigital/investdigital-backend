package info.investdigital.dao.asset;

import info.investdigital.entity.asset.BinanceSymbol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-04 14:12
 **/
@Repository
public interface BinanceSymbolsDao extends CrudRepository<BinanceSymbol, Long> {
    @Override
    List<BinanceSymbol> findAll();

    BinanceSymbol findBySymbol(String symbol);

}
