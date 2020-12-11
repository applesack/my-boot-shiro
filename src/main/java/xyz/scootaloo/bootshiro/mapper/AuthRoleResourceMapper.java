package xyz.scootaloo.bootshiro.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import xyz.scootaloo.bootshiro.domain.po.AuthRoleResource;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:46
 */
@Mapper
public interface AuthRoleResourceMapper {

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
    int insert(AuthRoleResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insertSelective(AuthRoleResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param id 1
     * @return com.usthe.bootshiro.domain.bo.AuthRoleResource
     * @throws DataAccessException when
     */
    AuthRoleResource selectByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKeySelective(AuthRoleResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKey(AuthRoleResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param roleId 1
     * @param resourceId 2
     * @return int
     * @throws DataAccessException when
     */
    int deleteByUniqueKey(@Param("roleId") Integer roleId, @Param("resourceId") Integer resourceId) throws DataAccessException;

}
