package sg.lwx.work.mapper;

import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.CurrentBlockNo;

@Repository
public interface CurrentBlockNoMapper {

    CurrentBlockNo selectByType(String erc20TokenType);
}
