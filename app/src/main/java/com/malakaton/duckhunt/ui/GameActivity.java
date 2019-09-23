package com.malakaton.duckhunt.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.malakaton.duckhunt.R;
import com.malakaton.duckhunt.common.Constants;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    TextView tvCounterDucks, tvTimer, tvNick;
    ImageView ivDuck;
    int counterDuck = 0;
    int widthScreen;
    int heightScreen;
    Random randomNumber;
    boolean gameOver = false;
    String id, nick;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = FirebaseFirestore.getInstance();
        
        initViewComponents();
        events();
        initScreen();
        moveDuck();
        initCountDown();
    }

    private void initViewComponents() {
        tvCounterDucks = findViewById(R.id.textViewCounter);
        tvTimer = findViewById(R.id.textViewTimer);
        tvNick = findViewById(R.id.textViewNick);
        ivDuck = findViewById(R.id.imageViewDuck);

        // Cambiar tipo de fuente
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        tvCounterDucks.setTypeface(typeface);
        tvTimer.setTypeface(typeface);
        tvNick.setTypeface(typeface);

        // Extras: obtener id/nick y setear en text view
        Bundle extras = getIntent().getExtras();
        nick = extras.getString(Constants.EXTRA_NICK);
        id = extras.getString(Constants.EXTRA_ID);
        tvNick.setText(nick);
    }

    private void events() {
        ivDuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameOver) {
                    counterDuck++;
                    tvCounterDucks.setText(String.valueOf(counterDuck));
                    ivDuck.setImageResource(R.drawable.duck_clicked);

                    // Setea la imagen del pato cazado a pato normal despues de 0,5 segundos y despues
                    // aparece la imagen del pato en otra posicion del layout
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ivDuck.setImageResource(R.drawable.duck);
                            moveDuck();
                        }
                    }, 500);
                }
            }
        });
    }

    private void moveDuck() {
        int min = 0;
        int maxWidth = widthScreen - ivDuck.getWidth();
        int maxHeight = heightScreen - ivDuck.getHeight();

        // Generamos 2 numeros aleatorios, una para la coordenada x y otro para
        // la coordenada y.
        int randomX = randomNumber.nextInt(((maxWidth - min) + 1) + min);
        int randomY = randomNumber.nextInt(((maxHeight - min) + 1) + min);

        // Utilizamos los numeros aleatorios para mover el pato a esa posicion
        ivDuck.setX(randomX);
        ivDuck.setY(randomY);
    }

    private void initScreen() {
        // Conocer el tamaño de la pantalla del dispositivo
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        // Inicializamos el objeto para generar random numbers
        randomNumber = new Random();
    }

    private void initCountDown() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                long restSeconds = millisUntilFinished / 1000;
                tvTimer.setText(restSeconds + "s");
            }

            public void onFinish() {
                tvTimer.setText("0s");
                gameOver = true;
                showGameOverDialog();
                saveResultFireStore();
            }
        }.start();
    }

    private void saveResultFireStore() {
        db.collection("users")
                .document(id)
                    .update(
                            "ducks", counterDuck
                    );
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        builder.setMessage("Has conseguido cazar " + counterDuck + " patos")
                .setTitle("GAME OVER");

        // añadir botones aceptar y cancelar
        builder.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                counterDuck = 0;
                tvCounterDucks.setText("0");
                gameOver = false;
                initCountDown();
                moveDuck();
            }
        });
        builder.setNegativeButton("Ver Ranking", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent i = new Intent(GameActivity.this, RankingActivity.class);
                startActivity(i);
            }
        });

        AlertDialog dialog = builder.create();
        // Mostrar el dialog
        dialog.show();
    }
}
