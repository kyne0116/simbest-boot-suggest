package com.simbest.boot.suggest.repository.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.simbest.boot.suggest.config.TenantContext;
import com.simbest.boot.suggest.model.TenantAwareEntity;
import com.simbest.boot.suggest.repository.TenantAwareRepository;

/**
 * 租户感知仓库实现类
 * 用于实现租户隔离的数据访问
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public class TenantAwareRepositoryImpl<T extends TenantAwareEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements TenantAwareRepository<T, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ?> entityInformation;

    /**
     * 构造函数
     * @param entityInformation 实体信息
     * @param entityManager 实体管理器
     */
    public TenantAwareRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    /**
     * 重写查询所有实体的方法，添加租户过滤
     * @return 实体列表
     */
    @Override
    public List<T> findAll() {
        return findAllByTenantCode(TenantContext.getCurrentTenant());
    }

    /**
     * 根据租户代码查询所有实体
     * @param tenantCode 租户代码
     * @return 实体列表
     */
    @Override
    public List<T> findAllByTenantCode(String tenantCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getDomainClass());
        Root<T> root = query.from(getDomainClass());
        query.where(cb.equal(root.get("tenantCode"), tenantCode));
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * 重写根据ID查询实体的方法，添加租户过滤
     * @param id 实体ID
     * @return 实体
     */
    @Override
    public Optional<T> findById(ID id) {
        return findByIdAndTenantCode(id, TenantContext.getCurrentTenant());
    }

    /**
     * 根据ID和租户代码查询实体
     * @param id 实体ID
     * @param tenantCode 租户代码
     * @return 实体
     */
    @Override
    public Optional<T> findByIdAndTenantCode(ID id, String tenantCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getDomainClass());
        Root<T> root = query.from(getDomainClass());
        query.where(
                cb.equal(root.get(entityInformation.getIdAttribute().getName()), id),
                cb.equal(root.get("tenantCode"), tenantCode)
        );
        List<T> result = entityManager.createQuery(query).getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    /**
     * 重写保存实体的方法，设置租户代码
     * @param entity 实体
     * @return 保存后的实体
     */
    @Override
    public <S extends T> S save(S entity) {
        if (entity.getTenantCode() == null) {
            entity.setTenantCode(TenantContext.getCurrentTenant());
        }
        return super.save(entity);
    }

    /**
     * 重写保存所有实体的方法，设置租户代码
     * @param entities 实体列表
     * @return 保存后的实体列表
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(entity -> {
            if (entity.getTenantCode() == null) {
                entity.setTenantCode(TenantContext.getCurrentTenant());
            }
        });
        return super.saveAll(entities);
    }

    /**
     * 根据租户代码删除所有实体
     * @param tenantCode 租户代码
     */
    @Override
    public void deleteAllByTenantCode(String tenantCode) {
        List<T> entities = findAllByTenantCode(tenantCode);
        deleteAll(entities);
    }

    /**
     * 根据ID和租户代码删除实体
     * @param id 实体ID
     * @param tenantCode 租户代码
     */
    @Override
    public void deleteByIdAndTenantCode(ID id, String tenantCode) {
        Optional<T> entity = findByIdAndTenantCode(id, tenantCode);
        entity.ifPresent(this::delete);
    }

    /**
     * 根据租户代码统计实体数量
     * @param tenantCode 租户代码
     * @return 实体数量
     */
    @Override
    public long countByTenantCode(String tenantCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(getDomainClass());
        query.select(cb.count(root));
        query.where(cb.equal(root.get("tenantCode"), tenantCode));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 重写统计实体数量的方法，添加租户过滤
     * @return 实体数量
     */
    @Override
    public long count() {
        return countByTenantCode(TenantContext.getCurrentTenant());
    }
}
