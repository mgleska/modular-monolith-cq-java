package com.mgleska.mmcqjava2.offer.access.console;

import com.mgleska.mmcqjava2.offer.action.command.ImportOffersCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "offer:import", description = "Command to import offers for given store.", mixinStandardHelpOptions = true)
public class ImportOffersCon implements Runnable  {

    private final ImportOffersCmd importOffersCmd;

    @Parameters(index = "0")
    String storeRid;

    public ImportOffersCon(ImportOffersCmd importOffersCmd) {
        this.importOffersCmd = importOffersCmd;
    }

    @Override
    public void run() {
        if (storeRid == null || storeRid.isEmpty()) {
            System.out.println("Missing parameter 'storeRid'");
            System.exit(1);
        }

        System.out.println("Importing offers for store with rid: " + storeRid + " ...");
        importOffersCmd.handle(storeRid);
        System.out.println("Offers imported successfully!");
    }
}
