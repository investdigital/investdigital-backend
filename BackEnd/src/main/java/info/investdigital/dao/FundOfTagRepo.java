package info.investdigital.dao;

import info.investdigital.entity.FundOfTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2017-12-12 17:10
 * @name UserRepo
 * @desc:
 */
@Repository
public interface FundOfTagRepo extends CrudRepository<FundOfTag,Long> {
    List<FundOfTag> findByFundId(Long fundId);
    List<FundOfTag> findByTagId(Long tagId);
}
