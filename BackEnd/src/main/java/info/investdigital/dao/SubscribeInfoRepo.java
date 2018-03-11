package info.investdigital.dao;

import info.investdigital.entity.SubscribeInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: huohuo
 * Created in 11:13  2018/3/8.
 */
@Repository
public interface SubscribeInfoRepo extends CrudRepository<SubscribeInfo,Long>{
}
