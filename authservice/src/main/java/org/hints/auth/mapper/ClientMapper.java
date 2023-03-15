package org.hints.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hints.auth.model.Client;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/3/14 17:54
 */
@Repository
public interface ClientMapper extends BaseMapper<Client> {

    Client findByClientId(@Param("clientId") String clientId);

    List<Client> queryClient(Client client);

    int insertClient(Client client);

    int deleteClient(String clientId);

    int updateClient(Client client);
    
}
