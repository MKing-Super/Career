package per.mk.springai.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Spring AI Demo 应用启动类
 */
@SpringBootApplication
@Slf4j
public class SpringaiDemoApplication {

    @Value("${server.port:8080}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(SpringaiDemoApplication.class, args);
    }

    /**
     * 应用启动后打印访问地址
     */
    @PostConstruct
    public void logStartup() {
        String host = getLocalIp();
        log.info("========================================");
        log.info("应用已启动: http://{}:{}", host, port);
        log.info("========================================");
    }

    /**
     * 获取本机局域网IP
     * 排除 loopback、VirtualBox 等虚拟网卡
     *
     * @return 本机IP地址
     */
    private String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                String name = networkInterface.getName();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                if (name.contains("VirtualBox") || name.contains("vbox") || name.startsWith("veth")) {
                    continue;
                }
                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("获取本机IP失败", e);
        }
        return "localhost";
    }

}
