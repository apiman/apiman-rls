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

package io.apiman.rls.vertx;

import io.apiman.rls.beans.LimitPeriod;
import io.apiman.rls.beans.rest.LimitBean;
import io.apiman.rls.beans.rest.NewLimitBean;
import io.apiman.rls.beans.rest.RlsInfoBean;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.io.IOException;
import java.time.ZoneId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author eric.wittmann@gmail.com
 */
@RunWith(VertxUnitRunner.class)
@SuppressWarnings("nls")
public class RlsRestVerticleVertxTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
    }

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(RlsRestVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testVerticle(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
            response.handler(body -> {
                try {
                    RlsInfoBean bean = beanFromBody(body.toString(), RlsInfoBean.class);
                    context.assertEquals("apiman-rls-api", bean.getName());
                    context.assertEquals("A REST API to the apiman Rate Limiting [Micro-]Service.  This API provides a way for external clients to quickly and accurately query and increment named rate limits.", bean.getDescription());
                    context.assertEquals("http://localhost:8080/limits", bean.getLinks().getCreate());
                    context.assertEquals("http://localhost:8080/limits", bean.getLinks().getList());
                } catch (Exception e) {
                    context.fail(e);
                }
                async.complete();
            });
        });
    }

    @Test
    public void testCreateLimit(TestContext context) {
        final Async async = context.async();
        final String limitId = getClass().getName() + "." + "testCreateLimit-1";

        HttpClientRequest request = vertx.createHttpClient().post(8080, "localhost", "/limits/", response -> {
            response.handler(body -> {
                try {
                    LimitBean bean = beanFromBody(body.toString(), LimitBean.class);
                    context.assertEquals(limitId, bean.getId());
                    context.assertEquals("http://localhost:8080/limits/" + limitId, bean.getLinks().getSelf());
                    context.assertEquals("http://localhost:8080/limits/" + limitId, bean.getLinks().getDelete());
                    context.assertEquals("http://localhost:8080/limits/" + limitId, bean.getLinks().getIncrement());
                } catch (Exception e) {
                    context.fail(e);
                }
                async.complete();
            });
        });
        NewLimitBean nlb = new NewLimitBean();
        nlb.setId(limitId);
        nlb.setMaxValue(100L);
        nlb.setPeriod(LimitPeriod.hour);
        nlb.setTz(ZoneId.of("UTC"));
        nlb.setValue(0L);
        request.end(beanToBody(nlb), "UTF-8");
    }
    
    /**
     * Converts a standard java bean into a string so that it can be written to the
     * body of an http request.
     * @param bean
     */
    private static String beanToBody(Object bean) {
        try {
            return mapper.writerFor(bean.getClass()).writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse the body into a java bean.
     * @param body
     * @param expectedType
     * @throws Exception
     */
    private static <T> T beanFromBody(String body, Class<T> expectedType) throws Exception {
        try {
            return mapper.readerFor(expectedType).readValue(body);
        } catch (IOException e) {
            throw new Exception("Invalid JSON error: " + body, e);
        }
    }
}
