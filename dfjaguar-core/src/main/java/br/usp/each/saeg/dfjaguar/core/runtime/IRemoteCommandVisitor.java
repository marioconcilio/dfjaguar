package br.usp.each.saeg.dfjaguar.core.runtime;

import java.io.IOException;

public interface IRemoteCommandVisitor {

    /**
     * Requests a execution data dump with an optional reset.
     *
     * @param dump
     *            <code>true</code> if the dump should be executed
     * @param reset
     *            <code>true</code> if the reset should be executed
     * @throws IOException
     *             in case of problems with the remote connection
     */
    void visitDumpCommand(boolean dump, boolean reset) throws IOException;

}
