package xyz.scootaloo.bootshiro.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import xyz.scootaloo.bootshiro.domain.po.AuthOperationLog;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:43
 */
@Mapper
public interface AuthOperationLogMapper {

    /**
     * description 获取资源相关操作日志
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthOperationLog>
     */
    List<AuthOperationLog> select();

    /**
     * description 插入资源相关操作日志
     *
     * @param operationLog 1
     * @return int
     * @throws DataAccessException when
     */
    int insert(AuthOperationLog operationLog) throws DataAccessException;

}
