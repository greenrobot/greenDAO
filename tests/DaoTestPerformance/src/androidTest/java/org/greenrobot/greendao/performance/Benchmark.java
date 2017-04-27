/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.performance;

import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import org.greenrobot.essentials.StringUtils;
import org.greenrobot.essentials.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Benchmark {
    public static final String TAG = "Benchmark";

    private final List<Pair<String, String>> fixedColumns = new ArrayList<>();
    private final List<Pair<String, String>> values = new ArrayList<>();
    private final File file;
    private final SimpleDateFormat dateFormat;
    private final char separator = '\t';

    private String[] headers;
    private boolean storeThreadTime;

    private boolean started;
    private long threadTimeMillis;
    private long timeMillis;
    private String name;
    private int runs;
    private int warmUpRuns;

    public Benchmark(File file) {
        this.file = file;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        checkForLastHeader(file);
    }

    private void checkForLastHeader(File file) {
        String contents = null;
        try {
            contents = FileUtils.readUtf8(file);
        } catch (FileNotFoundException e) {
            // OK
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (contents == null) {
            return;
        }

        String[] lines = StringUtils.split(contents, '\n');
        for (int i = lines.length - 1; i >= 0; i--) {
            String[] columnValues = StringUtils.split(lines[i], separator);
            if (columnValues.length > 1) {
                boolean longValueFound = false;
                for (String value : columnValues) {
                    try {
                        Long.parseLong(value);
                        longValueFound = true;
                        break;
                    } catch (NumberFormatException e) {
                        // OK, header candidate
                    }
                }
                if (!longValueFound) {
                    headers = columnValues;
                    break;
                }
            }
        }
    }

    public Benchmark warmUpRuns(int warmUpRuns) {
        this.warmUpRuns = warmUpRuns;
        return this;
    }

    public Benchmark enableThreadTime() {
        this.storeThreadTime = true;
        return this;
    }

    public Benchmark disableThreadTime() {
        this.storeThreadTime = false;
        return this;
    }

    public Benchmark addFixedColumn(String key, String value) {
        fixedColumns.add(new Pair<String, String>(key, value));
        return this;
    }

    public Benchmark addFixedColumnDevice() {
        addFixedColumn("device", Build.MODEL);
        return this;
    }

    public void start(String name) {
        if (started) {
            throw new RuntimeException("Already started");
        }
        started = true;
        prepareForNextRun();
        if (values.isEmpty()) {
            values.addAll(fixedColumns);
            String startTime = dateFormat.format(new Date());
            values.add(new Pair<>("time", startTime));
        }
        this.name = name;
        threadTimeMillis = SystemClock.currentThreadTimeMillis();
        timeMillis = SystemClock.elapsedRealtime();
    }

    /**
     * Try to give GC some time to settle down.
     */
    public void prepareForNextRun() {
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        long time = SystemClock.elapsedRealtime() - timeMillis;
        long timeThread = SystemClock.currentThreadTimeMillis() - threadTimeMillis;
        if (!started) {
            throw new RuntimeException("Not started");
        }
        started = false;

        Log.d(TAG, name + ": " + time + " ms (thread: " + timeThread + " ms)");
        values.add(new Pair<>(name, Long.toString(time)));
        if (storeThreadTime) {
            values.add(new Pair<>(name + "-thread", Long.toString(timeThread)));
        }
        name = null;
    }

    public void commit() {
        runs++;
        if (runs > warmUpRuns) {
            Log.d(TAG, "Writing results for run " + runs);
            String[] collectedHeaders = getAllFirsts(values);
            if (!Arrays.equals(collectedHeaders, headers)) {
                headers = collectedHeaders;
                String line = StringUtils.join(headers, "" + separator) + '\n';
                try {
                    FileUtils.appendUtf8(file, line);
                } catch (IOException e) {
                    throw new RuntimeException("Could not write header in benchmark file", e);
                }
            }

            StringBuilder line = new StringBuilder();
            for (Pair<String, String> pair : values) {
                line.append(pair.second).append(separator);
            }
            line.append('\n');
            try {
                FileUtils.appendUtf8(file, line);
            } catch (IOException e) {
                throw new RuntimeException("Could not write header in benchmark file", e);
            }
        } else {
            Log.d(TAG, "Ignoring results for run " + runs + " (warm up)");
        }
        values.clear();
    }

    private String[] getAllFirsts(List<Pair<String, String>> columns) {
        String[] firsts = new String[columns.size()];
        for (int i = 0; i < firsts.length; i++) {
            firsts[i] = columns.get(i).first;
        }
        return firsts;
    }
}
