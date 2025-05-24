package vallterra.bookkeeper.test_record.record;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import vallterra.bookkeeper.test_record.TestRecordInitializedException;
import vallterra.bookkeeper.test_record.TestRecordNotInitializedException;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Abstract class for testing CRUD operations on JOOQ records.
 *
 * @param <R> the JOOQ Record type
 * @param <P> the corresponding POJO type
 * @param <PK> the type of the primary key (ID) for the record
 * @param <S> the type of the implementing class, allowing for fluent method chaining
 */
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class JooqTestRecord<R extends Record & UpdatableRecord<R>, P extends Serializable, PK extends Serializable, S extends JooqTestRecord<R, P, PK, S>> {

    protected final DSLContext db;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    protected PK internalId;

    protected abstract Table<R> getTable();

    protected abstract TableField<R, PK> getIdField();

    protected abstract Class<P> getPojoClass();

    protected R getRecord() {
        return db.fetchSingle(getTable(), getIdField().eq(getInternalId()));
    }

    /**
     * @return the database key (e.g. generated ID) for this record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    public PK getId() throws TestRecordNotInitializedException {
        validateIsInitialized();

        return this.internalId;
    }

    /**
     * Create a new entry with all required (non-null) fields set to valid defaults.
     *
     * @return instance representing the created record
     * @throws IllegalAccessError if the record is already initialized
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public S createMinimal() throws TestRecordInitializedException {
        validateIsNotInitialized();

        var record = db.newRecord(getTable());
        eraseNonIdFields(record);
        setMinimal(record).store();

        setInternalId(record.getValue(getIdField()));

        return (S) this;
    }

    /**
     * Reset the record to a minimal state, clearing all fields except those required by the database.
     *
     * @return instance representing the reset record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public S resetToMinimal() throws TestRecordNotInitializedException {
        validateIsInitialized();

        var record = eraseNonIdFields(getRecord());
        setMinimal(record).store();

        return (S) this;
    }

    /**
     * Create a new entry with all fields set to valid defaults.
     *
     * @return instance representing the created record
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public S createFull() throws TestRecordInitializedException {
        validateIsNotInitialized();

        var record = db.newRecord(getTable());
        setFull(record).store();

        setInternalId(record.getValue(getIdField()));

        return (S) this;
    }

    /**
     * Reset the record to a full state, populating all fields with valid defaults.
     *
     * @return instance representing the reset record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public S resetToFull() throws TestRecordNotInitializedException {
        validateIsInitialized();

        var record = eraseNonIdFields(getRecord());
        setFull(record).store();

        return (S) this;
    }

    /**
     * Fetch the current state of the record from the database.
     *
     * @return POJO representing the fetched record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    public P fetch() throws TestRecordNotInitializedException {
        validateIsInitialized();

        return db.selectFrom(getTable())
                .where(getIdField().eq(getInternalId()))
                .fetchSingleInto(getPojoClass());
    }

    /**
     * Apply updates to the record and persist changes.
     *
     * @param update function that modifies the POJO
     * @return instance representing the updated record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public S update(@NotNull UnaryOperator<P> update) throws TestRecordNotInitializedException {
        validateIsInitialized();

        var record = getRecord();

        record.from(update.apply(record.into(getPojoClass())));
        record.store();

        return (S) this;
    }

    /**
     * Delete this record from the database.
     *
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    @Transactional
    public void delete() throws TestRecordNotInitializedException {
        validateIsInitialized();

        db.delete(getTable())
                .where(getIdField().eq(getInternalId()))
                .execute();
    }

    /**
     * Create a new entry with all required (non-null) fields set to valid defaults and fetch it.
     * Used when further operations on the entry are not needed.
     *
     * @return POJO representing the created and fetched record
     * @throws TestRecordInitializedException if the record is already initialized
     */
    @Transactional
    public P createMinimalAndFetch() throws TestRecordInitializedException {
        return createMinimal().fetch();
    }

    /**
     * Create a new entry with all fields set to valid defaults and fetch it.
     * Used when further operations on the entry are not needed.
     *
     * @return POJO representing the created and fetched record
     * @throws TestRecordInitializedException if the record is already initialized
     */
    @Transactional
    public P createFullAndFetch() throws TestRecordInitializedException {
        return createFull().fetch();
    }

    /**
     * Create a new entry with all required (non-null) fields set to valid defaults and apply overrides.
     *
     * @param override function that modifies the POJO
     * @return instance representing the created and updated record
     * @throws TestRecordInitializedException if the record is already initialized
     */
    @Transactional
    public S createMinimal(@NotNull UnaryOperator<P> override) {
        return createMinimal().update(override);
    }

    /**
     * Create a new entry with all fields set to valid defaults and apply overrides.
     *
     * @param override function that modifies the POJO
     * @return instance representing the created and updated record
     * @throws TestRecordInitializedException if the record is already initialized
     */
    @Transactional
    public S createFull(@NotNull UnaryOperator<P> override) {
        return createFull().update(override);
    }

    /**
     * Update the record with the given function and fetch the updated state.
     * This is a convenience method that is equivalent to calling {@link #update(UnaryOperator)} followed by {@link #fetch()}.
     *
     * @param update function that modifies the POJO
     * @return POJO representing the updated and fetched record
     * @throws TestRecordNotInitializedException if the record has not been initialized
     */
    @Transactional
    public P updateAndFetch(@NotNull UnaryOperator<P> update) throws TestRecordNotInitializedException {
        return update(update).fetch();
    }

    /**
     * @return true if the record is initialized and can be used, false otherwise
     */
    public boolean isActive() {
        return db.fetchExists(getTable(), getIdField().eq(getInternalId()));
    }

    protected void validateIsNotInitialized() throws TestRecordInitializedException {
        if (isActive()) {
            throw new TestRecordInitializedException("The record is already initialized and cannot be created again.");
        }
    }

    protected void validateIsInitialized() throws TestRecordNotInitializedException {
        if (!isActive()) {
            throw new TestRecordNotInitializedException();
        }
    }

    /**
     * Sets all required (non-null) fields to valid defaults.
     */
    protected abstract R setMinimal(R record);

    /**
     * Sets all fields to valid defaults.
     */
    protected abstract R setFull(R record);

    /**
     * Resets all fields to their default values, except for the ID field.
     * This is useful for resetting the record to a clean state without deleting it.
     */
    protected R eraseNonIdFields(R record) {
        for (var f : getTable().fields()) {
            if (f.equals(getIdField())) {
                continue;
            }

            record.set(f, null);
        }

        return record;
    }

}

