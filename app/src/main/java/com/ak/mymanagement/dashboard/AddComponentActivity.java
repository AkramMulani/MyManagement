package com.ak.mymanagement.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ak.mymanagement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
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

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_component);

        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            findViewById(R.id.qualitySpinner).getWindowInsetsController().hide(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            findViewById(R.id.qualitySpinner).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        drawerLayout = findViewById(R.id.drawer_layout1);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Add New Component");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int index = menuItem.getItemId();

                if (index == R.id.nav_settings) {
                    // settings selected
                }
                else if (index == R.id.nav_about_us) {
                    // about us selected
                }
                else if (index == R.id.nav_logout) {
                    // logout selected
                    auth.signOut();
                    finish();
                }

                // Close the drawer
                drawerLayout.closeDrawers();
                return true;
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        EditText compModel = findViewById(R.id.comp_model);
        EditText compCategory = findViewById(R.id.comp_category);

        String name = compName.getText().toString();
        String quantity = compQuantity.getText().toString();
        String price = compPrice.getText().toString();
        String model = compModel.getText().toString();
        String category = compCategory.getText().toString();

        // If image is selected then upload it to storage
        if (imageUri!=null)
            uploadImageToStorage(imageUri, name);

        refComp.child(name).child("name").setValue(name);
        refComp.child(name).child("quantity").setValue(quantity);
        refComp.child(name).child("price").setValue(price);
        refComp.child(name).child("quality").setValue(selectedQuality);
        refComp.child(name).child("updatedBy").setValue(user.getUid());
        refComp.child(name).child("model").setValue(model);
        refComp.child(name).child("category").setValue(category);

        // Check if the category already exists in the database

        db.getReference("Categories").orderByValue().equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Category does not exist, add it to the database
                    db.getReference("Categories").push().setValue(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error

            }
        });

        finish();
    }
}
