package com.agamy.inventory.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.agamy.inventory.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.agamy.inventory.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.agamy.inventory.domain.User.class.getName());
            createCache(cm, com.agamy.inventory.domain.Authority.class.getName());
            createCache(cm, com.agamy.inventory.domain.User.class.getName() + ".authorities");
            createCache(cm, com.agamy.inventory.domain.EntityAuditEvent.class.getName());
            createCache(cm, com.agamy.inventory.domain.Brand.class.getName());
            createCache(cm, com.agamy.inventory.domain.Brand.class.getName() + ".products");
            createCache(cm, com.agamy.inventory.domain.ProductCategory.class.getName());
            createCache(cm, com.agamy.inventory.domain.ProductCategory.class.getName() + ".products");
            createCache(cm, com.agamy.inventory.domain.Product.class.getName());
            createCache(cm, com.agamy.inventory.domain.Product.class.getName() + ".inventoryTransactions");
            createCache(cm, com.agamy.inventory.domain.Product.class.getName() + ".sales");
            createCache(cm, com.agamy.inventory.domain.Product.class.getName() + ".purchases");
            createCache(cm, com.agamy.inventory.domain.InventoryTransaction.class.getName());
            createCache(cm, com.agamy.inventory.domain.Customer.class.getName());
            createCache(cm, com.agamy.inventory.domain.Customer.class.getName() + ".saleOperations");
            createCache(cm, com.agamy.inventory.domain.SaleOperation.class.getName());
            createCache(cm, com.agamy.inventory.domain.SaleOperation.class.getName() + ".sales");
            createCache(cm, com.agamy.inventory.domain.Sale.class.getName());
            createCache(cm, com.agamy.inventory.domain.Sale.class.getName() + ".salePayments");
            createCache(cm, com.agamy.inventory.domain.SalePayment.class.getName());
            createCache(cm, com.agamy.inventory.domain.PurchaseOperation.class.getName());
            createCache(cm, com.agamy.inventory.domain.PurchaseOperation.class.getName() + ".purchases");
            createCache(cm, com.agamy.inventory.domain.Purchase.class.getName());
            createCache(cm, com.agamy.inventory.domain.Purchase.class.getName() + ".purchasePayments");
            createCache(cm, com.agamy.inventory.domain.PurchasePayment.class.getName());
            createCache(cm, com.agamy.inventory.domain.Bill.class.getName());
            createCache(cm, com.agamy.inventory.domain.Bill.class.getName() + ".saleOperations");
            createCache(cm, com.agamy.inventory.domain.Bill.class.getName() + ".purchaseOperations");
            createCache(cm, com.agamy.inventory.domain.Supplier.class.getName());
            createCache(cm, com.agamy.inventory.domain.Supplier.class.getName() + ".purchases");
            createCache(cm, com.agamy.inventory.domain.Employee.class.getName());
            createCache(cm, com.agamy.inventory.domain.Employee.class.getName() + ".salaryPayments");
            createCache(cm, com.agamy.inventory.domain.SalaryPayment.class.getName());
            createCache(cm, com.agamy.inventory.domain.CashTransaction.class.getName());
            createCache(cm, com.agamy.inventory.domain.OutgoingPayment.class.getName());
            createCache(cm, com.agamy.inventory.domain.CashBalance.class.getName());
            createCache(cm, com.agamy.inventory.domain.DailyCashReconciliation.class.getName());
            createCache(cm, com.agamy.inventory.domain.DailyCashReconciliation.class.getName() + ".dailyCashDetails");
            createCache(cm, com.agamy.inventory.domain.DailyCashDetail.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
