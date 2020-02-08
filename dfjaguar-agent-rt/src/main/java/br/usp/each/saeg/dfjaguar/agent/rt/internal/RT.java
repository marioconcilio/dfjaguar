package br.usp.each.saeg.dfjaguar.agent.rt.internal;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.dfjaguar.agent.rt.internal.Agent;
import br.usp.each.saeg.dfjaguar.core.runtime.AgentOptions;

public final class RT {

    private static RuntimeData DATA;

    private RT() {}

    public static void init(final AgentOptions options) throws Exception {
        init(Agent.getInstance(options).getData());
    }

    public static void init(final RuntimeData data) {
        DATA = data;
    }

    public static long[] getData(final long classId, final String className, final int size) {
        return DATA.getExecutionData(classId, className, size).getData();
    }
}
