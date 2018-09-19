package autoride.me.websocket_01;

import android.app.Activity;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    //192.168.0.119

    private Socket socket = null;
    private Activity activity = MainActivity.this;

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "onConnectError: " + args);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (activity != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("TAG", "onDisconnect: " + args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "onConnect: " + args);
        }
    };
    String messageShow;
    private Emitter.Listener newMessageListner = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "newMessageListner: " + args);
        }
    };



    TextInputEditText editName, editMessage;
    Button btnSendMessage;
    TextView textMessageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            socket = IO.socket("http://192.168.0.119:4000");
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on("mobile", newMessageListner);
            socket.connect();    //Connect socket to server
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        editName = (TextInputEditText) findViewById(R.id.eidtName);
        editMessage = (TextInputEditText) findViewById(R.id.editMessage);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        textMessageBody = (TextView) findViewById(R.id.textMessageBody);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public void sendMessage(View v) {
        if (socket.connected()) {
            String handle = editName.getText().toString();
            String message = editMessage.getText().toString();
            ChatMan chatMan = new ChatMan(handle, message);
            Gson gson = new Gson();
            String json = gson.toJson(chatMan);
            socket.emit("mobile", json);

        }
    }

    private void setListening() {
        if (socket.connected()) {
            socket.emit("mobile", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        JSONObject messageJson = new JSONObject(args[0].toString());
                        String message = messageJson.getString("message");
                        TextView textMessageBody = (TextView) findViewById(R.id.textMessageBody);
                        textMessageBody.setText(message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  mMessageAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Your toast message.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


}
