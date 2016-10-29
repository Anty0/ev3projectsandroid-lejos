package eu.codetopic.anty.ev3projectsbase;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;
import net.sf.lipermi.net.IServerListener;
import net.sf.lipermi.net.Server;

import java.io.IOException;
import java.net.Socket;

public final class BaseConstants {

    public static final int SELF_VERSION_CODE = 3;
    public static final CallHandler CALL_HANDLER = new CallHandler();
    public static final int TARGET_PORT = 4455;

    private BaseConstants() {
    }

    public static void initForServer(RMIHardware rmiHardware, RMIModes rmiModes) throws LipeRMIException {
        CALL_HANDLER.registerGlobal(RMIHardware.class, rmiHardware);
        CALL_HANDLER.registerGlobal(RMIModes.class, rmiModes);
        CALL_HANDLER.registerGlobal(RMIVersion.class, new RMIVersionImpl());
    }

    public static ClientDetector startServer(Server server) throws IOException {
        ClientDetector detector = new ClientDetector();
        server.addServerListener(detector);
        server.bind(TARGET_PORT, CALL_HANDLER);
        return detector;
    }

    public static SingleClientDetector startSingleClientServer(Server server) throws IOException {
        SingleClientDetector detector = new SingleClientDetector();
        server.addServerListener(detector);
        server.bind(TARGET_PORT, CALL_HANDLER);
        return detector;
    }

    public static Client startClient(String targetAddress) throws IOException {
        return new Client(targetAddress, TARGET_PORT, CALL_HANDLER);
    }

    public static class ClientDetector implements IServerListener {

        protected int count = 0;

        public int getClientsCount() {
            return count;
        }

        @Override
        public synchronized void clientConnected(Socket socket) {
            count++;
        }

        @Override
        public synchronized void clientDisconnected(Socket socket) {
            count--;
        }

    }

    public static class SingleClientDetector extends ClientDetector {

        public boolean isClientConnected() {
            return count > 0;
        }

        @Override
        public synchronized void clientConnected(Socket socket) {
            if (count > 0) {
                System.err.println("Caught request to connect more then one client to server" +
                        " that allows only one client connection. Disconnecting this client...");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.clientConnected(socket);
        }
    }

}
