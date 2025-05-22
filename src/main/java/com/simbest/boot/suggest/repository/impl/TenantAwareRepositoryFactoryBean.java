package com.simbest.boot.suggest.repository.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.simbest.boot.suggest.model.TenantAwareEntity;
import com.simbest.boot.suggest.repository.TenantAwareRepository;

/**
 * 租户感知仓库工厂Bean
 * 用于创建TenantAwareRepository实例
 * @param <R> 仓库类型
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public class TenantAwareRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    /**
     * 构造函数
     * @param repositoryInterface 仓库接口
     */
    public TenantAwareRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * 创建仓库工厂
     * @param entityManager 实体管理器
     * @return 仓库工厂
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new TenantAwareRepositoryFactory<>(entityManager);
    }

    /**
     * 租户感知仓库工厂
     * @param <T> 实体类型
     * @param <ID> 主键类型
     */
    private static class TenantAwareRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

        private final EntityManager entityManager;

        /**
         * 构造函数
         * @param entityManager 实体管理器
         */
        public TenantAwareRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        /**
         * 获取仓库实现
         * @param information 仓库信息
         * @param metadata 仓库元数据
         * @param entityManager 实体管理器
         * @return 仓库实现
         */
        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
            
            // 如果实体实现了TenantAwareEntity接口，并且仓库接口继承了TenantAwareRepository接口，则使用TenantAwareRepositoryImpl
            if (TenantAwareEntity.class.isAssignableFrom(information.getDomainType()) &&
                    TenantAwareRepository.class.isAssignableFrom(information.getRepositoryInterface())) {
                return new TenantAwareRepositoryImpl<>((JpaEntityInformation<? extends TenantAwareEntity, ?>) entityInformation, entityManager);
            }
            
            // 否则使用默认的仓库实现
            return super.getTargetRepository(information, entityManager);
        }

        /**
         * 获取仓库基类
         * @param metadata 仓库元数据
         * @return 仓库基类
         */
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            // 如果实体实现了TenantAwareEntity接口，并且仓库接口继承了TenantAwareRepository接口，则使用TenantAwareRepositoryImpl
            if (TenantAwareEntity.class.isAssignableFrom(metadata.getDomainType()) &&
                    TenantAwareRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                return TenantAwareRepositoryImpl.class;
            }
            
            // 否则使用默认的仓库基类
            return super.getRepositoryBaseClass(metadata);
        }
    }
}
