package br.edu.trabalho_interdisciplinar_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import br.edu.trabalho_interdisciplinar_iot.model.Sensor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarWebsocket();
    }

    private void inicializarWebsocket() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ClientManager client = ClientManager.createClient();
                try {
                    client.connectToServer(new ClimaEndPoint(), new URI("ws://192.168.2.106:1880/ws/clima"));
                } catch (DeploymentException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ClimaEndPoint extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            final Gson gson = new Gson();
            final TextView campoTemperatura = (TextView) findViewById(R.id.activity_main_campo_temperatura);
            final TextView campoUmidade = (TextView) findViewById(R.id.activity_main_campo_umidade);

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    final Sensor sensor = gson.fromJson(message, Sensor.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            campoTemperatura.setText(sensor.getTemperatura() + "ºC");
                            campoUmidade.setText(sensor.getUmidade() + "%");
                        }
                    });
                }
            });
        }
    }
}