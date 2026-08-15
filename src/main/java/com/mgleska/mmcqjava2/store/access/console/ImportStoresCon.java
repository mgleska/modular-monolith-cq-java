package com.mgleska.mmcqjava2.store.access.console;

import com.mgleska.mmcqjava2.shared.annotation.SkipCoverageAkaGenerated;
import com.mgleska.mmcqjava2.store.action.command.ImportStoresCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "store:import", description = "Command to import stores from external dictionary (API)", mixinStandardHelpOptions = true)
@SkipCoverageAkaGenerated
@Slf4j
public class ImportStoresCon implements Runnable  {

    private final ImportStoresCmd importStoresCmd;

    public ImportStoresCon(ImportStoresCmd importStoresCmd) {
        this.importStoresCmd = importStoresCmd;
    }

    @Override
    public void run() {
        log.info("Importing stores from external dictionary...");
        importStoresCmd.handle();
        log.info("Stores imported successfully!");
    }
}
