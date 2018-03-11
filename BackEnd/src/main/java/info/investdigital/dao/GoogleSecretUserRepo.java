package info.investdigital.dao;
import info.investdigital.entity.GoogleSecretUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: Gaoyp
 * @Description:
 * @Date: Create in 下午1:06 2018/3/5
 * @Modified By:
 */
@Repository
public interface GoogleSecretUserRepo extends CrudRepository<GoogleSecretUser,Long> {

    GoogleSecretUser findByUid(Long uid);

}
