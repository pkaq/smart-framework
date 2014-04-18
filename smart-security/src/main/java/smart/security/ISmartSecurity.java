package smart.security;

import java.util.Set;

/**
 * Smart Security 接口
 */
public interface ISmartSecurity {

    /**
     * 根据用户名获取密码
     *
     * @param username 用户名
     * @return 密码
     */
    String getPassword(String username);

    /**
     * 根据用户名获取角色名集合
     *
     * @param username 用户名
     * @return 角色名集合
     */
    Set<String> getRoleNameSet(String username);

    /**
     * 根据角色名获取权限名集合
     *
     * @param roleName 角色名
     * @return 权限名集合
     */
    Set<String> getPermNameSet(String roleName);
}
