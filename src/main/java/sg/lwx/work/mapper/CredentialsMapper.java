package sg.lwx.work.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.Credentials;

/**
 * @author lianwenxiu
 */
@Repository
@Mapper
public interface CredentialsMapper {
    Credentials selectPrivateKeyByAddress(String address);
}
