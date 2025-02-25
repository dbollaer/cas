package org.apereo.cas.trusted.authentication.storage;

import org.apereo.cas.configuration.model.support.mfa.trusteddevice.TrustedDevicesMultifactorProperties;
import org.apereo.cas.mongo.MongoDbConnectionFactory;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecord;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustRecordKeyGenerator;
import org.apereo.cas.util.LoggingUtils;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.util.function.FunctionUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is {@link MongoDbMultifactorAuthenticationTrustStorage}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class MongoDbMultifactorAuthenticationTrustStorage extends BaseMultifactorAuthenticationTrustStorage {
    private final MongoOperations mongoTemplate;

    public MongoDbMultifactorAuthenticationTrustStorage(final TrustedDevicesMultifactorProperties properties,
                                                        final CipherExecutor<Serializable, String> cipherExecutor,
                                                        final MongoOperations mongoTemplate,
                                                        final MultifactorAuthenticationTrustRecordKeyGenerator keyGenerationStrategy) {
        super(properties, cipherExecutor, keyGenerationStrategy);
        this.mongoTemplate = mongoTemplate;
        configureIndexes(mongoTemplate);
    }

    private void configureIndexes(final MongoOperations mongoTemplate) {
        /*
            Expire at zero indicates the entity will expire at the exact date provided in the field
         */
        val expirationDateIndex = new Index().on("expirationDate", Sort.Direction.ASC).expire(0);
        val collection = mongoTemplate.getCollection(getTrustedDevicesMultifactorProperties().getMongo().getCollection());
        MongoDbConnectionFactory.createOrUpdateIndexes(mongoTemplate, collection, List.of(expirationDateIndex));
    }

    @Override
    public void remove(final String key) {
        try {
            val query = new Query();
            query.addCriteria(Criteria.where("recordKey").is(key));
            val res = mongoTemplate.remove(query,
                MultifactorAuthenticationTrustRecord.class,
                getTrustedDevicesMultifactorProperties().getMongo().getCollection());
            LOGGER.info("Found and removed [{}] for query [{}]", res.getDeletedCount(), query);
        } catch (final Exception e) {
            LoggingUtils.error(LOGGER, e);
        }
    }

    @Override
    public void remove(final ZonedDateTime expirationDate) {
        try {
            val query = new Query();
            query.addCriteria(Criteria.where("expirationDate").lte(expirationDate));
            val res = mongoTemplate.remove(query, MultifactorAuthenticationTrustRecord.class,
                getTrustedDevicesMultifactorProperties().getMongo().getCollection());
            LOGGER.info("Found and removed [{}] for query [{}]", res.getDeletedCount(), query);
        } catch (final Exception e) {
            LoggingUtils.error(LOGGER, e);
        }
    }

    @Override
    public Set<? extends MultifactorAuthenticationTrustRecord> getAll() {
        val results = mongoTemplate.findAll(MultifactorAuthenticationTrustRecord.class,
            getTrustedDevicesMultifactorProperties().getMongo().getCollection());
        return new HashSet<>(results);
    }

    @Override
    public Set<? extends MultifactorAuthenticationTrustRecord> get(final ZonedDateTime onOrAfterDate) {
        val query = new Query();
        query.addCriteria(Criteria.where("recordDate").gte(onOrAfterDate));
        val results = mongoTemplate.find(query, MultifactorAuthenticationTrustRecord.class,
            getTrustedDevicesMultifactorProperties().getMongo().getCollection());
        return new HashSet<>(results);
    }

    @Override
    public Set<? extends MultifactorAuthenticationTrustRecord> get(final String principal) {
        val query = new Query();
        query.addCriteria(Criteria.where("principal").is(principal));
        val results = mongoTemplate.find(query, MultifactorAuthenticationTrustRecord.class,
            getTrustedDevicesMultifactorProperties().getMongo().getCollection());
        return new HashSet<>(results);
    }

    @Override
    public MultifactorAuthenticationTrustRecord get(final long id) {
        val query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, MultifactorAuthenticationTrustRecord.class,
            getTrustedDevicesMultifactorProperties().getMongo().getCollection());
    }

    @Override
    protected MultifactorAuthenticationTrustRecord saveInternal(final MultifactorAuthenticationTrustRecord record) {
        FunctionUtils.doIf(record.getId() < 0, __ -> record.setId(System.nanoTime())).accept(record);
        return mongoTemplate.save(record, getTrustedDevicesMultifactorProperties().getMongo().getCollection());
    }
}

