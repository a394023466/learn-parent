package learn.openstack.jcloud;

import org.jclouds.ContextBuilder;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/25 16:45
 */
public class JCloudsNovaV3 {


    public static void main(String[] args) throws IOException {
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");

        final NovaApi novaApi = ContextBuilder.newBuilder("openstack-nova")
                .endpoint("http://192.168.251.10:5000/v3")
                .credentials("Default:admin", "cloud123...")
                .overrides(overrides)
                .buildApi(NovaApi.class);

        for (String region : novaApi.getConfiguredRegions()) {
//            /**
//             *云主机，也就是虚拟机
//             */
            ServerApi serverApi = novaApi.getServerApi(region);
            for (Server server : serverApi.listInDetail().concat()) {
                System.out.println(server.getMetadata());
            }
//            final Optional<KeyPairApi> keyPairApiOptional = novaApi.getKeyPairApi(region);
//            if (keyPairApiOptional.isPresent()) {
//                for (KeyPair keyPair : keyPairApiOptional.get().list()) {
//                    System.out.println(keyPair);
//                }
//            }
//
//            final Optional<SecurityGroupApi> securityGroupApiOptional = novaApi.getSecurityGroupApi(region);
//            if (securityGroupApiOptional.isPresent()) {
//                for (SecurityGroup securityGroup : securityGroupApiOptional.get().list()) {
//                    for (SecurityGroupRule securityGroupRule : securityGroup.getRules()) {
//
//                    }
//                }
//            }
//
//            /**
//             * 镜像信息
//             */
//            ImageApi imageApi = novaApi.getImageApi(region);
//            for (Image image : imageApi.listInDetail().concat()) {
//                System.out.println(image.getMetadata());
//            }
//
//            /**
//             * 主机与每个project占用主机资源情况
//             */
//            final Optional<HostAdministrationApi> hostAdministrationApiOptional = novaApi.getHostAdministrationApi(region);
//            if (hostAdministrationApiOptional.isPresent()) {
//                Set<String> hostNames = new HashSet<>();
//                for (Host host : hostAdministrationApiOptional.get().list()) {
//                    System.out.println(host);
//                    hostNames.add(host.getName());
//                }
//                for (String name : hostNames) {
//                    for (HostResourceUsage test : hostAdministrationApiOptional.get().listResourceUsage(name)) {
//                        System.out.println(test);
//                    }
//                }
//            }
//
//            /**
//             *  虚拟机管理器信息
//             */
//            FlavorApi flavorApi = novaApi.getFlavorApi(region);
//            final Optional<HypervisorApi> hypervisorApiOptional = novaApi.getHypervisorApi(region);
//            if (hypervisorApiOptional.isPresent()) {
//                final HypervisorApi hypervisorApi = hypervisorApiOptional.get();
//                for (HypervisorDetails hypervisorDetails : hypervisorApi.listInDetail()) {
//                    System.out.println(hypervisorDetails);
//                }
//            }
//            /**
//             *模板信息
//             */
//            for (Flavor flavor : flavorApi.listInDetail().concat()) {
//                for (Link link : flavor.getLinks()) {
//                    System.out.println(link);
//                }
//            }
            /**
             * 浮动IP
             */
//            Optional<FloatingIPApi> floatingIPApiOptional = novaApi.getFloatingIPApi(region);
//            if (floatingIPApiOptional.isPresent()) {
//                final FloatingIPApi floatingIPApi = floatingIPApiOptional.get();
//                for (FloatingIP floatingIP : floatingIPApi.list()) {
//                    System.out.println(floatingIP);
//                }
//            }
//            final Optional<KeyPairApi> keyPairApi = novaApi.getKeyPairApi(region);
//            if(keyPairApi.isPresent()){
//                for (KeyPair keyPair : keyPairApi.get().list()) {
//                    System.out.println(keyPair);
//                }
//            }
        }
    }
}
