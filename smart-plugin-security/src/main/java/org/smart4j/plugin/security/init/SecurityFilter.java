package org.smart4j.plugin.security.init;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.smart4j.plugin.security.SecurityConfig;
import org.smart4j.plugin.security.SecurityConstant;
import org.smart4j.plugin.security.SmartSecurity;
import org.smart4j.plugin.security.realm.SmartCustomRealm;
import org.smart4j.plugin.security.realm.SmartJdbcRealm;

/**
 * 安全过滤器（扩展 ShiroFilter）
 *
 * @author huangyong
 * @since 2.3
 */
public class SecurityFilter extends ShiroFilter {

    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager webSecurityManager = super.getSecurityManager();
        setRealms(webSecurityManager);
        setCache(webSecurityManager);
    }

    private void setRealms(WebSecurityManager webSecurityManager) {
        String securityRealms = SecurityConfig.getRealms();
        if (securityRealms != null) {
            String[] securityRealmArray = securityRealms.split(",");
            if (securityRealmArray.length > 0) {
                Set<Realm> realms = new LinkedHashSet<Realm>();
                for (String securityRealm : securityRealmArray) {
                    if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_JDBC)) {
                        addJdbcRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_AD)) {
                        addAdRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_CUSTOM)) {
                        addCustomRealm(realms);
                    }
                }
                RealmSecurityManager realmSecurityManager = (RealmSecurityManager) webSecurityManager;
                realmSecurityManager.setRealms(realms);
            }
        }
    }

    private void addJdbcRealm(Set<Realm> realms) {
        SmartJdbcRealm smartJdbcRealm = new SmartJdbcRealm();
        realms.add(smartJdbcRealm);
    }

    private void addAdRealm(Set<Realm> realms) {
        ActiveDirectoryRealm realm = new ActiveDirectoryRealm();
        realm.setUrl(SecurityConfig.getAdUrl());
        realm.setSystemUsername(SecurityConfig.getAdSystemUsername());
        realm.setSystemPassword(SecurityConfig.getAdSystemPassword());
        realm.setSearchBase(SecurityConfig.getAdSearchBase());
        realms.add(realm);
    }

    private void addCustomRealm(Set<Realm> realms) {
        SmartSecurity smartSecurity = SecurityConfig.getSmartSecurity();
        SmartCustomRealm smartCustomRealm = new SmartCustomRealm(smartSecurity);
        realms.add(smartCustomRealm);
    }

    private void setCache(WebSecurityManager webSecurityManager) {
        if (SecurityConfig.isCacheable()) {
            CachingSecurityManager cachingSecurityManager = (CachingSecurityManager) webSecurityManager;
            CacheManager cacheManager = new MemoryConstrainedCacheManager();
            cachingSecurityManager.setCacheManager(cacheManager);
        }
    }
}
