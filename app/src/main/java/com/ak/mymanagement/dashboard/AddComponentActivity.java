package com.ak.mymanagement.dashboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ak.mymanagement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddComponentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView selectedImageView;
    private Spinner qualitySpinner;
    private String selectedQuality;
    private String imageUrl;
    private Uri imageUri;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference refComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_component);

        qualitySpinner = findViewById(R.id.qualitySpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.quality_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        qualitySpinner.setAdapter(adapter);

        selectedImageView = findViewById(R.id.selected_comp_image);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        refComp = db.getReference("Components");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Set the selected image to the ImageView
            selectedImageView.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected item from the spinner
        selectedQuality = parent.getItemAtPosition(position).toString();
        // Show a toast message with the selected item
        Toast.makeText(this, "Selected quality: " + selectedQuality, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing if nothing is selected
    }

    public void selectImage(View view) {
        // Open image picker intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Inside your activity class
    private void uploadImageToStorage(Uri imageUri, String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Generate a random unique ID for the image file
        String imageName = UUID.randomUUID().toString();

        // Create a reference to 'images/imageName.jpg'
        StorageReference imageRef = storageRef.child("images/" + imageName + ".jpg");

        // Upload file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully, now get the download URL
                        Task<Uri> downloadUrlTask = imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Save the download URL to the Firebase Realtime Database
                                imageUrl = uri.toString();
                                refComp.child(name).child("image").setValue(imageUrl);
                                Toast.makeText(AddComponentActivity.this, "Image Uploaded to storage", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    public void saveAndContinue(View view) {
        EditText compName = findViewById(R.id.comp_name);
        EditText compQuantity = findViewById(R.id.comp_quantity);
        EditText compPrice = findViewById(R.id.comp_price);

        String name = compName.getText().toString();
        String quantity = compQuantity.getText().toString();
        String price = compPrice.getText().toString();

        // If image is selected then upload it to storage
        if (imageUri!=null)
            uploadImageToStorage(imageUri, name);

        refComp.child(name).child("name").setValue(name);
        refComp.child(name).child("quantity").setValue(quantity);
        refComp.child(name).child("price").setValue(price);
        refComp.child(name).child("quality").setValue(selectedQuality);
        refComp.child(name).child("updatedBy").setValue(user.getUid());

        finish();
    }
}
