package edu.csulb.android.restofit.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Review;

import static android.R.attr.key;

public class AddReviewViewModel extends BaseObservable {

    private Context context;
    private Review review;

    public AddReviewViewModel(Context context, Review review) {
        this.context = context;
        this.review = review;
    }

    public String getTitle() {
        return review.getTitle();
    }

    public String getDescription() {
        return review.getDescription();
    }

    public String getPhotoUrl() {
        return review.getPhotoUrl();
    }

    public TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            review.setTitle(s.toString());
        }
    };

    public TextWatcher descriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            review.setDescription(s.toString());
        }
    };

    public void saveToDatabase(Bitmap bitmap) {

        if (TextUtils.isEmpty(review.getId())) {
            DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference(StaticMembers.CHILD_REVIEWS);
            review.setId(mainRef.push().getKey());
        }

        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 74, baos);
            byte[] bytes = baos.toByteArray();

            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(key + ".jpg");
            UploadTask uploadTask = storageRef.putBytes(bytes, metadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    review.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
                    System.out.println("review.getPhotoUrl(): " + review.getPhotoUrl());
                    FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_REVIEWS).child(review.getId()).setValue(review);
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_REVIEWS).child(review.getId()).setValue(review);
        }
    }
}