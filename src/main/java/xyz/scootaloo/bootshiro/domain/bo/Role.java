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

    /**
     * 工厂方法，将数字roleId转换成枚举对象
     * @param roleId 代表角色的数字，参数数据库
     * @return Role枚举对象
     */
    public static Role of(int roleId) {
        switch (roleId) {
            case 104: return Role.ANON;
            case 103: return Role.GUEST;
            case 102: return Role.USER;
            case 100: return Role.ADMIN;
            default:
                String msg = "输入错误,系统中不存在此roleId代表的角色,{roleId:" + roleId +"}";
                throw new IllegalArgumentException(msg);
        }
    }

    // getter
    public int getRoleId() {
        return this.roleId;
    }
    public String getInfo() {
        return this.info;
    }

}
