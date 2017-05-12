package edu.csulb.android.restofit.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.MeaningCloud;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Review;
import edu.csulb.android.restofit.receivers.AlarmReceiver;
import edu.csulb.android.restofit.tensorflow.Classifier;
import edu.csulb.android.restofit.tensorflow.TensorFlowImageClassifier;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewViewModel extends BaseObservable {

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    private Context context;
    private int restaurantId;
    private Review review;

    public AddReviewViewModel(Context context, int restaurantId, Review review) {
        this.context = context;
        this.restaurantId = restaurantId;
        this.review = review;
        initTensorFlowAndLoadModel();
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

    public void saveToDatabase(final Bitmap bitmap) {

        DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference(StaticMembers.CHILD_REVIEWS);
        review.setId(mainRef.push().getKey());

        review.setLatitude(LocationHelper.getLatitude());
        review.setLongitude(LocationHelper.getLongitude());
        review.setTimestamp(System.currentTimeMillis());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Assume user leaves a review 1 hour after eating
        int currentHour = StaticMembers.getCurrentHour();
        if (currentHour >= 5 && currentHour < 10) {
            // Just had breakfast
            FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_FOOD_TIMES + user.getUid()).
                    child(StaticMembers.CHILD_BREAKFAST).child(review.getId()).setValue(currentHour);
        } else if (currentHour >= 10 && currentHour <= 16) {
            // Just had lunch
            FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_FOOD_TIMES + user.getUid()).
                    child(StaticMembers.CHILD_LUNCH).child(review.getId()).setValue(currentHour);
        } else {
            // Dinner
            FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_FOOD_TIMES + user.getUid()).
                    child(StaticMembers.CHILD_DINNER).child(review.getId()).setValue(currentHour);
        }

        analyzeSentiment(review.getTitle() + " " + review.getDescription(), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(response.body().string());
                        switch (json.getString("score_tag")) {
                            case "P+":
                                review.setRating(5);
                                break;
                            case "P":
                                review.setRating(4);
                                break;
                            case "NEU":
                                review.setRating(3);
                                break;
                            case "N":
                                review.setRating(2);
                                break;
                            case "N+":
                                review.setRating(1);
                                break;
                            case "NONE":
                                review.setRating(0);
                                break;
                        }
                    } else {
                        System.out.println(response.errorBody().string());
                        review.setRating(3);
                    }

                    if (bitmap != null) {
                        analyzeTags(bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 74, baos);
                        byte[] bytes = baos.toByteArray();

                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference(review.getId() + ".jpg");
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
                                FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_REVIEWS + restaurantId).child(review.getId()).setValue(review);
                                System.out.println("setPhotoUrl: " + review.getPhotoUrl());
                            }
                        });
                    } else {
                        FirebaseDatabase.getInstance().getReference().child(StaticMembers.CHILD_REVIEWS + restaurantId).child(review.getId()).setValue(review);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        AlarmReceiver.handleIntent(context);
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            context.getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void analyzeTags(Bitmap bitmap) {
        Bitmap b = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        final List<Classifier.Recognition> results = classifier.recognizeImage(b);
        StringBuilder sb = new StringBuilder();
        for (Classifier.Recognition r : results) {
            sb.append(r.getTitle());
            sb.append(" ");
        }
        review.setPhotoTags(sb.toString());
    }

    public void analyzeSentiment(String text, Callback<ResponseBody> callback) {

        Map<String, String> parameters = new TreeMap<>();
        parameters.put("key", "630094cc07b4c0b37b3a47fcb779eeda");
        parameters.put("lang", "en");
        parameters.put("txt", text);
        parameters.put("model", "general");

        APIClient.getClient(MeaningCloud.URL, 4).create(MeaningCloud.class).analyze(parameters).enqueue(callback);
    }
}