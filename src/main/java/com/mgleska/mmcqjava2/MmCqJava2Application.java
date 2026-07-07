package com.mgleska.mmcqjava2;

import com.mgleska.mmcqjava2.shared.CustomExceptionHandler;
import com.mgleska.mmcqjava2.shared.MainCommand;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.modulith.Modulithic;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.util.Set;

@Modulithic
@SpringBootApplication
public class MmCqJava2Application implements CommandLineRunner {

    private final MainCommand mainCommand;
    private final IFactory factory;
    private final CustomExceptionHandler customExceptionHandler;

    private static final Set<String> CLI_KEYWORDS = Set.of("-h", "--help", "-V", "--version", "app-cli");

    public MmCqJava2Application(MainCommand mainCommand, IFactory factory, CustomExceptionHandler customExceptionHandler) {
        this.mainCommand = mainCommand;
        this.factory = factory;
        this.customExceptionHandler = customExceptionHandler;
    }

    static void main(String[] args) {
        boolean isCliContext = hasCliArgs(args);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(MmCqJava2Application.class)
                .web(isCliContext ? WebApplicationType.NONE : WebApplicationType.SERVLET)
                .run(args);

        if (isCliContext) {
            System.exit(SpringApplication.exit(context));
        }
        else {
            System.out.println("ready to serve requests :-)");
        }
    }

    private static boolean hasCliArgs(String[] args) {
        if (args.length == 0) return false;
        return CLI_KEYWORDS.contains(args[0]) || args[0].contains(":");
    }

    @Override
    @NullMarked
    public void run(String... args) {
        if (hasCliArgs(args)) {
            new CommandLine(mainCommand, factory)
                .setExecutionExceptionHandler(customExceptionHandler)
                .execute(args);
        }
    }
}
