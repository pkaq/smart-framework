package com.smart.security.tag;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.PermissionTag;

public class HasAnyPermsTag extends PermissionTag {

    private static final String PERM_NAMES_DELIMETER = ",";

    @Override
    protected boolean showTagBody(String permNames) {
        boolean hasAnyPerm = false;
        Subject subject = getSubject();
        if (subject != null) {
            for (String perm : permNames.split(PERM_NAMES_DELIMETER)) {
                if (subject.isPermitted(perm.trim())) {
                    hasAnyPerm = true;
                    break;
                }
            }
        }
        return hasAnyPerm;
    }
}
