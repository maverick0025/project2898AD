package com.experiment.dsa1.authenticationandauthorization;

import fi.iki.elonen.NanoHTTPD;

import java.util.concurrent.BlockingQueue;

public class OAuth2Server extends NanoHTTPD {
    private final BlockingQueue<String> urlQueue;

    public OAuth2Server(int port, BlockingQueue<String> urlQueue) {
        super(port);
        this.urlQueue = urlQueue;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("/".equals(session.getUri())) {
            String fullUrl = session.getUri() + "?" + session.getQueryParameterString();

            urlQueue.offer(fullUrl);
            return newFixedLengthResponse("Authorization successful. You can close this window.");
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }
}
