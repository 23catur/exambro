package id.sch.smkn2cikbar.exambrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LockDevice extends AppCompatActivity {

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_device);


    // Retrieve Device Policy Manager so that we can check whether we can
// lock to screen later
    mAdminComponentName = new ComponentName(this,AppAdminReceiver.class);
    mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if(mDevicePolicyManager.isDeviceOwnerApp(getPackageName())){
                  // App is whitelisted
                  setDefaultCosuPolicies(true);
              }
            else {
                  // did you provision the app using <adb shell dpm set-device-owner ...> ?
              }
        }
    }

    public void onStart() {
// Consider locking your app here or by some other mechanism
// Active Manager is supported on Android M
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mDevicePolicyManager.isLockTaskPermitted(this.getPackageName())) {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                        setDefaultCosuPolicies(true);
                        startLockTask();
                    }
                }
            }
        }
    }


    private void unlockApp(){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    stopLockTask();
                }
            }
        }
        setDefaultCosuPolicies(false);

        }

private void setDefaultCosuPolicies(boolean active){

        // Set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // Disable keyguard and status bar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);
    }

    // Enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // Set system update policy
        if (active){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,null);
            }
        }

        // set this Activity as a lock task package
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,active ? new String[]{getPackageName()} : new String[]{});
    }

    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
        // set Cosu activity as home intent receiver so that it is started
        // on reboot
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.addPersistentPreferredActivity(mAdminComponentName, intentFilter, new ComponentName(getPackageName(), MainActivity.class.getName()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, getPackageName());
            }
        }
        }

private void setUserRestriction(String restriction, boolean disallow){
        if (disallow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.addUserRestriction(mAdminComponentName,restriction);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.clearUserRestriction(mAdminComponentName,restriction);
            }
        }
        }

private void enableStayOnWhilePluggedIn(boolean enabled){
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.setGlobalSetting(mAdminComponentName,Settings.Global.STAY_ON_WHILE_PLUGGED_IN,Integer.toString(BatteryManager.BATTERY_PLUGGED_AC| BatteryManager.BATTERY_PLUGGED_USB| BatteryManager.BATTERY_PLUGGED_WIRELESS));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDevicePolicyManager.setGlobalSetting(mAdminComponentName, Settings.Global.STAY_ON_WHILE_PLUGGED_IN,"0");
            }
        }
        }
        }
