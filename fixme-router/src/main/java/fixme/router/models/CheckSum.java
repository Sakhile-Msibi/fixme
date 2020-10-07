package fixme.router.models;

public class CheckSum implements CommInterface {
    private int CHECKSUM = CommInterface.CHECKSUM;

    @Override
    public void performAction(Headers header, int resp) {
        int size = getMsgSize(header.msg);
        int checksum = getCheckSum(header.msg[header.msg.length - 1]);
        int action;

        if (resp != CHECKSUM) {
            new Dispatch().performAction(header, resp);
            return ;
        }

        if (size % 256 != checksum) {
            action = CommInterface.ECHOBACK;
        } else {
            action = CommInterface.DISPATCH;
        }
        new Dispatch().performAction(header, action);
    }

    private int getMsgSize(String datum[]) {
        int j = 0;
		char t[];
        
        for(int k = 0; k < datum.length - 1; k++) {
            t = datum[k].toCharArray();
            
			for(int i = 0; i < t.length; i++) {
				j += (int)t[i];
			}
			j += 1;
		}
        return (j);
    }

    private int getCheckSum(String str) {
        int tag;
        int value;

        try {
            String ops[] = str.split("=");
            tag = Integer.parseInt(ops[0]);
            value = Integer.parseInt(ops[1]);
            if (tag == 10) {
                return value;
            }
        } catch(Exception e) {
            System.out.println("Error message passed");
        }
        return (0);
    }
}