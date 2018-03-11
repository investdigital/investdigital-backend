package info.investdigital.dao;

import info.investdigital.entity.GoogleSecret;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 上午11:34 2018/3/5
 * @Modified By:
 */
@Repository
public interface GoogleSecretRepo extends CrudRepository<GoogleSecret,Long> {


}
