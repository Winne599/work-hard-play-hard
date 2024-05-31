package sg.lwx.work.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.ContractInfo;

@Mapper
@Repository
public interface ContractInfoMapper {

    ContractInfo selectByContractAddress(String contractAddress);
}
