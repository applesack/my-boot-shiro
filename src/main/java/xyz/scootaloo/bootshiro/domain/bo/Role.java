package xyz.scootaloo.bootshiro.domain.bo;

/**
 * 与数据库中auth_role角色表对应
 * 以枚举的形式列举，较于用数字表示更方便管理。
 * @author : flutterdash@qq.com
 * @since : 2020年12月12日 22:37
 */
public enum Role {

    ADMIN(100, "系统管理员"),
    USER (102, "用户"     ),
    GUEST(103, "访客"     ),
    ANON (104, "无角色"   );

    private final int roleId;
    private final String info;

    Role(int roleId, String info) {
        this.info = info;
        this.roleId = roleId;
    }

    // getter
    public int getRoleId() {
        return this.roleId;
    }
    public String getInfo() {
        return this.info;
    }

}
