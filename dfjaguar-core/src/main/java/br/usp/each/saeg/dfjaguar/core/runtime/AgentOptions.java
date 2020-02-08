package br.usp.each.saeg.dfjaguar.core.runtime;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility to create and parse options for the runtime agent. Options are
 * represented as a string in the following format:
 *
 * <pre>
 *   key1=value1,key2=value2,key3=value3
 * </pre>
 */
public final class AgentOptions {

    /**
     * The IP address or DNS name the tcpserver binds to or the tcpclient
     * connects to. Default is defined by {@link #DEFAULT_ADDRESS}.
     */
    public static final String ADDRESS = "address";

    /**
     * Default value for the "address" agent option.
     */
    public static final String DEFAULT_ADDRESS = null;

    /**
     * The port the tcpserver binds to or the tcpclient connects to. In
     * tcpserver mode the port must be available, which means that if multiple
     * JaCoCo agents should run on the same machine, different ports have to be
     * specified. Default is defined by {@link #DEFAULT_PORT}.
     */
    public static final String PORT = "port";

    /**
     * Default value for the "port" agent option.
     */
    public static final int DEFAULT_PORT = 6300;

    private final Map<String, String> options;

    /**
     * New instance with all values set to default.
     */
    public AgentOptions() {
        this.options = new HashMap<>();
    }

    private static final Collection<String> VALID_OPTIONS = Arrays.asList(
            ADDRESS,
            PORT
    );

    private static final Pattern OPTION_SPLIT = Pattern.compile(",(?=[a-zA-Z0-9_\\-]+=)");

    /**
     * New instance parsed from the given option string.
     *
     * @param optionstr
     *            string to parse or <code>null</code>
     */
    public AgentOptions(final String optionstr) {
        this();
        if (optionstr != null && optionstr.length() > 0) {
            for (final String entry : OPTION_SPLIT.split(optionstr)) {
                final int pos = entry.indexOf('=');
                if (pos == -1) {
                    throw new IllegalArgumentException(String.format("Invalid agent option syntax \"%s\".", optionstr));
                }
                final String key = entry.substring(0, pos);
                if (!VALID_OPTIONS.contains(key)) {
                    throw new IllegalArgumentException(String.format("Unknown agent option \"%s\".", key));
                }

                final String value = entry.substring(pos + 1);
                setOption(key, value);
            }

            validateAll();
        }
    }

    /**
     * Generate required JVM argument based on current configuration and
     * supplied agent jar location.
     *
     * @param agentJarFile
     *            location of the JaCoCo Agent Jar
     * @return Argument to pass to create new VM with coverage enabled
     */
    public String getVMArgument(final File agentJarFile) {
        return String.format("-javaagent:%s=%s", agentJarFile, this);
    }

    /**
     * Generate required quoted JVM argument based on current configuration and
     * supplied agent jar location.
     *
     * @param agentJarFile
     *            location of the JaCoCo Agent Jar
     * @return Quoted argument to pass to create new VM with coverage enabled
     */
    public String getQuotedVMArgument(final File agentJarFile) {
        return CommandLineSupport.quote(getVMArgument(agentJarFile));
    }

    /**
     * Generate required quotes JVM argument based on current configuration and
     * prepends it to the given argument command line. If a agent with the same
     * JAR file is already specified this parameter is removed from the existing
     * command line.
     *
     * @param arguments
     *            existing command line arguments or <code>null</code>
     * @param agentJarFile
     *            location of the JaCoCo Agent Jar
     * @return VM command line arguments prepended with configured JaCoCo agent
     */
    public String prependVMArguments(final String arguments, final File agentJarFile) {
        final List<String> args = CommandLineSupport.split(arguments);
        final String plainAgent = String.format("-javaagent:%s", agentJarFile);
        for (final Iterator<String> i = args.iterator(); i.hasNext();) {
            if (i.next().startsWith(plainAgent)) {
                i.remove();
            }
        }
        args.add(0, getVMArgument(agentJarFile));
        return CommandLineSupport.quote(args);
    }

    /**
     * Returns the port on which to listen to when the output is
     * <code>tcpserver</code> or the port to connect to when output is
     * <code>tcpclient</code>.
     *
     * @return port to listen on or connect to
     */
    public int getPort() {
        return getOption(PORT, DEFAULT_PORT);
    }

    /**
     * Sets the port on which to listen to when output is <code>tcpserver</code>
     * or the port to connect to when output is <code>tcpclient</code>
     *
     * @param port
     *            port to listen on or connect to
     */
    public void setPort(final int port) {
        validatePort(port);
        setOption(PORT, port);
    }

    /**
     * Gets the hostname or IP address to listen to when output is
     * <code>tcpserver</code> or connect to when output is
     * <code>tcpclient</code>
     *
     * @return Hostname or IP address
     */
    public String getAddress() {
        return getOption(ADDRESS, DEFAULT_ADDRESS);
    }

    /**
     * Sets the hostname or IP address to listen to when output is
     * <code>tcpserver</code> or connect to when output is
     * <code>tcpclient</code>
     *
     * @param address
     *            Hostname or IP address
     */
    public void setAddress(final String address) {
        setOption(ADDRESS, address);
    }

    private void validateAll() {
        validatePort(getPort());
    }

    private void validatePort(final int port) {
        if (port < 0) {
            throw new IllegalArgumentException("port must be positive");
        }
    }

    private void setOption(final String key, final int value) {
        setOption(key, Integer.toString(value));
    }

    private void setOption(final String key, final boolean value) {
        setOption(key, Boolean.toString(value));
    }

    private void setOption(final String key, final String value) {
        options.put(key, value);
    }

    private String getOption(final String key, final String defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : value;
    }

    private boolean getOption(final String key, final boolean defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private int getOption(final String key, final int defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    /**
     * Creates a string representation that can be passed to the agent via the
     * command line. Might be the empty string, if no options are set.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final String key : VALID_OPTIONS) {
            final String value = options.get(key);
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append(',');
                }

                sb.append(key).append('=').append(value);
            }
        }
        return sb.toString();
    }
}
