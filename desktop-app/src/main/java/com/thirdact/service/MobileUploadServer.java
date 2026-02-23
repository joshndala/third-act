package com.thirdact.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.function.Consumer;

/**
 * A lightweight HTTP server to receive uploaded photos over the local network.
 */
public class MobileUploadServer {

    private HttpServer server;
    private int port;
    private String serverUrl;
    private Consumer<File> onFileReceived;
    private Runnable onServerStopped;

    public MobileUploadServer(Consumer<File> onFileReceived, Runnable onServerStopped) {
        this.onFileReceived = onFileReceived;
        this.onServerStopped = onServerStopped;
    }

    /**
     * Starts the server on a random available port.
     * 
     * @return true if successfully started, false otherwise.
     */
    public boolean start() {
        try {
            // Port 0 tells the OS to assign a random available port
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", 0), 0);
            port = server.getAddress().getPort();

            // Set up endpoints
            server.createContext("/", new HtmlHandler());
            server.createContext("/upload", new UploadHandler());

            server.setExecutor(null); // Use default executor
            server.start();

            String ipAddress = getLocalIpAddress();
            serverUrl = "http://" + ipAddress + ":" + port;

            System.out.println("[MobileUploadServer] Started at " + serverUrl);
            return true;

        } catch (IOException e) {
            System.err.println("[MobileUploadServer] Failed to start server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[MobileUploadServer] Stopped.");
            if (onServerStopped != null) {
                onServerStopped.run();
            }
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // We only want IPv4 addresses
                    if (inetAddress.getHostAddress().contains(".")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[MobileUploadServer] Could not determine local IP: " + e.getMessage());
        }
        return "127.0.0.1"; // Fallback
    }

    /**
     * Handler to serve the HTML upload page.
     */
    private class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            String htmlResponse = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <!-- Essential for proper zooming and scaling on mobile devices -->
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <title>Upload Note</title>
                        <style>
                            body {
                                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                                margin: 0;
                                padding: 20px;
                                display: flex;
                                flex-direction: column;
                                align-items: center;
                                justify-content: center;
                                min-height: 100vh;
                                background-color: #121212;
                                color: #ffffff;
                                text-align: center;
                            }
                            h2 { margin-bottom: 5px; }
                            p { color: #cccccc; margin-bottom: 30px; }

                            /* Hide default file input */
                            input[type="file"] {
                                display: none;
                            }

                            /* Custom button styles */
                            .upload-btn {
                                background-color: #4caf50;
                                color: white;
                                padding: 15px 30px;
                                font-size: 18px;
                                font-weight: bold;
                                border: none;
                                border-radius: 8px;
                                cursor: pointer;
                                display: inline-block;
                                box-shadow: 0 4px 6px rgba(0,0,0,0.3);
                                transition: background-color 0.2s;
                                width: 80%;
                                max-width: 300px;
                            }
                            .upload-btn:active {
                                background-color: #388e3c;
                                transform: translateY(2px);
                            }

                            #status {
                                margin-top: 20px;
                                font-size: 16px;
                                display: none;
                            }
                            .success { color: #4caf50; }
                            .error { color: #f44336; }
                            .spinner {
                                display: inline-block;
                                width: 20px;
                                height: 20px;
                                border: 3px solid rgba(255,255,255,.3);
                                border-radius: 50%;
                                border-top-color: #fff;
                                animation: spin 1s ease-in-out infinite;
                                margin-right: 10px;
                                vertical-align: middle;
                            }
                            @keyframes spin {
                                to { transform: rotate(360deg); }
                            }
                        </style>
                    </head>
                    <body>
                        <h2>Upload Handwritten Note</h2>
                        <p>Take a photo or choose an image</p>

                        <label class="upload-btn">
                            Upload Photo
                            <!-- The accept attribute forces iOS to transcode HEIC to JPEG -->
                            <input type="file" id="fileInput" accept="image/jpeg,image/png,image/webp">
                        </label>
                        <div id="status"></div>

                        <script>
                            const fileInput = document.getElementById('fileInput');
                            const statusDiv = document.getElementById('status');
                            const btnLabel = document.querySelector('.upload-btn');

                            fileInput.addEventListener('change', async (e) => {
                                if (e.target.files.length === 0) return;

                                const file = e.target.files[0];

                                // UI Updates
                                statusDiv.style.display = 'block';
                                statusDiv.className = '';
                                statusDiv.innerHTML = '<span class="spinner"></span> Uploading...';
                                btnLabel.style.display = 'none'; // Hide button while uploading

                                try {
                                    const response = await fetch('/upload', {
                                        method: 'POST',
                                        headers: {
                                            // Pass filename so server can save with correct extension
                                            'X-File-Name': encodeURIComponent(file.name)
                                        },
                                        body: file // Send raw binary data
                                    });

                                    if (response.ok) {
                                        statusDiv.innerHTML = '✅ Upload successful! You can close this page.';
                                        statusDiv.className = 'success';
                                    } else {
                                        throw new Error('Server returned ' + response.status);
                                    }
                                } catch (err) {
                                    statusDiv.innerHTML = '❌ Upload failed: ' + err.message;
                                    statusDiv.className = 'error';
                                    btnLabel.style.display = 'inline-block'; // Show button again so they can retry
                                }
                            });
                        </script>
                    </body>
                    </html>
                    """;

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlResponse.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(htmlResponse.getBytes());
            }
        }
    }

    /**
     * Handler to receive the binary file data POST.
     */
    private class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Extract filename extension if provided, default to .jpg
            String filename = "mobile_upload.jpg";
            String headerVal = exchange.getRequestHeaders().getFirst("X-File-Name");
            if (headerVal != null && !headerVal.isBlank()) {
                filename = java.net.URLDecoder.decode(headerVal, "UTF-8");
                // Safety: only keep the extension
                int dotIndex = filename.lastIndexOf('.');
                if (dotIndex != -1) {
                    filename = "mobile_upload" + filename.substring(dotIndex);
                }
            }

            // Create temporary file
            File tempFile = File.createTempFile("thirdact_", "_" + filename);
            tempFile.deleteOnExit();

            // Read the binary stream directly into the file
            try (InputStream is = exchange.getRequestBody();
                    FileOutputStream fos = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Send success response
            String response = "OK";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            // Notify reality that file was received
            if (onFileReceived != null) {
                // Must run on background or UI thread depending on what happens next.
                // Stopping the server closes the exchange streams completely.
                new Thread(() -> {
                    onFileReceived.accept(tempFile);
                    stop(); // Shut down the server once successful upload completes
                }).start();
            }
        }
    }
}
