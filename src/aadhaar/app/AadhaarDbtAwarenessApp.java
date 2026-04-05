package aadhaar.app;

import aadhaar.backend.AwarenessService;
import aadhaar.web.AwarenessWebServer;
import java.nio.file.Path;

public final class AadhaarDbtAwarenessApp {
    private AadhaarDbtAwarenessApp() {
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? parsePort(args[0]) : 8080;
        boolean openBrowser = true;
        for (String arg : args) {
            if ("--no-browser".equalsIgnoreCase(arg)) {
                openBrowser = false;
            }
        }

        AwarenessWebServer server = new AwarenessWebServer(new AwarenessService(), Path.of("assets"), port, openBrowser);
        server.start();
        System.out.println("Aadhaar DBT Awareness System is running at " + server.baseUrl());
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 8080;
        }
    }
}
