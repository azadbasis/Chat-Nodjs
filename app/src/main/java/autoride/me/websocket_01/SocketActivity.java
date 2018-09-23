package autoride.me.websocket_01;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketActivity extends AppCompatActivity {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.0.119:4000");
        } catch (URISyntaxException e) {
        }
    }

    private TextView textMessageTyping;
    private EditText mInputMessageView, editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        mSocket.on("mobile", onNewMessage);
        mSocket.connect();
        mInputMessageView = (EditText) findViewById(R.id.mInputMessageView);

    }




    public void sendText(View view) {

        mInputMessageView = (EditText) findViewById(R.id.mInputMessageView);
        editName = (EditText) findViewById(R.id.editName);
        String message = mInputMessageView.getText().toString().trim();
        String handle = editName.getText().toString().trim();

        if (TextUtils.isEmpty(message) & TextUtils.isEmpty(handle)) {
            return;
        }
        ChatMan chatMan = new ChatMan(handle, message);
        Gson gson = new Gson();
        String json = gson.toJson(chatMan);
        mInputMessageView.setText("");
        mSocket.emit("mobile", json);

    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(args[0].toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String username;
                    String message;
                    try {
                        username = data.getString("handle");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                    playBeep();

                }
            });
        }
    };



    private void addMessage(String username, String message) {

        TextView textMessageBody = (TextView) findViewById(R.id.textMessageBody);
        String chatText = username + ":" + "  " + message;
        textMessageBody.setText(chatText);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("mobile", onNewMessage);
    }



    public void playBeep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
