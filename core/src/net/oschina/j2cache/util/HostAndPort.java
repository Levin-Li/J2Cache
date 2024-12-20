package net.oschina.j2cache.util;

/**
 * 为了一增加三方库的依赖，单独实现了一个HostAndPort类
 * 实现逻辑参考了spring-data-redis的RedisNode工具类
 */
public class HostAndPort {

    /**
     * Hostname, IPv4/IPv6 literal, or unvalidated nonsense.
     */
    private String host;

    /**
     * Validated port number in the range [0..65535], or NO_PORT
     */
    private int port;

    private HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "HostAndPort{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    /**
     * @param hostPortString
     * @return
     */
    public static HostAndPort fromString(String hostPortString) {
        if (!StringUtils.hasText(hostPortString)) {
            throw new IllegalArgumentException("HostAndPort must not be null");
        }
        hostPortString = hostPortString.trim();

        String host;
        String portString = null;

        if (hostPortString.startsWith("[")) {
            String[] hostAndPort = getHostAndPortFromBracketedHost(hostPortString);
            host = hostAndPort[0];
            portString = hostAndPort[1];
        } else {
            int colonPos = hostPortString.indexOf(':');
            if (colonPos >= 0 && hostPortString.indexOf(':', colonPos + 1) == -1) {
                // Exactly 1 colon. Split into host:port.
                host = hostPortString.substring(0, colonPos);
                portString = hostPortString.substring(colonPos + 1);
            } else {
                // 0 or 2+ colons. Bare hostname or IPv6 literal.
                host = hostPortString;
            }
        }

        int port = -1;
        try {
            port = Integer.parseInt(portString);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(String.format("Unparseable port number: %s", hostPortString));
        }

        if (!isValidPort(port)) {
            throw new IllegalArgumentException(String.format("Port number out of range: %s", hostPortString));
        }

        return new HostAndPort(host, port);
    }


    /**
     * Parses a bracketed host-port string, throwing IllegalArgumentException if parsing fails.
     *
     * @param hostPortString the full bracketed host-port specification. Post might not be specified.
     * @return an array with 2 strings: host and port, in that order.
     * @throws IllegalArgumentException if parsing the bracketed host-port string fails.
     */
    private static String[] getHostAndPortFromBracketedHost(String hostPortString) {

        if (hostPortString.charAt(0) != '[') {
            throw new IllegalArgumentException(
                    String.format("Bracketed host-port string must start with a bracket: %s", hostPortString));
        }

        int colonIndex = hostPortString.indexOf(':');
        int closeBracketIndex = hostPortString.lastIndexOf(']');

        if (!(colonIndex > -1 && closeBracketIndex > colonIndex)) {
            throw new IllegalArgumentException(String.format("Invalid bracketed host/port: %s", hostPortString));
        }

        String host = hostPortString.substring(1, closeBracketIndex);
        if (closeBracketIndex + 1 == hostPortString.length()) {
            return new String[]{host, ""};
        } else {
            if (!(hostPortString.charAt(closeBracketIndex + 1) == ':')) {
                throw new IllegalArgumentException(
                        String.format("Only a colon may follow a close bracket: %s", hostPortString));
            }
            for (int i = closeBracketIndex + 2; i < hostPortString.length(); ++i) {
                if (!Character.isDigit(hostPortString.charAt(i))) {
                    throw new IllegalArgumentException(String.format("Port must be numeric: %s", hostPortString));
                }
            }
            return new String[]{host, hostPortString.substring(closeBracketIndex + 2)};
        }
    }

    private static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }

    public static void main(String[] args) {
        HostAndPort hostAndPort = HostAndPort.fromString("127.0.0.1:6379");
        System.out.println(hostAndPort);

        hostAndPort = HostAndPort.fromString("[2001:db8:130F:0000:0000:09C0:876A:130B]:6379");
        System.out.println(hostAndPort);

        //will throw error
//        hostAndPort = HostAndPort.fromString("2001:db8:130F:0000:0000:09C0:876A:130B:6379");
//        System.out.println(hostAndPort);
    }

}
