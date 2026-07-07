package com.mgleska.mmcqjava2.store.access.console;

import com.mgleska.mmcqjava2.store.action.command.ImportStoresCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "store:import", description = "Command to import stores from external dictionary (API)", mixinStandardHelpOptions = true)
public class ImportStoresCon implements Runnable  {

    private final ImportStoresCmd importStoresCmd;

    public ImportStoresCon(ImportStoresCmd importStoresCmd) {
        this.importStoresCmd = importStoresCmd;
    }

    @Override
    public void run() {
        System.out.println("Importing stores from external dictionary...");
        importStoresCmd.handle();
        System.out.println("Stores imported successfully!");
    }
}
