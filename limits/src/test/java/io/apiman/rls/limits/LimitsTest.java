/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.rls.limits;

import io.apiman.rls.beans.LimitPeriod;
import io.apiman.rls.beans.rest.LimitBean;
import io.apiman.rls.beans.rest.NewLimitBean;
import io.apiman.rls.limits.exceptions.LimitExceededException;
import io.apiman.rls.limits.exceptions.LimitNotFoundException;
import io.apiman.rls.limits.exceptions.LimitPeriodConflictException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author eric.wittmann@gmail.com
 */
@SuppressWarnings("nls")
public class LimitsTest {
    
    @Test(expected=LimitNotFoundException.class)
    public void testLimitNotFound() throws Exception {
        Limits limits = new Limits();
        limits.getLimit("test-limit");
    }
    
    @Test
    public void testCreateLimit() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.minute);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.minute, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T17:13Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());

        newLimit = new NewLimitBean();
        newLimit.setId("test-limit-2");
        newLimit.setMaxValue(10000l);
        newLimit.setPeriod(LimitPeriod.month);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-2", limit.getId());
        Assert.assertEquals(10000l, limit.getMaxValue());
        Assert.assertEquals(10000l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.month, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-11-01T00:00Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());

        newLimit = new NewLimitBean();
        newLimit.setId("test-limit-3");
        newLimit.setMaxValue(10000l);
        newLimit.setPeriod(LimitPeriod.month);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(100l);
        limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-3", limit.getId());
        Assert.assertEquals(10000l, limit.getMaxValue());
        Assert.assertEquals(10000l - 100l, limit.getRemainingValue());
        Assert.assertEquals(100l, limit.getValue());
        Assert.assertEquals(LimitPeriod.month, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-11-01T00:00Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());
    }

    @Test(expected=LimitPeriodConflictException.class)
    public void testCreateConflict() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.minute);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(LimitPeriod.minute, limit.getPeriod());

        newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10000l);
        newLimit.setPeriod(LimitPeriod.month);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
    }

    @Test
    public void testCreateLimitAsIncrement() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.hour);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.hour, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T18:00Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());
        
        newLimit.setValue(1);
        for (int i = 0; i < 10; i++) {
            ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + i, 0, ZoneId.of("UTC"));
            limits.createLimit(time, newLimit);
        }
        
        // We've reached the limit - the next one should fail.
        ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + 11, 0, ZoneId.of("UTC"));
        try {
            limits.createLimit(time, newLimit);
        } catch (LimitExceededException e) {
            Assert.assertEquals("2015-10-07T18:00Z[UTC]", e.getResetOn().toString());
        }
    }

    @Test
    public void testIncrementPerHour() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.hour);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.hour, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T18:00Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());
        
        newLimit.setValue(1);
        for (int i = 0; i < 10; i++) {
            ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + i, 0, ZoneId.of("UTC"));
            LimitBean incrementLimit = limits.incrementLimit(time, "test-limit-1", 1);
            Assert.assertEquals(10 - i - 1, incrementLimit.getRemainingValue());
        }
        
        // We've reached the limit - the next one should fail.
        ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + 11, 0, ZoneId.of("UTC"));
        try {
            limits.incrementLimit(time, "test-limit-1", 1);
        } catch (LimitExceededException e) {
            Assert.assertEquals("2015-10-07T18:00Z[UTC]", e.getResetOn().toString());
        }
    }

    @Test
    public void testIncrementPerHourThreaded() throws Exception {
        final Limits limits = new Limits();
        
        final NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(1000l);
        newLimit.setPeriod(LimitPeriod.hour);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(1000l, limit.getMaxValue());
        Assert.assertEquals(1000l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.hour, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T18:00Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());
        
        newLimit.setValue(1);
        
        // Now spin up 100 threads, each one will increment 10 times - they should all pass
        CountDownLatch latch = new CountDownLatch(100);
        final List<String> errors = new ArrayList<>();
        for (int numThreads = 0; numThreads < 100; numThreads++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + i, 0, ZoneId.of("UTC"));
                        try {
                            limits.incrementLimit(time, "test-limit-1", 1);
                        } catch (LimitNotFoundException e) {
                            errors.add("LimitNotFound error in thread " + Thread.currentThread().getName());
                        } catch (LimitExceededException e) {
                            errors.add("LimitExceeded error in thread " + Thread.currentThread().getName());
                        }
                    }
                    latch.countDown();
                }
            }, "incrementer-" + numThreads);
            thread.start();
        }
        latch.await();
        
        // There should be no errors in the list
        Assert.assertTrue("Error(s) detected: " + errors.toArray(), errors.isEmpty());
        
        // We've reached the limit - the next one should fail.
        ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39 + 11, 0, ZoneId.of("UTC"));
        try {
            limits.incrementLimit(time, "test-limit-1", 1);
        } catch (LimitExceededException e) {
            Assert.assertEquals("2015-10-07T18:00Z[UTC]", e.getResetOn().toString());
        }
    }

    @Test
    public void testIncrementPerSecondWithReset() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.second);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.second, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T17:12:40Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());
        
        newLimit.setValue(1);
        for (int i = 0; i < 10; i++) {
            ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39, i, ZoneId.of("UTC"));
            LimitBean incrementLimit = limits.incrementLimit(time, "test-limit-1", 1);
            Assert.assertEquals(10 - i - 1, incrementLimit.getRemainingValue());
        }
        
        // We've reached the limit - the next one should fail.
        ZonedDateTime time = ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 11, ZoneId.of("UTC"));
        try {
            limits.incrementLimit(time, "test-limit-1", 1);
        } catch (LimitExceededException e) {
            Assert.assertEquals("2015-10-07T17:12:40Z[UTC]", e.getResetOn().toString());
        }
        
        // If the next request comes in after the reset time, then it should work again
        time = ZonedDateTime.of(2015, 10, 7, 17, 12, 40, 99, ZoneId.of("UTC"));
        LimitBean incrementLimit = limits.incrementLimit(time, "test-limit-1", 1);
        Assert.assertEquals(9, incrementLimit.getRemainingValue());
        Assert.assertEquals("2015-10-07T17:12:41Z[UTC]", incrementLimit.getResetOn().toString());
    }

    @Test
    public void testGetLimit() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.minute);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.minute, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T17:13Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());

        LimitBean retrievedLimit = limits.getLimit("test-limit-1");
        Assert.assertEquals("test-limit-1", retrievedLimit.getId());
        Assert.assertEquals(10l, retrievedLimit.getMaxValue());
        Assert.assertEquals(10l, retrievedLimit.getRemainingValue());
        Assert.assertEquals(0l, retrievedLimit.getValue());
        Assert.assertEquals(LimitPeriod.minute, retrievedLimit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", retrievedLimit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T17:13Z[UTC]", retrievedLimit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", retrievedLimit.getModifiedOn().toString());
    }

    @Test
    public void testDeleteLimit() throws Exception {
        Limits limits = new Limits();
        
        NewLimitBean newLimit = new NewLimitBean();
        newLimit.setId("test-limit-1");
        newLimit.setMaxValue(10l);
        newLimit.setPeriod(LimitPeriod.minute);
        newLimit.setTz(ZoneId.of("UTC"));
        newLimit.setValue(0l);
        LimitBean limit = limits.createLimit(ZonedDateTime.of(2015, 10, 7, 17, 12, 39, 0, ZoneId.of("UTC")), newLimit);
        Assert.assertEquals("test-limit-1", limit.getId());
        Assert.assertEquals(10l, limit.getMaxValue());
        Assert.assertEquals(10l, limit.getRemainingValue());
        Assert.assertEquals(0l, limit.getValue());
        Assert.assertEquals(LimitPeriod.minute, limit.getPeriod());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getCreatedOn().toString());
        Assert.assertEquals("2015-10-07T17:13Z[UTC]", limit.getResetOn().toString());
        Assert.assertEquals("2015-10-07T17:12:39Z[UTC]", limit.getModifiedOn().toString());

        limits.deleteLimit(ZonedDateTime.now(ZoneId.of("UTC")), "test-limit-1");
        
        try {
            limits.getLimit("test-limit-1");
            Assert.fail("Expected a limit-not-found error.");
        } catch (LimitNotFoundException e) {
            // OK!  This is expected.
        }
    }

    @Test(expected=LimitNotFoundException.class)
    public void testDeleteLimitNotFound() throws Exception {
        Limits limits = new Limits();
        
        limits.deleteLimit(ZonedDateTime.now(ZoneId.of("UTC")), "test-limit-1");
    }

    /**
     * Test method for {@link io.apiman.rls.limits.Limits#nextResetTime(java.time.ZonedDateTime, io.apiman.rls.beans.LimitPeriod)}.
     */
    @Test
    public void testNextResetTime() {
        ZonedDateTime dateTime = ZonedDateTime.of(2010, 2, 10, 12, 35, 21, 19, ZoneId.of("America/New_York"));

        ZonedDateTime actual = Limits.nextResetTime(dateTime, LimitPeriod.second);
        Assert.assertEquals("2010-02-10T12:35:22-05:00[America/New_York]", actual.toString());

        actual = Limits.nextResetTime(dateTime, LimitPeriod.minute);
        Assert.assertEquals("2010-02-10T12:36-05:00[America/New_York]", actual.toString());

        actual = Limits.nextResetTime(dateTime, LimitPeriod.hour);
        Assert.assertEquals("2010-02-10T13:00-05:00[America/New_York]", actual.toString());

        actual = Limits.nextResetTime(dateTime, LimitPeriod.day);
        Assert.assertEquals("2010-02-11T00:00-05:00[America/New_York]", actual.toString());

        actual = Limits.nextResetTime(dateTime, LimitPeriod.month);
        Assert.assertEquals("2010-03-01T00:00-05:00[America/New_York]", actual.toString());

        actual = Limits.nextResetTime(dateTime, LimitPeriod.year);
        Assert.assertEquals("2011-01-01T00:00-05:00[America/New_York]", actual.toString());

    }

}
