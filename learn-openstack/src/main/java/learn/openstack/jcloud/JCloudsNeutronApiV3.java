package learn.openstack.jcloud;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.jclouds.ContextBuilder;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.*;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.HealthMonitor;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Member;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.Pool;
import org.jclouds.openstack.neutron.v2.domain.lbaas.v1.VIP;
import org.jclouds.openstack.neutron.v2.extensions.FWaaSApi;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.extensions.lbaas.v1.LBaaSApi;
import org.jclouds.openstack.neutron.v2.features.FloatingIPApi;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.features.ExtensionApi;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/25 16:45
 */
public class JCloudsNeutronApiV3 {


    public static void main(String[] args) throws IOException {
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");
        overrides.put(KeystoneProperties.SCOPE, "projectId:0abb823c9c014cd2ac63886d5b00c13c");
        final NeutronApi neutronApi = ContextBuilder.newBuilder("openstack-neutron")
                .endpoint("http://192.168.251.12:5000/v3")
                .credentials("Default:admin", "cloud123...")
                .overrides(overrides)
                .buildApi(NeutronApi.class);


        for (String s : neutronApi.getConfiguredRegions()) {
            /**
             * 网络信息
             */
            final NetworkApi networkApi = neutronApi.getNetworkApi(s);
            for (Network network : networkApi.list().concat()) {
                System.out.println(network.getNetworkType().name());
            }
//
//            /**
//             * 获取子网信息
//             */
//            final SubnetApi subnetApi = neutronApi.getSubnetApi(s);
//            for (Subnet subnet : subnetApi.list().concat()) {
//                System.out.println(subnet);
//            }
//            /**
//             * 获取路由信息
//             */
//            final Optional<RouterApi> routerApi = neutronApi.getRouterApi(s);
//            if (routerApi.isPresent()) {
//                for (Router router : routerApi.get().list().concat()) {
//                    System.out.println(router);
//                }
//            }
//
//            /**
//             * 端口信息
//             * 返回当前租户在Neutron中定义的所有端口的列表。该列表提供了惟一的
//             * 为租户配置的每个网络的标识符。
//             */
//            final PortApi portApi = neutronApi.getPortApi(s);
//            for (Port port : portApi.list().concat()) {
//                System.out.println(port);
//            }
//
//            /**
//             * 获取防火墙信息
//             * FWaaS扩展为OpenStack用户提供了部署防火墙以保护其网络的能力。
//             */
//            final Optional<FWaaSApi> fWaaSApiOptional = neutronApi.getFWaaSApi(s);
//            if (fWaaSApiOptional.isPresent()) {
//                /**
//                 * 返回当前租户在Neutron中定义的所有路由器的列表。该列表提供了惟一的
//                 * 为租户配置的每个防火墙的标识符
//                 */
//                final FWaaSApi fWaaSApi = fWaaSApiOptional.get();
//                for (Firewall firewall : fWaaSApi.list().concat()) {
//                    System.out.println(firewall);
//                }
//            }
//
//            /**
//             * 提供对OpenStack网络(Neutron) v2 API的负载平衡操作的访问。
//             * LBaaS v1是一个扩展，用于负载平衡实例和外部网络之间的通信。
//             */
//            final Optional<LBaaSApi> lBaaSApiOptional = neutronApi.getLBaaSApi(s);
//            if (lBaaSApiOptional.isPresent()) {
//
//                final LBaaSApi lBaaSApi = lBaaSApiOptional.get();
//                for (VIP vip : lBaaSApi.listVIPs().concat()) {
//                    System.out.println(vip);
//                }
//
//                /**
//                 * 返回租户可以访问的池的列表。默认策略设置仅返回
//                 * *提交请求的租户拥有的池，除非请求是由
//                 * *具有管理权限的用户。
//                 */
//                for (Pool pool : lBaaSApi.listPools().concat()) {
//                    System.out.println(pool);
//                }
//
//                /**
//                 * 返回租户可以访问的成员列表。默认策略设置仅返回
//                 * *提交请求的租户拥有的成员，除非该请求是由
//                 * *具有管理权限的用户。
//                 */
//                for (Member member : lBaaSApi.listMembers().concat()) {
//                    System.out.println(member);
//                }
//
//                /**
//                 * 返回租户可以访问的HealthMonitors列表。默认策略设置仅返回
//                 *
//                 * *由提交请求的租户拥有的HealthMonitors，除非该请求是由
//                 *
//                 * *具有管理权限的用户。
//                 */
//                for (HealthMonitor healthMonitor : lBaaSApi.listHealthMonitors().concat()) {
//                    System.out.println(healthMonitor);
//                }
//            }
//
//            /**
//             * Provides access to Extension features.
//             *
//             */
//            final ExtensionApi extensionApi = neutronApi.getExtensionApi(s);
//            /**
//             * 列出所有可用的扩展
//             */
//            for (Extension extension : extensionApi.list()) {
//                System.out.println(extension);
//            }

            final FloatingIPApi floatingIPApi = neutronApi.getFloatingIPApi(s);
            for (FloatingIP floatingIP : floatingIPApi.list().concat()) {
                System.out.println(floatingIP);
            }
        }
    }
}
