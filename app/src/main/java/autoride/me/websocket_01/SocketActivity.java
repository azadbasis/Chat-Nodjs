package autoride.me.websocket_01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        mSocket.on("mobile", onNewMessage);
        mSocket.connect();
    }

    private EditText mInputMessageView, eidtName;

    public void sendText(View view) {
        mInputMessageView = (EditText) findViewById(R.id.mInputMessageView);
        eidtName = (EditText) findViewById(R.id.eidtName);
        String message = mInputMessageView.getText().toString().trim();
        String handle = eidtName.getText().toString().trim();
        if (TextUtils.isEmpty(message) & TextUtils.isEmpty(handle)) {
            return;
        }
        ChatMan chatMan = new ChatMan(handle, message);
        Gson gson = new Gson();
        String json = gson.toJson(chatMan);

        mInputMessageView.setText("");
        eidtName.setText("");
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
                }
            });
        }
    };

    private void addMessage(String username, String message) {

        TextView textMessageBody = (TextView) findViewById(R.id.textMessageBody);
        textMessageBody.setText(username + ":" + "  " + message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("mobile", onNewMessage);
    }

}
