package com.malakaton.duckhunt.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.malakaton.duckhunt.R;
import com.malakaton.duckhunt.models.User;
import com.malakaton.duckhunt.common.Constants;

public class LoginActivity extends AppCompatActivity {
    EditText etNick;
    Button btnStart;
    String nick;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instanciar conexion database
        db = FirebaseFirestore.getInstance();

        etNick = findViewById(R.id.editTextNick);
        btnStart = findViewById(R.id.buttonStart);

        // Cambiar tipo de fuente
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        etNick.setTypeface(typeface);
        btnStart.setTypeface(typeface);

        // Eventos: evento click
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = etNick.getText().toString();

                if (nick.isEmpty()) {
                    etNick.setError("El nombre de usuario es obligatorio");
                } else {
                    /**
                     * Pasamos de un activity a otro, es decir, pasaremos del login a la pantalla
                     * del juego, el objeto intent recibe como primer parametro el activity
                     * en el que estamos y el segundo parametro el activity que queremos ir
                     *
                     * Para lanzar el cambio de activity hay que llamar al metodo startActivity
                     *
                     */
                    addNickAndStart();
                }
            }
        });
    }

    private void addNickAndStart() {
        // Verificamos que el nick no existe en la bbddd
        db.collection("users").whereEqualTo("nick", nick)
                .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.size() > 0) {
                                etNick.setText("El nick no esta disponible");
                            } else {
                                // Si no existe el nick, guardamos el nick de usuario en db con 0 patos cazados si no existe en bbdd
                                addNickToFireStore();
                            }
                        }
                    });
    }

    private void addNickToFireStore() {
        db.collection("users")
                .add(new User(nick, 0))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            etNick.setText("");
                            Intent i = new Intent(LoginActivity.this, GameActivity.class);
                            // Pasar parametros de un activity a otro (pantalla)
                            i.putExtra(Constants.EXTRA_NICK, nick);
                            i.putExtra(Constants.EXTRA_ID, documentReference.getId());
                            startActivity(i);
                        }
                    });
    }
}
