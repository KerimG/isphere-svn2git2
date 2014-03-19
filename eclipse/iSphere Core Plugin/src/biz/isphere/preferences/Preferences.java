package biz.isphere.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.ISpherePlugin;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.preferencepages.IPreferences;

/**
 * Class to manage access to the preferences of the plugin.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences {

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the LPEX Task-Tags plugin.
     */
    private IPreferenceStore preferenceStore;

    /**
     * Preferences keys:
     */
    
    private static final String DOMAIN = ISpherePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String SPOOLED_FILES_SAVE_DIRECTORY = DOMAIN + "SPOOLED_FILES.SAVE.DIRECTORY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_DEFAULT_FORMAT = DOMAIN + "SPOOLED_FILES.DEFAULT_FORMAT"; //$NON-NLS-1$

    private static final String SOURCEFILESEARCH_SEARCHSTRING = DOMAIN + "SOURCEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String MESSAGEFILESEARCH_SEARCHSTRING = DOMAIN + "MESSAGEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String ISPHERE_LIBRARY = DOMAIN + "LIBRARY"; //$NON-NLS-1$

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISpherePlugin.getDefault().getPreferenceStore();
        }
        return instance;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }

    /*
     * Preferences: GETTER
     */

    public String getISphereLibrary() {
        return preferenceStore.getString(ISPHERE_LIBRARY);
    }

    public String getMessageFileSearchString() {
        return preferenceStore.getString(MESSAGEFILESEARCH_SEARCHSTRING);
    }

    public String getSourceFileSearchString() {
        return preferenceStore.getString(SOURCEFILESEARCH_SEARCHSTRING);
    }
    
    public String getSpooledFileConversionDefaultFormat() {
        return preferenceStore.getString(SPOOLED_FILES_DEFAULT_FORMAT);
    }
    
    public String getSpooledFileConversionText() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT);
    }
    
    public String getSpooledFileConversionTextLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY);
    }
    
    public String getSpooledFileConversionTextCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT_COMMAND);
    }
    
    public String getSpooledFileConversionHTML() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML);
    }
    
    public String getSpooledFileConversionHTMLLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML_LIBRARY);
    }
    
    public String getSpooledFileConversionHTMLCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML_COMMAND);
    }
    
    public String getSpooledFileConversionPDF() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF);
    }
    
    public String getSpooledFileConversionPDFLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF_LIBRARY);
    }
    
    public String getSpooledFileConversionPDFCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF_COMMAND);
    }
    
    public String getSpooledFileSaveDirectory() {
        String directory = preferenceStore.getString(SPOOLED_FILES_SAVE_DIRECTORY);
        if (StringHelper.isNullOrEmpty(directory)) {
            return "C:\\"; //$NON-NLS-1$
        }
        return directory;
    }

    /*
     * Preferences: SETTER
     */

    public void setISphereLibrary(String aLibrary) {
        preferenceStore.setValue(ISPHERE_LIBRARY, aLibrary.trim());
    }

    public void setMessageFileSearchString(String aSearchString) {
        preferenceStore.setValue(MESSAGEFILESEARCH_SEARCHSTRING, aSearchString.trim());
    }

    public void setSourceFileSearchString(String aSearchString) {
        preferenceStore.setValue(SOURCEFILESEARCH_SEARCHSTRING, aSearchString.trim());
    }
    
    public void setSpooledFileDefaultFormat(String aFormat) {
        preferenceStore.setValue(SPOOLED_FILES_DEFAULT_FORMAT, aFormat);
    }
    
    public void setSpooledFileConversionText(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT, aConversionType);
    }
    
    public void setSpooledFileConversionLibraryText(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY, aLibrary);
    }
    
    public void setSpooledFileConversionCommandText(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT_COMMAND, aCommand);
    }
    
    public void setSpooledFileConversionHTML(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML, aConversionType);
    }
    
    public void setSpooledFileConversionLibraryHTML(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML_LIBRARY, aLibrary);
    }
    
    public void setSpooledFileConversionCommandHTML(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML_COMMAND, aCommand);
    }
    
    public void setSpooledFileConversionPDF(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF, aConversionType);
    }
    
    public void setSpooledFileConversionLibraryPDF(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF_LIBRARY, aLibrary);
    }
    
    public void setSpooledFileConversionCommandPDF(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF_COMMAND, aCommand);
    }
    
    public void setSpooledFileSaveDirectory(String aDirectory) {
        preferenceStore.setValue(SPOOLED_FILES_SAVE_DIRECTORY, aDirectory);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {
        preferenceStore.setDefault(ISPHERE_LIBRARY, getDefaultISphereLibrary());
        
        preferenceStore.setDefault(SPOOLED_FILES_DEFAULT_FORMAT, getDefaultSpooledFileConversionDefaultFormat());
        
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT, getDefaultSpooledFileConversionText());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_COMMAND, getDefaultSpooledFileConversionTextCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY, getDefaultSpooledFileConversionTextLibrary());
        
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML, getDefaultSpooledFileConversionHTML());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_COMMAND, getDefaultSpooledFileConversionHTMLCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_LIBRARY, getDefaultSpooledFileConversionHTMLLibrary());
        
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF, getDefaultSpooledFileConversionPDF());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_COMMAND, getDefaultSpooledFileConversionPDFCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_LIBRARY, getDefaultSpooledFileConversionPDFLibrary());
    }

    /*
     * Preferences: Default Values
     */

    public String getDefaultISphereLibrary() {
        return "ISPHERE";
    }
    
    /**
     * Returns the default format for spooled file conversion on double-click on
     * a spooled file.
     * 
     * @return default format on spooled file double-click
     */
    public String getDefaultSpooledFileConversionDefaultFormat() {
        return IPreferences.OUTPUT_FORMAT_TEXT;
    }
    
    public String getDefaultSpooledFileConversionText() {
        return IPreferences.SPLF_CONVERSION_DEFAULT;
    }
    
    public String getDefaultSpooledFileConversionTextLibrary() {
        return "";
    }
    
    public String getDefaultSpooledFileConversionTextCommand() {
        return "";
    }
    
    public String getDefaultSpooledFileConversionHTML() {
        return IPreferences.SPLF_CONVERSION_DEFAULT;
    }
    
    public String getDefaultSpooledFileConversionHTMLLibrary() {
        return "";
    }
    
    public String getDefaultSpooledFileConversionHTMLCommand() {
        return "";
    }
    
    public String getDefaultSpooledFileConversionPDF() {
        return IPreferences.SPLF_CONVERSION_DEFAULT;
    }
    
    public String getDefaultSpooledFileConversionPDFLibrary() {
        return "";
    }
    
    public String getDefaultSpooledFileConversionPDFCommand() {
        return "";
    }
}