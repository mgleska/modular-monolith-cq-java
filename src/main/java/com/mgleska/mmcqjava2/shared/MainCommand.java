package com.mgleska.mmcqjava2.shared;

import com.mgleska.mmcqjava2.offer.access.console.ImportOffersCon;
import com.mgleska.mmcqjava2.product.access.console.ImportProductsCon;
import com.mgleska.mmcqjava2.product.access.console.ImportProductsQuantityCon;
import com.mgleska.mmcqjava2.store.access.console.ImportStoresCon;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "app-cli",
        mixinStandardHelpOptions = true,
        subcommands = { ImportStoresCon.class, ImportOffersCon.class, ImportProductsCon.class, ImportProductsQuantityCon.class },
        description = "Available command line actions for application"
)
public class MainCommand implements Runnable {
    @Override
    public void run() {
        // This method will only execute if the user enters the command itself without any subcommands.
        System.out.println("Enter the subcommand. Use --help to see options.");
    }
}
