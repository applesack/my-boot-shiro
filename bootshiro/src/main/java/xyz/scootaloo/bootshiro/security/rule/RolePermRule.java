package xyz.scootaloo.bootshiro.security.rule;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.scootaloo.bootshiro.utils.StringUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * 与数据表auth_resource对应的实体类。
 * 将数据库中的字段处理成shiro能够识别的格式。
 * 由于从数据库中取出的数据需要进行进一步的处理，所以单独设计这个类来做这个功能
 * @see xyz.scootaloo.bootshiro.mapper.AuthResourceMapper#selectRoleRules
 * ---------------------------------------------------------------------
 * @author : flutterdash@qq.com
 * @since : 2020年12月04日 15:43
 */
@Data
@NoArgsConstructor
public class RolePermRule implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ANON_ROLE = "role_anon";

    // 资源的url
    private String url;       // 格式如: "/account/login==POST"
    // 访问资源所需要的角色列表，多个角色用逗号间隔
    private String needRoles; // 格式如: "role_admin,role_user"

    /**
     * 将url 和 needRoles 转化成shiro可识别的过滤器链：url=jwt[角色1、角色2、角色n]
     * anon 或者
     * jwt[role1,role2,...]
     * @return java.lang.StringBuilder
     */
    public String toFilterChain() {
        if (null == this.url || this.url.isEmpty()) {
            return null;
        }
        Set<String> setRole = StringUtils.splitAndToSet(this.getNeedRoles(), ',');

        // 约定若role_anon角色拥有此uri资源的权限,则此uri资源直接访问不需要认证和权限
        if (!StringUtils.isEmpty(getNeedRoles())) {
            if (setRole.contains(ANON_ROLE)) {
                return "anon";
            } else {
                return "jwt[" + getNeedRoles() + "]";
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "RolePermRule [url=" + url + ", needRoles=" + needRoles + "]";
    }

}
