package br.usp.each.saeg.dfjaguar.core.data;

import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.data.IExecutionDataVisitor;
import br.usp.each.saeg.badua.core.internal.data.CompactDataOutput;

import java.io.IOException;
import java.io.OutputStream;

import static br.usp.each.saeg.badua.core.data.ExecutionDataWriter.*;

public class ExecutionDataWriter implements IExecutionDataVisitor {

    protected final CompactDataOutput out;

    public ExecutionDataWriter(final OutputStream output) throws IOException {
        out = new CompactDataOutput(output);
        writeHeader();
    }

    private void writeHeader() throws IOException {
        out.writeByte(BLOCK_HEADER);
        out.writeChar(MAGIC_NUMBER);
        out.writeChar(FORMAT_VERSION);
    }

    @Override
    public void visitClassExecution(final ExecutionData data) {
        try {
            out.writeByte(BLOCK_EXECUTIONDATA);
            out.writeLong(data.getId());
            out.writeUTF(data.getName());
            out.writeLongArray(data.getData());
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
