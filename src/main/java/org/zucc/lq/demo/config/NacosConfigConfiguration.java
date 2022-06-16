package org.zucc.lq.demo.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosPropertySourceRepository;
import com.alibaba.cloud.nacos.client.NacosPropertySource;
import com.alibaba.cloud.nacos.parser.NacosDataParserHandler;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.event.config.EventPublishingConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.zucc.lq.demo.Unmeaning;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration(proxyBeanMethods = false)
@EnableNacosConfig(
        globalProperties = @NacosProperties(
                serverAddr = "${spring.cloud.nacos.config.server-addr}"
        )
)
public class NacosConfigConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfigConfiguration.class);

    @Bean
    public Unmeaning nacosConfigManagerPostProcessor(NacosConfigManager nacosConfigManager,
                                                     ConfigurableApplicationContext applicationContext) throws NacosException {
        ConfigService configService = nacosConfigManager.getConfigService();
        EventPublishingConfigService configService_2 = new EventPublishingConfigService(configService, nacosConfigManager.getNacosConfigProperties().assembleConfigServiceProperties(),
                applicationContext, Executors.newSingleThreadExecutor());

        for (NacosPropertySource propertySource : NacosPropertySourceRepository
                .getAll()) {
            if (!propertySource.isRefreshable()) {
                continue;
            }
            String dataId = propertySource.getDataId();
            String group = propertySource.getGroup();
            configService_2.addListener(dataId,
                    group,
                    "yaml",
                    new AbstractListener() {
                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            System.out.println("没什么用... 监控");
                            System.out.println("没什么用... 打印");
                            System.out.println("等着environment刷新完 或者自己在这里去刷新");


                            ConfigurableEnvironment environment = applicationContext.getEnvironment();
                            MutablePropertySources propertySources = environment.getPropertySources();

                            PropertySource<?> before = propertySources.get(dataId);
                            LOGGER.info("before dataId = {}, source = {}, name={}, class={}", dataId, before.getSource(), before.getName(), before.getClass());
                            try {
                                List<PropertySource<?>> yaml = NacosDataParserHandler.getInstance().parseNacosData(dataId, configInfo,
                                        "yaml");
                                yaml.forEach(propertySource -> {
                                    propertySources.replace(propertySource.getName(), propertySource);
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // 线程安全的

                            PropertySource<?> after = propertySources.get(dataId);
                            LOGGER.info("after dataId = {}, source = {}, name={}, class={}", dataId, after.getSource(), after.getName(), after.getClass());

                        }
                    });
        }

        try {
            Field service = nacosConfigManager.getClass().getDeclaredField("service");
            service.setAccessible(true);
            service.set(nacosConfigManager, configService_2);
        } catch (Exception e) {

        }


        return new Unmeaning();
    }
}
