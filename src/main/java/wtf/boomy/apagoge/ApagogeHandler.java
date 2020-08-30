/*
 *     Copyright (C) 2020 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wtf.boomy.apagoge;

import com.google.gson.JsonObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A simple handler for the Apagoge backend, does nothing until the {@link #begin()} method is called.
 *
 * @author boomboompower
 * @version 1.0
 */
public final class ApagogeHandler {
    
    private ApagogeVerifier updater;
    
    /**
     * Basic constructor for the handler
     *
     * @param programFile the file the program is being run from
     * @param programName the name/identifier used by the program
     * @param version the version of the program being run
     */
    public ApagogeHandler(File programFile, String programName, String version) {
        if (programFile.isDirectory()) {
            this.updater = null;
        } else {
            this.updater = findAndConstruct(programFile, programName, version);
        }
    }
    
    /**
     * Adds a callback class which will be notified once the run method is called
     * if there is no found updater implementation it will immediately be called with a failure.
     *
     * @param listener the listener to detect status changes
     */
    public void addCompletionListener(CompletionListener listener) {
        if (this.updater != null) {
            this.updater.addCompletionListener(listener);
        } else {
            listener.onFinish(this, false);
        }
    }
    
    /**
     * Adds classes to the apagoge backend to be scanned.
     *
     * @param clazz a list of classes
     */
    public void addValidatorClasses(Class<?>... clazz) {
        if (this.updater != null) {
            this.updater.addValidatorClasses(clazz);
        }
    }
    
    /**
     * Tells the updater to run if possible. This is not a guarantee, and can be ignored
     * depending on the implementation of the updater.
     */
    public void begin() {
        if (this.updater != null) {
            this.updater.run();
        }
    }
    
    /**
     * Sets the build to unofficial mode, kills the updater.
     */
    public void wipeUpdaterInstance() {
        if (this.updater != null) {
            this.updater = null;
        }
    }
    
    public void requestKill() {
        if (this.updater != null) {
            this.updater.kill();
            
            this.updater = null;
        }
    }
    
    /**
     * Is the current version of the program newer than the latest version?
     *
     * @return true if the program is newer than the latest version
     */
    public boolean isRunningNewerVersion() {
        return this.updater != null && this.updater.isRunningNewerVersion();
    }
    
    /**
     * True if the data from the updater is not null and the updater exists
     *
     * @return true if an update is available
     */
    public boolean isUpdateAvailable() {
        return this.updater != null && this.updater.getNewestVersion() != null;
    }
    
    /**
     * Returns the data for the newest version if there is one, or null if this version is up to date or {@link #isRunningNewerVersion()} is true
     *
     * @return JSON data for the newest version or null
     */
    public JsonObject getUpdateData() {
        return this.updater != null ? this.updater.getNewestVersion() : null;
    }
    
    /**
     * Returns the build-type of this build.
     *
     * @return Returns 1 if this binary has been signed with a valid signature and the file has been verified by the updater
     * Returns 0 if the file has not been verified, but has the correct signature
     * Returns -1 if the file has an incorrect signature.
     */
    public int getBuildType() {
        return this.updater == null ? -1 : this.updater.getBuildType();
    }
    
    /**
     * Returns the SHA-256 hash of this verified build, will return null if the {@link #getBuildType()} is -1;
     *
     * @return the SHA-256 hash of this build, or null.
     */
    public String getVerifiedSignature() {
        return this.updater == null ? null : this.updater.getVerifiedHash();
    }
    
    /**
     * Returns the stored instance of the updater.
     *
     * @return the stored instance of the updater, could be null.
     */
    public ApagogeVerifier getUpdater() {
        return this.updater;
    }
    
    /**
     * Finds a valid Apagoge implementation and fills it
     *
     * @param programFile the location of the program being run
     * @param programName the name of the program being run
     * @param programVersion the version of the program
     *
     * @return null if an implementation cannot be found
     */
    private ApagogeVerifier findAndConstruct(File programFile, String programName, String programVersion) {
        for (String name : new String[] {"ApagogeUpdaterImpl", "ApagogeUpdater", "a", "b"}) {
            try {
                Class<?> clazz = Class.forName(getClass().getPackage().getName() + ".impl." + name);
                
                Constructor<?> constructor = clazz.getDeclaredConstructor(File.class, String.class, String.class, ApagogeHandler.class);
                
                boolean wasAccessible = constructor.isAccessible();
                
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                
                Object created = constructor.newInstance(programFile, programName, programVersion, this);
                
                if (!wasAccessible) {
                    constructor.setAccessible(false);
                }
                
                if (created instanceof ApagogeVerifier) {
                    return (ApagogeVerifier) created;
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
            }
        }
        
        return null;
    }
}
