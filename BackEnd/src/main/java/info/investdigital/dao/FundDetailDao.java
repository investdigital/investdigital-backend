package info.investdigital.dao;

import info.investdigital.entity.FundDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

/**
 * @Author: huohuo
 * Created in 18:00  2018/3/5.
 */
public interface FundDetailDao extends CrudRepository<FundDetail,Long>{
    FundDetail findByFundCode(BigInteger fundCode);
    Page<FundDetail> findAll(Pageable pageable);
}
