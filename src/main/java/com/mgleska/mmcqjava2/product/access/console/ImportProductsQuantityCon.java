package com.mgleska.mmcqjava2.product.access.console;

import com.mgleska.mmcqjava2.product.action.command.ImportQuantityCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "product:quantity", description = "Command to import quantities for each products in all stores (API).", mixinStandardHelpOptions = true)
public class ImportProductsQuantityCon implements Runnable  {

    private final ImportQuantityCmd action;

    public ImportProductsQuantityCon(ImportQuantityCmd importQuantityCmd) {
        this.action = importQuantityCmd;
    }

    @Override
    public void run() {
        System.out.println("Importing product quantity...");
        action.handle();
        System.out.println("Product quantity imported successfully!");
    }
}
