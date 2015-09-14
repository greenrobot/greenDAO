package de.greenrobot.performance.parse;

import android.os.Debug;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParsePerfTest {

    long start;
    private String traceName;
    boolean useTraceView = false;

    public void testPerformance() throws Exception {
        runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
    }

    protected void runTests(int entityCount) throws ParseException {
        DaoLog.d("####################");
        DaoLog.d(getClass().getSimpleName() + ": " + entityCount + " entities on " + new Date());
        DaoLog.d("####################");

        List<ParseObject> list = new ArrayList<ParseObject>(entityCount);
        startClock("create");
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity(i));
        }
        stopClock();
        System.gc();

        //TODO dao.deleteAll();
        // runOneByOneTests(list, entityCount, entityCount / 10);
        //TODO dao.deleteAll();
        DaoLog.d("------------------------");
        System.gc();

        runBatchTests(list);

//        startClock("delete-all");
        // TODO dao.deleteAll();
//        stopClock();
        System.gc();
    }

//    protected void runOneByOneTests(List<T> list, int loadCount, int modifyCount) {
//        dao.insertInTx(list);
//        List<K> keys = new ArrayList<K>(loadCount);
//        for (int i = 0; i < loadCount; i++) {
//            keys.add(daoAccess.getKey(list.get(i)));
//        }
//        clearIdentityScopeIfAny();
//        System.gc();
//
//        list = runLoadOneByOne(keys, "load-one-by-one-1");
//        list = runLoadOneByOne(keys, "load-one-by-one-2");
//        Debug.stopMethodTracing();
//
//        dao.deleteAll();
//        System.gc();
//
//        startClock("insert-one-by-one");
//        for (int i = 0; i < modifyCount; i++) {
//            dao.insert(list.get(i));
//        }
//        stopClock(modifyCount + " entities");
//        System.gc();
//
//        startClock("update-one-by-one");
//        for (int i = 0; i < modifyCount; i++) {
//            dao.update(list.get(i));
//        }
//        stopClock(modifyCount + " entities");
//        System.gc();
//
//        startClock("delete-one-by-one");
//        for (int i = 0; i < modifyCount; i++) {
//            dao.delete(list.get(i));
//        }
//        stopClock(modifyCount + " entities");
//        System.gc();
//    }

//    protected List<T> runLoadOneByOne(List<K> keys, String traceName) {
//        List<T> list = new ArrayList<T>(keys.size());
//        startClock(traceName);
//        for (K key : keys) {
//            list.add(dao.load(key));
//        }
//        stopClock(keys.size() + " entities");
//        return list;
//    }

    protected void runBatchTests(List<ParseObject> list) throws ParseException {
        startClock("insert");
        ParseObject.pinAll(list);
        stopClock(list.size() + " entities");

        list = null;
        System.gc();

        list = runLoadAll("load-all-1");
        list = runLoadAll("load-all-2");

        startClock("update");
        ParseObject.pinAll(list);
        stopClock(list.size() + " entities");

        startClock("delete");
        ParseObject.unpinAll(list);
        stopClock(list.size() + " entities");
    }

    protected List<ParseObject> runLoadAll(String traceName) throws ParseException {
        startClock(traceName);
        List<ParseObject> list = ParseQuery.getQuery("SimpleEntity").fromLocalDatastore().find();
        int dummy = 0;
        for (ParseObject entity : list) {
            boolean simpleBoolean = entity.getBoolean("simpleBoolean");
            int simpleByte = entity.getInt("simpleByte");
            int simpleShort = entity.getInt("simpleShort");
            int simpleInt = entity.getInt("simpleInt");
            long simpleLong = entity.getLong("simpleLong");
            double simpleFloat = entity.getDouble("simpleFloat");
            double simpleDouble = entity.getDouble("simpleDouble");
            String simpleString = entity.getString("simpleString");
            byte[] simpleByteArrays = entity.getBytes("simpleByteArray");
            dummy+=simpleByte+simpleShort+simpleInt;
        }

        stopClock(list.size() + " entities");
        DaoLog.i("Dummy: "+dummy);
        return list;
    }

    protected void startClock(String traceName) {
        System.gc();
        this.traceName = traceName;
        if (useTraceView) {
            Debug.startMethodTracing(traceName);
        }
        start = System.currentTimeMillis();
    }

    protected void stopClock() {
        stopClock(null);
    }

    protected void stopClock(String extraInfoOrNull) {
        long time = System.currentTimeMillis() - start;
        String extraLog = extraInfoOrNull != null ? " (" + extraInfoOrNull + ")" : "";
        DaoLog.d(traceName + " completed in " + time + "ms" + extraLog);
        if (useTraceView) {
            Debug.stopMethodTracing();
        }
        System.gc();
    }

    public ParseObject createEntity(int nr) {
        ParseObject entity = new ParseObject("SimpleEntity");
        entity.put("simpleBoolean", true);
        entity.put("simpleByte", nr&0xff);
        entity.put("simpleShort", nr&0xffff);
        entity.put("simpleInt", nr);
        entity.put("simpleLong", Long.MAX_VALUE-nr);
        entity.put("simpleFloat", (float)(Math.PI * nr));
        entity.put("simpleDouble", Math.E *nr);
        entity.put("simpleString", "greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.put("simpleByteArray", bytes);
        return entity;
    }
}
