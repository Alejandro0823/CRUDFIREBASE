package com.example.crudfirebase;


import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.crudfirebase.model.Note;

import java.util.Map;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";

    TextView edtTitle;
    TextView edtContent;
    Button btAdd;

    private FirebaseFirestore firestoreDB;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btAdd = findViewById(R.id.btAdd);

        firestoreDB = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("UpdateNoteId");

            edtTitle.setText(bundle.getString("UpdateNoteTitle"));
            edtContent.setText(bundle.getString("UpdateNoteContent"));
        }

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();

                if (title.length() > 0) {
                    if (id.length() > 0) {
                        updateNote(id, title, content);
                    } else {
                        addNote(title, content);
                    }
                }

                finish();
            }
        });
    }

    private void updateNote(String id, String title, String content) {
        Map<String, Object> note = (new Note(id, title, content)).toMap();

        firestoreDB.collection("notes")
                .document(id)
                .set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Nota actualizada exitosamente!");
                        Toast.makeText(getApplicationContext(), "La nota ha sido actualizada!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No se pudo agregar la nota!", e);
                        Toast.makeText(getApplicationContext(), "Error al actualizar!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNote(String title, String content) {
        Map<String, Object> note = new Note(title, content).toMap();

        firestoreDB.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Nota Agregada!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error", e);
                        Toast.makeText(getApplicationContext(), "No se pudo agregar la nota!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}