package br.usp.each.saeg.dfjaguar.agent.rt.internal;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.dfjaguar.agent.rt.internal.output.TcpServerOutput;
import br.usp.each.saeg.dfjaguar.core.data.ExecutionDataWriter;
import br.usp.each.saeg.dfjaguar.core.runtime.AgentOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

public class Agent implements IAgent {

    private static Agent singleton;
    private static final Random RANDOM = new Random();

    public static synchronized Agent getInstance(final AgentOptions options) throws Exception {
        if (singleton == null) {
            final Agent agent = new Agent(options);
            agent.startup();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> agent.shutdown()));
            singleton = agent;
        }

        return singleton;
    }

    public static synchronized Agent getInstance()
            throws IllegalStateException {
        if (singleton == null) {
            throw new IllegalStateException("agent not started.");
        }

        return singleton;
    }

    private final AgentOptions options;
    private final RuntimeData data;
    private TcpServerOutput output;

    private Agent(final AgentOptions options) {
        this.options = options;
        this.data = new RuntimeData();
    }

    public void startup() throws Exception {
        try {
//            String sessionId = createSessionId();
//            data.setSessionId(sessionId);
            output = new TcpServerOutput();
            output.startup(options, data);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void shutdown() {
        try {
            output.writeExecutionData(false);
            output.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RuntimeData getData() {
        return data;
    }

    private String createSessionId() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (final Exception e) {
            host = "unknownhost";
        }

        return host + "-" + Integer.toHexString(RANDOM.nextInt());
    }

    @Override
    public byte[] getExecutionData(boolean reset) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            final ExecutionDataWriter writer = new ExecutionDataWriter(buffer);
            data.collect(writer);
        }
        catch (final IOException e) {
            throw new AssertionError(e);
        }

        return buffer.toByteArray();
    }

    @Override
    public void dump(boolean reset) throws IOException {
        output.writeExecutionData(reset);
    }
}
