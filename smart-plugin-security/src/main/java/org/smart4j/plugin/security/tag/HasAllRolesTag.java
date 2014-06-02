package org.smart4j.plugin.security.tag;

import java.util.Arrays;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.RoleTag;

/**
 * 判断当前用户是否拥有其中所有的角色（逗号分隔，表示“与”的关系）
 *
 * @author huangyong
 * @since 2.3
 */
public class HasAllRolesTag extends RoleTag {

    private static final String ROLE_NAMES_DELIMETER = ",";

    @Override
    protected boolean showTagBody(String roleNames) {
        boolean hasAllRole = false;
        Subject subject = getSubject();
        if (subject != null) {
            if (subject.hasAllRoles(Arrays.asList(roleNames.split(ROLE_NAMES_DELIMETER)))) {
                hasAllRole = true;
            }
        }
        return hasAllRole;
    }
}
