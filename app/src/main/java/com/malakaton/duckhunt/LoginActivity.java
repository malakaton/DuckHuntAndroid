package com.malakaton.duckhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.malakaton.duckhunt.common.Constants;

public class LoginActivity extends AppCompatActivity {
    EditText etNick;
    Button btnStart;
    String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Intent i = new Intent(LoginActivity.this, GameActivity.class);
                    // Pasar parametros de un activity a otro (pantalla)
                    i.putExtra(Constants.EXTRA_NICK, nick);
                    startActivity(i);
                }
            }
        });
    }
}
