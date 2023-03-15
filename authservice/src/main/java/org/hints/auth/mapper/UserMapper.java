package org.hints.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hints.auth.model.Client;
import org.hints.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2023/3/14 17:54
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    User finduser(@Param("username") String username);

    List<HashMap> getAuth(@Param("username") String username);
}
