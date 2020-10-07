package fixme.router.models;

public class EchoBack implements CommInterface {
    private int ECHOBACK = CommInterface.ECHOBACK;

    @Override
    public void performAction(Headers attatch, int resp) {
        if (resp != ECHOBACK)
            return ;
        attatch.isRead = false;
        attatch.client.write(attatch.buffer, attatch, attatch.rwHandler);
    }
}