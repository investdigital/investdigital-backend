package info.investdigital.dao;

import info.investdigital.entity.PrepareAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: huohuo
 * Created in 18:25  2018/3/7.
 */
@Repository
public interface PrepareAddressRepo extends CrudRepository<PrepareAddress,Long>{
    Page<PrepareAddress> findAll(Pageable pageable);
}
