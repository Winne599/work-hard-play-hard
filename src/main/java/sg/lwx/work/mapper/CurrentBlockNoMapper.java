package sg.lwx.work.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.CurrentBlockNo;

@Repository
@Mapper
public interface CurrentBlockNoMapper {

    CurrentBlockNo selectByType(String erc20TokenType);
}
