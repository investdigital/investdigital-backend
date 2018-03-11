package info.investdigital.dao;


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
public interface FundRepo extends CrudRepository<Fund,Long> {
    List<Fund> findByIssueUser(Long userId);
    Page<Fund> findByIssueUser(Long userId,Pageable pageable);
}
