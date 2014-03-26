package com.smart.security.tag;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

public class HasAllPermissionsTag extends PermissionTag {

    private static final String PERM_NAMES_DELIMETER = ",";

    public HasAllPermissionsTag() {
    }

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
