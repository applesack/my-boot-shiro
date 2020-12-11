package xyz.scootaloo.bootshiro.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import xyz.scootaloo.bootshiro.domain.po.AuthUserRole;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 10:02
 */
@Mapper
public interface AuthUserRoleMapper {

    /**
     * description TODO
     *
     * @param id 1
     * @return int
     * @throws DataAccessException when
     */
    int deleteByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insert(AuthUserRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insertSelective(AuthUserRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param id 1
     * @return com.usthe.bootshiro.domain.bo.AuthUserRole
     * @throws DataAccessException when
     */
    AuthUserRole selectByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKeySelective(AuthUserRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKey(AuthUserRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int deleteByUniqueKey(AuthUserRole record) throws DataAccessException;

}
