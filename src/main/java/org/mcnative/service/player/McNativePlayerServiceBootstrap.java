package org.mcnative.service.player;

import io.sentry.Sentry;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.logging.PretronicLoggerFactory;
import net.pretronic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import net.pretronic.libraries.logging.io.LoggingPrintStream;
import org.mcnative.service.player.util.Environment;

public final class McNativePlayerServiceBootstrap {

    public static void main(String[] args) {
        boolean development = Environment.getVariable("ENVIRONMENT","development").equalsIgnoreCase("development");

        PretronicLogger logger = PretronicLoggerFactory.getLogger("McNativeMonitoringService");
        SLF4JStaticBridge.setLogger(logger);
        LoggingPrintStream.hook(logger);

        String dsn = Environment.getVariableOrNull("SENTRY_DSN");
        if(dsn != null && !development){
            Sentry.init(options -> options.setDsn(dsn));
        }

        McNativePlayerService service = new McNativePlayerService(logger);
        Runtime.getRuntime().addShutdownHook(new Thread(service::stop));
    }
}
