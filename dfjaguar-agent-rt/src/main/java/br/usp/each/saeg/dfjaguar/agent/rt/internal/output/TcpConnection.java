package br.usp.each.saeg.dfjaguar.agent.rt.internal.output;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.dfjaguar.core.runtime.IRemoteCommandVisitor;
import br.usp.each.saeg.dfjaguar.core.runtime.RemoteControlReader;
import br.usp.each.saeg.dfjaguar.core.runtime.RemoteControlWriter;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TcpConnection implements IRemoteCommandVisitor {

    private final RuntimeData data;
    private final Socket socket;
    private RemoteControlWriter writer;
    private RemoteControlReader reader;
    private boolean initialized;

    public TcpConnection(final Socket socket, final RuntimeData data) {
        this.socket = socket;
        this.data = data;
        this.initialized = false;
    }

    public void init() throws IOException {
        this.writer = new RemoteControlWriter(socket.getOutputStream());
        this.reader = new RemoteControlReader(socket.getInputStream());
        this.reader.setRemoteCommandVisitor(this);
        this.initialized = true;
    }

    public void run() throws IOException {
        try {
            reader.read();
        }
        catch (final SocketException e) {
            if (!socket.isClosed()) {
                throw e;
            }
        }
        finally {
            close();
        }
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public void writeExecutionData(final boolean reset) throws IOException {
        if (initialized && !socket.isClosed()) {
            visitDumpCommand(true, reset);
        }
    }

    public void visitDumpCommand(boolean dump, boolean reset) throws IOException {
        if (dump) {
            data.collect(writer);
        }
        else {
            if (reset) {
//                data.reset();
            }
        }
        writer.sendCmdOk();
    }
}
