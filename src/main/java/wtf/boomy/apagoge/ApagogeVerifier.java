package wtf.boomy.apagoge;

import com.google.gson.JsonObject;

/**
 * A basic Apagoge verifier, contains specification which all verifiers must follow
 */
public interface ApagogeVerifier {
    
    /**
     * Returns the build-type of this program.
     *
     * @return Returns 1 if this binary has been signed with a valid signature and the file has been verified by the updater
     * Returns 0 if the file has not been verified, but has the correct signature
     * Returns -1 if the file has an incorrect signature.
     */
    public int getBuildType();
    
    /**
     * Returns the SHA-256 hash of this verified build, will return null if the {@link #getBuildType()} is -1;
     *
     * @return the SHA-256 hash of this build, or null.
     */
    public String getVerifiedHash();
    
    /**
     * Returns the data for the newest version if there is one, or null if this version is up to date or {@link #isRunningNewerVersion()} is true
     *
     * @return JSON data for the newest version or null
     */
    public JsonObject getNewestVersion();
    
    /**
     * True if the version being run is newer than the latest version, false if it's older or is the latest release.
     *
     * @return See above
     */
    public boolean isRunningNewerVersion();
    
    /**
     * Adds a callback method, will notify once the verifier is done
     *
     * @param listener the listener to verify, see {@link CompletionListener}
     */
    public void addCompletionListener(CompletionListener listener);
    
    /**
     * Adds a list of classes to validate against. Depending on the implementation this may do nothing.
     *
     * @param classArray an array of classes to check for validation
     */
    public void addValidatorClasses(Class<?>[] classArray);
    
    /**
     * Invalidates all JSON cached data, depending on the implementation this may be handled differently
     */
    public void resetUpdateCache();
    
    /**
     * Tells the Apagoge updater to run, results may be cached from the first time this is ran and can be wiped with {@link #resetUpdateCache()}
     */
    public void run();
    
    /**
     * Terminates this updater, once this is called the following will commence
     *
     * - Build type will be -1
     * - Verified hash will be removed
     * - Running new version will be set to false
     * - New version JSON data will be erased
     * - Run will no longer work
     * - Validator classes will be cleared
     *
     * This method effectively ends the Updater until the process is restarted or a new Updater is created.
     */
    public void kill();
    
    public default int getChecksRan() { return 0; }
    
    public default int getPassedChecks() { return 0; }
}
