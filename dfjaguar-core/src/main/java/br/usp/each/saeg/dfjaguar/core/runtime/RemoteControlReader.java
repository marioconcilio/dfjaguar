package br.usp.each.saeg.dfjaguar.core.runtime;

import br.usp.each.saeg.dfjaguar.core.data.ExecutionDataReader;

import java.io.IOException;
import java.io.InputStream;

public class RemoteControlReader extends ExecutionDataReader {

    private IRemoteCommandVisitor remoteCommandVisitor;

    public RemoteControlReader(final InputStream input) {
        super(input);
    }

    @Override
    protected void readBlock(final byte type) throws IOException {
        switch (type) {
            case RemoteControlWriter.BLOCK_CMDDUMP:
                readDumpCommand();
                break;
            case RemoteControlWriter.BLOCK_CMDOK:
                break;
            default:
                super.readBlock(type);
        }
    }

    public void setRemoteCommandVisitor(final IRemoteCommandVisitor visitor) {
        this.remoteCommandVisitor = visitor;
    }

    private void readDumpCommand() throws IOException {
        if (remoteCommandVisitor == null) {
            throw new IOException("No remote command visitor.");
        }

        final boolean dump = in.readBoolean();
        final boolean reset = in.readBoolean();
        remoteCommandVisitor.visitDumpCommand(dump, reset);
    }
}
