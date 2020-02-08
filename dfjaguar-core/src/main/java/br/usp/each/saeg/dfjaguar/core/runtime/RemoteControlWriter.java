package br.usp.each.saeg.dfjaguar.core.runtime;

import br.usp.each.saeg.dfjaguar.core.data.ExecutionDataWriter;

import java.io.IOException;
import java.io.OutputStream;

public class RemoteControlWriter extends ExecutionDataWriter implements IRemoteCommandVisitor {

    public static final byte BLOCK_CMDOK = 0x20;
    public static final byte BLOCK_CMDDUMP = 0x40;

    public RemoteControlWriter(OutputStream output) throws IOException {
        super(output);
    }

    public void sendCmdOk() throws IOException {
        out.writeByte(RemoteControlWriter.BLOCK_CMDOK);
    }

    @Override
    public void visitDumpCommand(final boolean dump, final boolean reset) throws IOException {
        out.writeByte(RemoteControlWriter.BLOCK_CMDDUMP);
        out.writeBoolean(dump);
        out.writeBoolean(reset);
    }
}
