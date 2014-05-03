package org.smart4j.security.init;

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
import org.smart4j.security.SmartSecurity;
import org.smart4j.security.realm.SmartCustomRealm;
import org.smart4j.security.realm.SmartJdbcRealm;
import org.smart4j.security.tool.SmartProps;

public class SmartShiroFilter extends ShiroFilter {

    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager webSecurityManager = super.getSecurityManager();
        initRealms(webSecurityManager);
        initCache(webSecurityManager);
    }

    private void initRealms(WebSecurityManager webSecurityManager) {
        String securityRealms = SmartProps.getRealms();
        if (securityRealms != null) {
            String[] securityRealmArray = securityRealms.split(",");
            if (securityRealmArray.length > 0) {
                Set<Realm> realms = new LinkedHashSet<Realm>();
                for (String securityRealm : securityRealmArray) {
                    if (securityRealm.equalsIgnoreCase("jdbc")) {
                        addJdbcRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase("ad")) {
                        addAdRealm(realms);
                    } else if (securityRealm.equalsIgnoreCase("custom")) {
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
        realm.setUrl(SmartProps.getAdUrl());
        realm.setSystemUsername(SmartProps.getAdSystemUsername());
        realm.setSystemPassword(SmartProps.getAdSystemPassword());
        realm.setSearchBase(SmartProps.getAdSearchBase());
        realms.add(realm);
    }

    private void addCustomRealm(Set<Realm> realms) {
        SmartSecurity smartSecurity = SmartProps.getSmartSecurity();
        SmartCustomRealm smartCustomRealm = new SmartCustomRealm(smartSecurity);
        realms.add(smartCustomRealm);
    }

    private void initCache(WebSecurityManager webSecurityManager) {
        if (SmartProps.isCache()) {
            CachingSecurityManager cachingSecurityManager = (CachingSecurityManager) webSecurityManager;
            CacheManager cacheManager = new MemoryConstrainedCacheManager();
            cachingSecurityManager.setCacheManager(cacheManager);
        }
    }
}
