package chapter_05.ioc;

public class MessageHolder {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageHolder{message='" + message + "'}";
    }
}

