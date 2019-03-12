package cordova.plugin.nativeintunesdkwrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.client.app.startup.ADALConnectionDetails;
import com.microsoft.intune.mam.client.identity.MAMPolicyManager;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback;
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotificationType;

/**
 * This class echoes a string called from JavaScript.
 */

public class NativeIntunesdkWrapper extends CordovaPlugin {

    private static final String SHARED_PREFERENCES = "com.eygsl.cbs.eydialin";
    private static final String SP_RESOURCE_ID = "resourceId";
    private static final String SP_AAD_ID = "aadId";
    private static final String SP_UPN = "upn";
    private static final String SP_SHOULD_UPDATE_TOKEN = "updateToken";
    MAMEnrollmentManager mgr;
    Context context;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equalsIgnoreCase("initializeIntuneSDK")) {
            this.initializeInTuneSDK(args.get(0).toString(), args.get(1).toString(), args.get(2).toString(), args.get(3).toString());
            return true;
        }
        context = this.cordova.getContext();
        return false;
    }

    /**
     * Method to initialize InTune SDK
     *
     * @param emailStr
     * @param clientId
     * @param tenantId
     * @param authToken
     */
    public void initializeInTuneSDK(String emailStr, String clientId, String tenantId, String authToken) {
        try {
            context = this.cordova.getContext();
            mgr = MAMComponents.get(MAMEnrollmentManager.class);
            mgr.registerAuthenticationCallback((final String upn, final String aadId, final String resourceId) -> authToken);
            mgr.registerAccountForMAM(emailStr, clientId, tenantId);

            MAMComponents.get(MAMNotificationReceiverRegistry.class).registerReceiver(notification -> {
                if (notification instanceof MAMEnrollmentNotification) {
                    MAMEnrollmentManager.Result result = ((MAMEnrollmentNotification) notification).getEnrollmentResult();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            if (result.name().equalsIgnoreCase("ENROLLMENT_SUCCEEDED")) {
                                showEnrollmentSuccessAlert(context);
                            }
                        }
                    });
                } else {
                    Log.d("Enrollment Receiver", "Unexpected notification type received");
                }
                return true;
            }, MAMNotificationType.MAM_ENROLLMENT_RESULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show authentication succeeded alert
     *
     * @param context
     */
    public void showEnrollmentSuccessAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your organization is now protecting its data in this app.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}


 
