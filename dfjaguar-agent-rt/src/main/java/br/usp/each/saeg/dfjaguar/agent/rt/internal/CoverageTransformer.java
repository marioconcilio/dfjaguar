package br.usp.each.saeg.dfjaguar.agent.rt.internal;

import br.usp.each.saeg.badua.core.instr.Instrumenter;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CoverageTransformer implements ClassFileTransformer {

    private final static String BADUA_PACKAGE = RT.class.getPackage().getName().replace('.', '/');

    private final Instrumenter instrumenter;

    public CoverageTransformer() {
        instrumenter = new Instrumenter(RT.class.getName(),
                Boolean.valueOf(System.getProperty("badua.experimental.exception_handler")));
    }

    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] classfileBuffer) throws IllegalClassFormatException {

        if (skip(loader, className)) {
            return null;
        }

        try {
            return instrumenter.instrument(classfileBuffer, className);
        }
        catch (final IOException e) {
            final IllegalClassFormatException ex = new IllegalClassFormatException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
    }

    private boolean skip(final ClassLoader loader, final String className) {
        return loader == null
                || loader.getClass().getName().equals("sun.reflect.DelegatingClassLoader")
                || loader.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader")
                || className.startsWith(BADUA_PACKAGE);
    }
}
