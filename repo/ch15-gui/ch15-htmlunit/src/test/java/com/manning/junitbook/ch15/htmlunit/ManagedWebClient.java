/*
 * ========================================================================
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.manning.junitbook.ch15.htmlunit;

import org.htmlunit.BrowserVersion;
import org.htmlunit.SilentCssErrorHandler;
import org.htmlunit.WebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.logging.Level;

/**
 * Manages an HtmlUnit WebClient on behalf of subclasses. The class makes sure
 * the close() method is called when a test is done with a WebClient instance.
 */
public abstract class ManagedWebClient {
    protected WebClient webClient;

    @BeforeEach
    public void setUp() {
        webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);

        // 解决方案1：抑制无关警告日志
        java.util.logging.Logger.getLogger("org.htmlunit.IncorrectnessListenerImpl").setLevel(Level.OFF);

        // 解决方案2：忽略脚本错误，防止因外部脚本问题导致测试失败
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        // 解决方案3：设置JS超时时间
        webClient.getOptions().setJavaScriptEnabled(true);

        // 你已配置的：忽略CSS错误
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
    }

    @AfterEach
    public void tearDown() {
        webClient.close();
    }
}
