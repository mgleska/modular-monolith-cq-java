package com.mgleska.mmcqjava2.offer.access.console;

import com.mgleska.mmcqjava2.offer.action.command.ImportOffersCmd;
import com.mgleska.mmcqjava2.shared.annotation.SkipCoverageAkaGenerated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "offer:import", description = "Command to import offers for given store.", mixinStandardHelpOptions = true)
@SkipCoverageAkaGenerated
@Slf4j
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
            log.error("Missing parameter 'storeRid'");
            System.exit(1);
        }

        log.info("Importing offers for store with rid: {}", storeRid);
        importOffersCmd.handle(storeRid);
        log.info("Offers imported successfully!");
    }
}
