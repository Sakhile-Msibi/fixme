package fixme.router.models;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;

public class Headers {
    public AsynchronousServerSocketChannel server;
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public SocketAddress clientAddr;
    public String msg[];
    public Writer rwHandler;
    public boolean isRead;
}
  