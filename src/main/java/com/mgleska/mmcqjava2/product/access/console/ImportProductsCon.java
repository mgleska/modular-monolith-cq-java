package com.mgleska.mmcqjava2.product.access.console;

import com.mgleska.mmcqjava2.product.action.command.ImportProductsCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "product:import", description = "Command to import products from external dictionary (API).", mixinStandardHelpOptions = true)
public class ImportProductsCon implements Runnable  {

    private final ImportProductsCmd importProductsCmd;

    public ImportProductsCon(ImportProductsCmd importProductsCmd) {
        this.importProductsCmd = importProductsCmd;
    }

    @Override
    public void run() {
        System.out.println("Importing products from external dictionary...");
        importProductsCmd.handle();
        System.out.println("Products imported successfully!");
    }
}
