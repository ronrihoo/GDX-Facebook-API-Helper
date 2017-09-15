// add package here

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import de.tomgrill.gdxfacebook.core.*;

public class FacebookApi {

    private final static String CLASS_NAME = FacebookApi.class.getSimpleName();
    private final static String ERR_IN = "Error: in " + CLASS_NAME;
    private final static String EXC_IN = "Exception: in " + CLASS_NAME;

    private final static boolean VERBOSE_DEFAULT = true;
    private final static boolean DEBUG_DEFAULT = true;

    private boolean verbose;
    private boolean debug;

    private Array<String> permissions;

    private GDXFacebookConfig config;
    private GDXFacebook facebook;
    private GDXFacebookAccessToken token;

    /* if there's a default class where credentials are stored or retrieved from,
     * then a constructor like this one could be used
     */ /*
    public FacebookApi() {
        this(Credentials.FB_API_ID);
    }
    /**/

    public FacebookApi(String appId) {
        this(appId, null, null);
    }

    /**
     *
     * @param appId The Facebook API ID is required.
     * @param prefFilename Optional (example: ".facebookSessionData").
     * @param apiVersion Optional. Graph API Version (default: v2.6).
     */
    public FacebookApi(String appId, String prefFilename, String apiVersion) {
        init(appId, prefFilename, apiVersion, DEBUG_DEFAULT);
    }

    private void init(String appId, String prefFilename,
                      String apiVersion, boolean debug) {
        setVerbose(VERBOSE_DEFAULT);
        setDebug(debug);
        if (appId != null) {
            setAppId(appId);
        } else {
            try {
                throw new RuntimeException(EXC_IN +
                        ".init(...): appId is null. Has it been passed correctly?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (prefFilename != null)
            setPrefFilename(prefFilename);
        if (apiVersion != null)
            setGraphApiVersion(apiVersion);
        initPermissionsArray();
        installGdxFacebookInstance();
        setGdxFacebookInstance();
        addBasicPermissions();
    }

    private void setGdxDebug(boolean debug) {
        debugLog("setGdxDebug(boolean debug)");
        if (debug)
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // TODO: turn it off when debug == false
    }

    private void instantiateConfig() {
        debugLog("instantiateConfig()");
        config = new GDXFacebookConfig();
    }

    private boolean verifyConfig() {
        debugLog("verifyConfig()");
        if (config == null) {
            try {
                instantiateConfig();
            }
            catch (Exception e) {
                System.out.println(EXC_IN + ".verifyConfig(): \n" + e);
                return false;
            }
        }
        return true;
    }

    private void setAppId(String appId) {
        debugLog("setAppId(String appId)");
        if (verifyConfig())
            config.APP_ID = appId;
    }

    private void setPrefFilename(String filename) {
        debugLog("setPrefFilename(String filename)");
        if (verifyConfig())
            config.PREF_FILENAME = filename;
    }

    private void setGraphApiVersion(String version) {
        debugLog("setGraphApiVersion(String version)");
        if (verifyConfig())
            config.GRAPH_API_VERSION = version;
    }

    private void installGdxFacebookInstance() {
        debugLog("installGdxFacebookInstance()");
        if (verifyConfig())
            facebook = GDXFacebookSystem.install(config);
    }

    /**
     * No parameters, as it is handled by GDXFacebookSystem
     */
    public void setGdxFacebookInstance() {
        debugLog("setGdxFacebookInstance()");
        if (facebook != null)
            facebook = GDXFacebookSystem.getFacebook();
    }

    /**
     *
     * @return GDXFacebook object.
     */
    public GDXFacebook getGdxFacebookInstance() {
        debugLog("getGdxFacebookInstance()");
        return facebook;
    }

    private void initPermissionsArray() {
        debugLog("initPermissionsArray()");
        permissions = new Array<String>();
    }

    private void addBasicPermissions() {
        debugLog("addBasicPermissions()");
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_friends");
    }

    public void addPermission(String permission) {
        debugLog("addPermission(String)");
        if (permissions != null)
            permissions.add(permission);
        else
            System.out.println(ERR_IN + ": permissions array is null");
    }

    /**
     * Basic sign-in with read-mode
     */
    public void signIn() {
        debugLog("signIn()");
        signIn(SignInMode.READ);
    }

    public void signIn(SignInMode signInMode) {
        debugLog("signIn(SignInMode)");
        if (facebook != null) {
            if (permissions == null) {
                addBasicPermissions();
                debug(CLASS_NAME +
                        ": returned to signIn(SignInMode) from addBasicPermissions()");
            }
            facebook.signIn(signInMode, permissions, new GDXFacebookCallback<SignInResult>() {

                @Override
                public void onSuccess(SignInResult result) {
                    if (verbose)
                        System.out.println("Successfully signed in Facebook.");
                    token = result.getAccessToken();
                    debug(CLASS_NAME + ".signIn() succeeded.");
                    debug(CLASS_NAME + ": accessToken is " + token);
                }

                @Override
                public void onError(GDXFacebookError error) {
                    if (debug)
                        System.out.println(ERR_IN + ".signIn(): " +
                                error.getErrorMessage());
                    debug("signIn() invoked by GDXFacebook instance, facebook, in " +
                            CLASS_NAME + " faced an error.");
                }

                @Override
                public void onCancel() {
                    // When the user cancels the login process
                    debug("signIn() invoked by GDXFacebook instance, facebook, in " +
                            CLASS_NAME + " canceled.");
                }

                @Override
                public void onFail(Throwable t) {
                    // When the login fails
                    debug("signIn() invoked by GDXFacebook instance, facebook, in " +
                            CLASS_NAME + " failed.");
                }
            });
        } else {
            debug(ERR_IN + ".signIn(): " +
                    "GDXFacebook instance, facebook, is null.");
        }
    }

    public void signOut() {
        debug(CLASS_NAME + ".signOut()");
        facebook.signOut();
    }

    protected void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    protected void setDebug(boolean debug) {
        this.debug = debug;
        setGdxDebug(debug);
    }

    private void debug(String msg) {
        if (debug)
            System.out.println(msg);
    }

    private void debugLog(String methodName) {
        if (verbose)
            debug(CLASS_NAME + "." + methodName);
    }

}
