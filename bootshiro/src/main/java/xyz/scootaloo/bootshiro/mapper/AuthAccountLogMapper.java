package xyz.scootaloo.bootshiro.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.shiro.dao.DataAccessException;
import xyz.scootaloo.bootshiro.domain.po.AuthAccountLog;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:42
 */
@Mapper
public interface AuthAccountLogMapper {

    List<AuthAccountLog> selectAccountLogList();

    int insert(AuthAccountLog authAccountLog) throws DataAccessException;

}
