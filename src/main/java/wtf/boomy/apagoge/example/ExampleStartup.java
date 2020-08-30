package wtf.boomy.apagoge.example;

import wtf.boomy.apagoge.ApagogeHandler;

import java.io.File;
import java.net.URISyntaxException;

/**
 * An example implementation of an Apagoge update checker
 */
public class ExampleStartup {
    
    public static void main(String[] args) throws URISyntaxException {
        // Get the current running jar file
        File runningFile = new File(ExampleStartup.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        
        // Construct a new handler
        ApagogeHandler handler = new ApagogeHandler(runningFile, "ApagogeExample", "1.0");
        
        // Add this class as part of the validation
        handler.addValidatorClasses(ExampleStartup.class);
        
        handler.addCompletionListener((apagogeHandler, success) -> {
            System.out.println("----------------- Build -----------------");
            System.out.println("Detected build: " + (apagogeHandler.getBuildType() == -1 ? "Unverified" : handler.getBuildType() == 0 ? "Unknown" : "Verified"));
            System.out.println("----------------- Checks -----------------");
    
            if (apagogeHandler.getUpdater() != null) {
                System.out.println("Checks: " + apagogeHandler.getUpdater().getPassedChecks() + " out of " + handler.getUpdater().getChecksRan() + " passed.");
            } else {
                System.out.println("Checks: Unable to find");
            }
    
            System.out.println("----------------- Data -----------------");
            System.out.println("Is update available? " + (apagogeHandler.isUpdateAvailable() ? "Yes" : "No"));
            System.out.println("Is this program a pre-release? " + (apagogeHandler.isRunningNewerVersion() ? "Yes" : "No"));
            System.out.println("Verified Signature: " + (apagogeHandler.getVerifiedSignature() == null ? "None" : handler.getVerifiedSignature()));
    
            apagogeHandler.requestKill();
        });
        
        // Starts the validation check
        handler.begin();
    }
}
