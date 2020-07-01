package learn.openstack.jcloud;

import org.jclouds.ContextBuilder;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.glance.v1_0.GlanceApi;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.features.ImageApi;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.v2_0.domain.PaginatedCollection;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/25 16:45
 */
public class JCloudsGlanceV3 {


    public static void main(String[] args) throws IOException {
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");
        overrides.put(KeystoneProperties.SCOPE, "projectId:0abb823c9c014cd2ac63886d5b00c13c");

        System.out.println("测试release开分支，打tag，修复bug");
        final GlanceApi glanceApi = ContextBuilder.newBuilder("openstack-glance")
                .apiVersion("v2.4")
                .endpoint("http://192.168.251.12:5000/v3")
                .credentials("Default:admin", "cloud123...")
                .overrides(overrides)
                .buildApi(GlanceApi.class);

        for (String s : glanceApi.getConfiguredRegions()) {

            final ImageApi imageApi = glanceApi.getImageApi(s);

            for (ImageDetails imageDetails : imageApi.listInDetail().concat()) {
                System.out.println(imageDetails);
            }
        }
    }
}
