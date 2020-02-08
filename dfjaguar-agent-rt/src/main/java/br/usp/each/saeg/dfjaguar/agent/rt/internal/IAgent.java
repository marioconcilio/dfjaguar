package br.usp.each.saeg.dfjaguar.agent.rt.internal;

import java.io.IOException;

public interface IAgent {

    /**
     * Returns current execution data.
     *
     * @param reset
     *            if <code>true</code> the current execution data is cleared
     *            afterwards
     * @return dump of current execution data in JaCoCo binary format
     */
    byte[] getExecutionData(boolean reset);

    /**
     * Triggers a dump of the current execution data through the configured
     * output.
     *
     * @param reset
     *            if <code>true</code> the current execution data is cleared
     *            afterwards
     * @throws IOException
     *             if the output can't write execution data
     */
    void dump(boolean reset) throws IOException;
}
