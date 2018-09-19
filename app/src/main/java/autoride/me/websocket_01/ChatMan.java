package autoride.me.websocket_01;

/**
 * Created by goldenreign on 9/19/2018.
 */

public class ChatMan {

    String handle;
    String message;

    public ChatMan(String name, String message) {
        this.handle = name;
        this.message = message;
    }

    public String getName() {
        return handle;
    }

    public void setName(String name) {
        this.handle = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
