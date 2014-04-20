package org.smart4j.security.tag;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

public class HasAllPermsTag extends PermissionTag {

    private static final String PERM_NAMES_DELIMETER = ",";

    @Override
    protected boolean showTagBody(String permNames) {
        boolean hasAllPerm = false;
        Subject subject = getSubject();
        if (subject != null) {
            if (subject.isPermittedAll(permNames.split(PERM_NAMES_DELIMETER))) {
                hasAllPerm = true;
            }
        }
        return hasAllPerm;
    }
}
