package sg.lwx.work.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sg.lwx.work.domain.Transaction;

@Repository
@Mapper
public interface TransactionMapper {
    Transaction selectByHash(String transactionHash);

    int insert(Transaction transaction);
}
