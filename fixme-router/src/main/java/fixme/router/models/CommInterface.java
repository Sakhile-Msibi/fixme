package fixme.router.models;

public interface CommInterface {
    public static int CHECKSUM = 1;
    public static int DISPATCH = 2;
    public static int ECHOBACK = 3;
    public void performAction(Headers header, int resp);
}