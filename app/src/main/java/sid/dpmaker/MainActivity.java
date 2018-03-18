package sid.dpmaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImage.ActivityResult;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {

    private Drawable chosenFore = null;
    private Drawable drawableForeBlack;
    private Drawable drawableForeBlackNeon;
    private Drawable drawableForeBlueNeon;
    private Drawable drawableForeGreenNeon;
    private Drawable drawableForePinkNeon;
    private Drawable drawableForewhiteNeon;
    private Button image_picker_button;
    private Uri mCropImageUri;
    private ImageView mCropImageView;
    private ProgressBar progressbar;
    private Bitmap resultBitmap = null;
    private Button save_image_button;
    private Bitmap scaledBitmapBack;
    private Spinner spinner_dp_type;
    private TextView warn_message;

    class DpSelectorListener implements OnItemSelectedListener {
        DpSelectorListener() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String choosen_dp = parent.getItemAtPosition(position).toString();
            Log.d("choosen_dp", choosen_dp);
            if (choosen_dp.compareTo("Black") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForeBlack;
            } else if (choosen_dp.compareTo("Black Neon") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForeBlackNeon;
            } else if (choosen_dp.compareTo("Light Green Neon") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForeGreenNeon;
            } else if (choosen_dp.compareTo("Pink Neon") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForePinkNeon;
            } else if (choosen_dp.compareTo("Sky Blue Neon") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForeBlueNeon;
            } else if (choosen_dp.compareTo("White Neon") == 0) {
                MainActivity.this.chosenFore = MainActivity.this.drawableForewhiteNeon;
            }
            if (MainActivity.this.resultBitmap != null) {
                MainActivity.this.updateFrame();
            } else {
                MainActivity.this.mCropImageView.setImageDrawable(MainActivity.this.chosenFore);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    class C02102 implements OnClickListener {
        C02102() {
        }

        public void onClick(View view) {
            CropImage.startPickImageActivity(MainActivity.this);
        }
    }

    class C02113 implements OnClickListener {
        C02113() {
        }

        public void onClick(View view) {
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_main), (CharSequence) "Saving... ", -2).show();
            MainActivity.this.saveIQDp();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
        this.image_picker_button = (Button) findViewById(R.id.pick_image_button);
        this.mCropImageView = (ImageView) findViewById(R.id.cropped_image);
        this.drawableForeBlack = getResources().getDrawable(R.drawable.black);
        this.drawableForeBlueNeon = getResources().getDrawable(R.drawable.sky_blue_neon);
        this.drawableForeBlackNeon = getResources().getDrawable(R.drawable.black_neon);
        this.drawableForeGreenNeon = getResources().getDrawable(R.drawable.light_green_neon);
        this.drawableForePinkNeon = getResources().getDrawable(R.drawable.pink_neon);
        this.drawableForewhiteNeon = getResources().getDrawable(R.drawable.white_background_neon);
        this.spinner_dp_type = (Spinner) findViewById(R.id.spinner);
        this.warn_message = (TextView) findViewById(R.id.warn_message);
        ArrayAdapter<CharSequence> os_adapter = ArrayAdapter.createFromResource(this, R.array.dp_types, R.layout.support_simple_spinner_dropdown_item);
        os_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        this.spinner_dp_type.setAdapter(os_adapter);
        this.spinner_dp_type.setOnItemSelectedListener(new DpSelectorListener());
        this.chosenFore = this.drawableForeBlack;
        this.mCropImageView.setImageDrawable(this.chosenFore);
        this.image_picker_button.setOnClickListener(new C02102());
        this.save_image_button = (Button) findViewById(R.id.save_image_button);
        this.save_image_button.setOnClickListener(new C02113());
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void updateFrame() {
        this.resultBitmap = overlay(((BitmapDrawable) this.chosenFore).getBitmap(), this.scaledBitmapBack);
        this.mCropImageView.setImageBitmap(this.resultBitmap);
    }

    @SuppressLint({"NewApi"})
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == -1) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                this.mCropImageUri = imageUri;
                requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                Uri resultUri = result.getUri();
                Bitmap bitmapFore = ((BitmapDrawable) this.chosenFore).getBitmap();
                Bitmap bitmapBack = null;
                try {
                    bitmapBack = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    Log.d("bitmap_creation", "unable to add bitmap");
                    e.printStackTrace();
                }
                int xScale = bitmapFore.getWidth() - ((bitmapFore.getWidth() * 35) / 100);
                Log.d("scale_value", String.valueOf(xScale));
                this.scaledBitmapBack = Bitmap.createScaledBitmap(bitmapBack, xScale, xScale, false);
                this.resultBitmap = overlay(bitmapFore, this.scaledBitmapBack);
                this.mCropImageView.setImageBitmap(this.resultBitmap);
                this.image_picker_button.setVisibility(View.GONE);
                this.save_image_button.setVisibility(View.VISIBLE);
                this.warn_message.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError();
            }
        }
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        try {
            Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp2, (float) ((bmp1.getWidth() - bmp2.getWidth()) / 2), (float) (((bmp1.getHeight() - bmp2.getHeight()) / 2) + 30), null);
            canvas.drawBitmap(bmp1, new Matrix(), null);
            return bmOverlay;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            return;
        }
        if (this.mCropImageUri == null || grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_SHORT).show();
        } else {
            startCropImageActivity(this.mCropImageUri);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setMinCropResultSize(800, 800).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true).start(this);
    }


    private void saveIQDp() {
        Exception e;
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/IQDP");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File mypath = new File(dir, "iqdppic_" + Long.valueOf(System.currentTimeMillis() / 1000).toString() + ".png");
        try {
            mypath.createNewFile();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Log.d("savepath", mypath.toString());
        try {
            FileOutputStream fos = new FileOutputStream(mypath);
            try {
                this.resultBitmap.compress(CompressFormat.PNG, 100, fos);
                fos.close();
                MediaScannerConnection.scanFile(this,
                        new String[] { mypath.toString() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
                Log.d("image_save", "image saved");
                snackBarshowMessage(mypath.toString());

            } catch (Exception e3) {
                e = e3;
                Log.e("SAVE_IMAGE", e.getMessage(), e);
            }
        } catch (Exception e4) {
            e = e4;
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
    }

    private void snackBarshowMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_main), "Saved at " + message, -2).show();
    }
}
