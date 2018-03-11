package info.investdigital.dao;


import info.investdigital.entity.FundComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface FundCommentRepo extends CrudRepository<FundComment,Long> {
    List<FundComment> findByFundIdAndUserId(Long fundId,Long userId);
    List<FundComment> findByFundId(Long fundId);
    Page<FundComment> findByFundId(Long fundId,Pageable pageable);
}
