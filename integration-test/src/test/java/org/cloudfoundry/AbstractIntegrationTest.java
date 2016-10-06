/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(IntegrationTestConfiguration.class)
public abstract class AbstractIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.test");

    @Rule
    public final TestName testName = new TestName();

    @Autowired
    protected NameFactory nameFactory;

    @Before
    public void testEntry() {
        this.logger.debug(">> {} <<", getTestName());
    }

    @After
    public final void testExit() throws InterruptedException {
        this.logger.debug("<< {} >>", getTestName());
    }

    protected static Mono<byte[]> getBytes(String path) {
        try (InputStream in = new FileInputStream(new File("src/test/resources", path)); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            return Mono.just(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTestName() {
        return String.format("%s.%s", this.getClass().getSimpleName(), this.testName.getMethodName());
    }

}
