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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author eric.wittmann@gmail.com
 */
@SuppressWarnings("nls")
public class RlsRestVerticleTest {
    
    @Test
    public void testParseQuery() {
        Map<String, String> map = RlsRestVerticle.parseQuery("");
        Assert.assertTrue(map.isEmpty());

        map = RlsRestVerticle.parseQuery("param=value");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("value", map.get("param"));

        map = RlsRestVerticle.parseQuery("param1=value&param2=value");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("value", map.get("param1"));
        Assert.assertEquals("value", map.get("param2"));

        map = RlsRestVerticle.parseQuery("param1=value&param2=value&other=hello+world");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("value", map.get("param1"));
        Assert.assertEquals("value", map.get("param2"));
        Assert.assertEquals("hello+world", map.get("other"));
    }

}
