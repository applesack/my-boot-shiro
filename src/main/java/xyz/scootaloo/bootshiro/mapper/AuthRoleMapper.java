package xyz.scootaloo.bootshiro.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import xyz.scootaloo.bootshiro.domain.po.AuthRole;

import java.util.List;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 9:45
 */
@Mapper
public interface AuthRoleMapper {

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
    int insert(AuthRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insertSelective(AuthRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param id 1
     * @return com.usthe.bootshiro.domain.bo.AuthRole
     * @throws DataAccessException when
     */
    AuthRole selectByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKeySelective(AuthRole record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKey(AuthRole record) throws DataAccessException;

    /**
     * description TODO
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthRole>
     * @throws DataAccessException when
     */
    List<AuthRole> selectRoles() throws DataAccessException;

}
