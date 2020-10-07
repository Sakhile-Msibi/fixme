package fixme.router.models;

import fixme.router.controllers.RouterController;

public class Dispatch implements CommInterface {
    private int DISPATCH = CommInterface.DISPATCH;

    @Override
    public void performAction(Headers header, int resp) {
        int id = getDestination(header.msg);
        int srcId = getSource(header.msg);

        if (resp != DISPATCH) {
            new EchoBack().performAction(header, resp);
            return ;
        }

        if (srcId != header.clientId) {
            System.out.println("src = " + srcId + " clientId = "+ header.clientId);
            new EchoBack().performAction(header, CommInterface.ECHOBACK);
            return ;
        }  
        try {
            if (header.client.isOpen() && RouterController.getSize() > 1) {
                Headers head = RouterController.getClient(id);
                if (head == null) {
                    new EchoBack().performAction(header, CommInterface.ECHOBACK);
                    return ;
                }
                head.isRead = false;
                head.client.write(header.buffer, head, header.rwHandler);
            }
        } catch(Exception e) {
            new EchoBack().performAction(header, CommInterface.ECHOBACK);
        }
    }

    private int getDestination(String datum[]) {
        try {
            for(int i = 0; i < datum.length; i++) {
                if (datum[i].contains("56")) {
                    return Integer.parseInt(datum[i].split("=")[1]);
                }
            }
        } catch(Exception e) {

        }
        return -1;
    }

    private int getSource(String datum[]) {
        try {
            if (datum[0].split("=")[0].equalsIgnoreCase("id")) {
                return Integer.parseInt(datum[0].split("=")[1]);
            }
        } catch(Exception e) {
            
        }
        return -1;
    }
}