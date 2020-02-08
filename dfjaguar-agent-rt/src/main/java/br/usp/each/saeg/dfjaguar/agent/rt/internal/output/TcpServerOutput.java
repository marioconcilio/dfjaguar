package br.usp.each.saeg.dfjaguar.agent.rt.internal.output;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.dfjaguar.core.runtime.AgentOptions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class TcpServerOutput {

    private TcpConnection connection;
    private ServerSocket serverSocket;
    private Thread worker;

    public void startup(final AgentOptions options, final RuntimeData data) throws IOException {
        serverSocket = createServerSocket(options);
        worker = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    synchronized (serverSocket) {
                        connection = new TcpConnection(serverSocket.accept(), data);
                    }

                    connection.init();
                    connection.run();
                }
                catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }
        });

        worker.setName(getClass().getName());
        worker.setDaemon(true);
        worker.start();
    }

    public void shutdown() throws Exception {
        serverSocket.close();

        synchronized (serverSocket) {
            if (connection != null) {
                connection.close();
            }
        }
        worker.join();
    }

    public void writeExecutionData(final boolean reset) throws IOException {
        if (connection != null) {
            connection.writeExecutionData(reset);
        }
    }

    protected ServerSocket createServerSocket(final AgentOptions options) throws IOException {
        final InetAddress inetAddr = getInetAddress(options.getAddress());
        return new ServerSocket(options.getPort(), 1, inetAddr);
    }

    protected InetAddress getInetAddress(final String address) throws UnknownHostException {
        if ("*".equals(address)) {
            return null;
        }

        return InetAddress.getByName(address);
    }
}
