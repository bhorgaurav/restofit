package edu.csulb.android.restofit.views.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.validation.Validator;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.databinding.ActivityAddReviewBinding;
import edu.csulb.android.restofit.pojos.Review;
import edu.csulb.android.restofit.viewmodels.AddReviewViewModel;

public class AddReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 901;
    private AddReviewViewModel model;
    private Validator validator;
    private Uri outputFileUri;
    private ActivityAddReviewBinding binding;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_review);
        model = new AddReviewViewModel(getApplicationContext(), new Review("A", "B", "C", 2));
        binding.setModel(model);
        validator = new Validator(binding);

        binding.buttonSelectPhoto.setOnClickListener(this);
        binding.buttonCancel.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);

        askPermissions();
    }

    private void askPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "The app needs access to your files. Please update from settings.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_select_photo:
                openImageIntent();
                break;
            case R.id.button_cancel:
                finish();
                break;
            case R.id.button_submit:
                if (validator.validate()) {
                    model.saveToDatabase(bitmap);
                    finish();
                }
                break;
        }
    }

    /**
     * Credits http://stackoverflow.com/a/12347567/2058134
     */
    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "restofit" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data.getData() != null) {
                    // Image arrived from the gallery.
                    outputFileUri = data.getData();
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                    binding.imageViewReviewPhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}