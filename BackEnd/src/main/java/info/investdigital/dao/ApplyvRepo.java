package info.investdigital.dao;

import info.investdigital.entity.ApplyV;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ccl
 * @time 2018-03-07 13:10
 * @name ApplyvRepo
 * @desc:
 */
@Repository
public interface ApplyvRepo extends CrudRepository<ApplyV,Long> {
    ApplyV findByUserId(Long userId);
    List<ApplyV> findByStatus(Integer status);
}
