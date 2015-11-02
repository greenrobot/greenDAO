package de.greenrobot.performance;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import de.greenrobot.performance.common.BuildConfig;

/**
 * Base test case including some helper methods when running a performance test.
 *
 * <p/><b>Note:</b> To run a single test, create a new "Android Tests" run configuration in Android
 * Studio. Right-click to run for Android Tests currently does not work in abstract classes.
 */
public abstract class BasePerfTestCase extends ApplicationTestCase<Application> {

    protected static final int QUERY_COUNT = 1000;
    protected static final int RUNS = 8;
    private static final int BATCH_SIZE = 10000;

    private long start;

    public BasePerfTestCase() {
        super(Application.class);
    }

    protected int getBatchSize() {
        return BATCH_SIZE;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }

    protected void startClock() {
        if (start != 0) {
            throw new IllegalStateException("Call stopClock before starting it again.");
        }
        start = System.currentTimeMillis();
    }

    protected void stopClock(LogMessage type) {
        long time = System.currentTimeMillis() - start;
        start = 0;

        String message = null;
        if (type == LogMessage.QUERY_INDEXED) {
            message = "Queried for " + QUERY_COUNT + " of " + getBatchSize()
                    + " indexed entities in "
                    + time + " ms.";
        } else if (type == LogMessage.BATCH_CREATE) {
            message = "Created (batch) " + getBatchSize() + " entities in " + time + " ms";
        } else if (type == LogMessage.BATCH_UPDATE) {
            message = "Updated (batch) " + getBatchSize() + " entities in " + time + " ms";
        } else if (type == LogMessage.BATCH_READ) {
            message = "Read (batch) " + getBatchSize() + " entities in " + time + " ms";
        } else if (type == LogMessage.BATCH_ACCESS) {
            message = "Accessed properties of " + getBatchSize() + " entities in " + time + " ms";
        } else if (type == LogMessage.ONE_BY_ONE_CREATE) {
            message = "Inserted (one-by-one) " + getBatchSize() / 10 + " entities in " + time
                    + " ms";
        } else if (type == LogMessage.ONE_BY_ONE_UPDATE) {
            message = "Updated (one-by-one) " + getBatchSize() / 10 + " entities in " + time
                    + " ms";
        } else if (type == LogMessage.BATCH_DELETE) {
            message = "Deleted (batch) all entities in " + time + " ms";
        }

        if (message != null) {
            log(message);
        }
    }

    /**
     * Convenience method to create a debug log message.
     */
    protected void log(String message) {
        Log.d(getLogTag(), message);
    }

    protected abstract String getLogTag();

    public void testIndexedStringEntityQueries() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(getLogTag(), "Performance tests are disabled.");
            return;
        }

        Log.d(getLogTag(), "--------Indexed Queries: Start");
        doIndexedStringEntityQueries();
        Log.d(getLogTag(), "--------Indexed Queries: End");
    }

    public void testSingleAndBatchCrud() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(getLogTag(), "Performance tests are disabled.");
            return;
        }

        Log.d(getLogTag(), "--------One-by-one/Batch CRUD: Start");
        doOneByOneAndBatchCrud();
        Log.d(getLogTag(), "--------One-by-one/Batch CRUD: End");
    }

    /**
     * Create entities with a string property, populate them with {@link
     * StringGenerator#createFixedRandomStrings(int)}. Then query for the fixed set of indexes given
     * by {@link StringGenerator#getFixedRandomIndices(int, int)}. See existing tests for guidance.
     */
    protected abstract void doIndexedStringEntityQueries() throws Exception;

    /**
     * Run one-by-one create, update. Delete all. Then batch create, update, load and access. Delete
     * all. See existing tests for guidance.
     */
    protected abstract void doOneByOneAndBatchCrud() throws Exception;

    public enum LogMessage {
        BATCH_CREATE,
        BATCH_UPDATE,
        BATCH_READ,
        BATCH_ACCESS,
        ONE_BY_ONE_CREATE,
        ONE_BY_ONE_UPDATE,
        BATCH_DELETE,
        QUERY_INDEXED
    }
}
