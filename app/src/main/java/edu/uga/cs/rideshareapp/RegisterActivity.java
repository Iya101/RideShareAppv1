package edu.uga.cs.rideshareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "RegisterActivity";

    private EditText emailEditText;
    private EditText passworEditText;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById( R.id.editText );
        passworEditText = findViewById( R.id.editText5 );

        Button registerButton = findViewById(R.id.button3);
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String email = emailEditText.getText().toString().trim();
            final String password = passworEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Registration successful
                            Log.d(DEBUG_TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Registered user: " + email, Toast.LENGTH_SHORT).show();

                            // Get the current user
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Generate random IDs
                            int randomUserId = generateRandomId();
                            int randomDriverId = generateRandomId();

                            // Store the IDs in Firebase under the user's profile
                            if (user != null) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("userId", randomUserId);
                                updates.put("driverId", randomDriverId);


                                databaseReference.updateChildren(updates).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(DEBUG_TAG, "User and driver IDs added to database successfully.");
                                        Intent intent = new Intent(RegisterActivity.this, RideShareAppManagementActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(DEBUG_TAG, "Failed to add user and driver IDs", task1.getException());
                                        Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            // Registration failed
                            Log.w(DEBUG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private int generateRandomId() {
        return (int) (Math.random() * 100000);  // Generate a random number up to 100000
    }

}
