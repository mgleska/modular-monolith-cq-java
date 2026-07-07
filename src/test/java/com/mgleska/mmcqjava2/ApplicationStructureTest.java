package com.mgleska.mmcqjava2;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationStructureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(MmCqJava2Application.class).verify();
        modules.verify();
    }
}
