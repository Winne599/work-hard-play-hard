package sg.lwx.work.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.TransferRecord;

@Repository
@Mapper
public interface TransferRecordMapper {
    void insert(TransferRecord record);

    TransferRecord selectByPrimaryKey(Integer id);

    void updateHashByPrimaryKey(TransferRecord record);

    TransferRecord selectByTransactionHash(String transactionHash);
}
