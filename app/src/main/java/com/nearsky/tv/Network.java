package com.nearsky.tv;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.*;

final class Network {
    private Network() { }
    static byte[] bytes(String address, int maxBytes) throws IOException {
        URL url = new URL(address);
        if (!"https".equals(url.getProtocol())) throw new IOException("HTTPS required");
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        c.setConnectTimeout(8000); c.setReadTimeout(10000);
        c.setRequestProperty("User-Agent", "NearSky-AndroidTV/1.0");
        try {
            int status = c.getResponseCode();
            if (status != 200) throw new IOException("Service returned HTTP " + status);
            if (!"https".equals(c.getURL().getProtocol())) throw new IOException("Insecure redirect");
            try (InputStream in = c.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192]; int n;
                while ((n = in.read(buffer)) != -1) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedIOException();
                    if (out.size() + n > maxBytes) throw new IOException("Response too large");
                    out.write(buffer, 0, n);
                }
                return out.toByteArray();
            }
        } finally { c.disconnect(); }
    }
    static JSONObject json(String url) throws IOException, JSONException {
        return new JSONObject(new String(bytes(url, 2 * 1024 * 1024), StandardCharsets.UTF_8));
    }
}
