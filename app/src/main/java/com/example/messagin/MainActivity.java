package com.example.messagin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {
    private final String CHANNEL_ID = "hola";
    TextView titulo, descripcion;
    Button enviar;
    private TextView notifyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titulo = findViewById(R.id.idTitulo);
        descripcion = findViewById(R.id.idDecripcion);
        enviar = findViewById(R.id.btnEnviar);
        //Notificacion por tema de nombre Tutorial
        //esta notificacion es solo para dispositivos que se han registrado al tema
        FirebaseMessaging.getInstance().subscribeToTopic("tutorial");
        mostrarToken();
        notifyTextView = findViewById(R.id.txtnotify);
        InfNotify();
    }
    private void InfNotify(){
        if(getIntent().getExtras()!=null){
            notifyTextView.setText("");
            for(String key: getIntent().getExtras().keySet()){
                String value= getIntent().getExtras().getString(key);
                notifyTextView.append("\n" + key +": "+value);
            }
        }
    }
    public void mostrarToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String Token = instanceIdResult.getToken();
                Toast.makeText(MainActivity.this, "Tu token actual: "+Token, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void NotificarLocal(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //notificacion conf
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("titulo : " +titulo.getText())
                .setContentText("descripcion : " +descripcion.getText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(123, builder.build());
        }

    }
}