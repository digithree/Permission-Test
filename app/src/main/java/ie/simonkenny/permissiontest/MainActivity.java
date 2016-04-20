package ie.simonkenny.permissiontest;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 132;  // this number is arbitrary

    private final String PHONE_NUMBER = "00000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button callButton = (Button) findViewById(R.id.call_button);
        if (callButton != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callAction();
                }
            });
        }
    }

    private void callAction() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    final Activity finalActivity = this;
                    new AlertDialog.Builder(this)
                            .setTitle("Important")
                            .setMessage("You need to enable phone call permission for this app.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(finalActivity,
                                            new String[]{Manifest.permission.CALL_PHONE},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            })
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            }
        }

        //call support number using ACTION_CALL intent
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
        try {
            startActivity(callIntent);
        } catch (SecurityException ex) {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callAction();
                } else { // they have refused the permission and opted to not be asked again
                    new AlertDialog.Builder(this)
                            .setTitle("Important")
                            .setMessage("Permission not granted. If you have previously checked \"Never ask again\" then you must add this permission manually in the settings app.")
                            .setPositiveButton("Ok", null)
                            .create()
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
