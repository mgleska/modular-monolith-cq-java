package com.mgleska.mmcqjava2.product.access.console;

import com.mgleska.mmcqjava2.product.action.command.ImportProductsCmd;
import com.mgleska.mmcqjava2.shared.annotation.SkipCoverageAkaGenerated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "product:import", description = "Command to import products from external dictionary (API).", mixinStandardHelpOptions = true)
@SkipCoverageAkaGenerated
@Slf4j
public class ImportProductsCon implements Runnable  {

    private final ImportProductsCmd importProductsCmd;

    public ImportProductsCon(ImportProductsCmd importProductsCmd) {
        this.importProductsCmd = importProductsCmd;
    }

    @Override
    public void run() {
        log.info("Importing products from external dictionary...");
        importProductsCmd.handle();
        log.info("Products imported successfully!");
    }
}
