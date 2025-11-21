package com.manning.junitbook.ch07;

import org.junit.jupiter.api.*;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.util.ByteArrayISO8859Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TestMyClient {

    private final WebClient webClient = new WebClient();

    @BeforeAll
    static void setup() throws Exception {

        final Server server = new Server(8081);

        final Context ctx = new Context(server, "/");
        ctx.setHandler(new TestGetContentOkHandler());

        server.setStopAtShutdown(true);
        server.start();
    }

    @AfterAll
    static void teardown() {
        // snip
    }

    @Test
    void testGetContentOk() throws MalformedURLException {
        final String result = webClient.getContent(new URL("http://localhost:8081/testGetContentOk"));
        Assertions.assertEquals("<h1>It works!</h1>", result);
    }

    private static class TestGetContentOkHandler extends AbstractHandler {
        @Override
        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                throws IOException {

            try(OutputStream out = response.getOutputStream();
                final ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer()
            ) {
                writer.write("<h1>It works!</h1>");
                writer.flush();
                response.setIntHeader(HttpHeaders.CONTENT_LENGTH, writer.size());
                writer.writeTo(out);
                out.flush();
            }
        }
    }
}
