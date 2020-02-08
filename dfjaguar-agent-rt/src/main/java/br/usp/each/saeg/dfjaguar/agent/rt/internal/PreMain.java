package br.usp.each.saeg.dfjaguar.agent.rt.internal;

import br.usp.each.saeg.dfjaguar.core.runtime.AgentOptions;

import java.lang.instrument.Instrumentation;

public class PreMain {

    private PreMain() {}

    public static void premain(final String options, final Instrumentation inst) throws Exception {
        final AgentOptions agentOptions = new AgentOptions(options);
        RT.init(agentOptions);
        inst.addTransformer(new CoverageTransformer());
    }
}
