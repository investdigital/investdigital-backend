package info.investdigital.dao;

import info.investdigital.entity.TransferInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: huohuo
 * Created in 12:31  2018/3/8.
 */
@Repository
public interface TransferInfoRepo extends CrudRepository<TransferInfo,Long>{
}
