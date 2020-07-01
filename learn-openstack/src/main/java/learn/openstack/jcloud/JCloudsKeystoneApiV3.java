package learn.openstack.jcloud;

import com.google.common.base.Optional;
import org.jclouds.ContextBuilder;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.extensions.ServiceAdminApi;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/25 16:45
 */
public class JCloudsKeystoneApiV3 {


    public static void main(String[] args) throws IOException {
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");
        overrides.put(KeystoneProperties.SCOPE, "projectId:0abb823c9c014cd2ac63886d5b00c13c");
        final KeystoneApi keystoneApi = ContextBuilder.newBuilder("openstack-keystone")
                .endpoint("http://192.168.251.12:5000/v3")
                .credentials("Default:admin", "cloud123...")
                .overrides(overrides)
                .buildApi(KeystoneApi.class);

        final Optional<? extends UserApi> optional = keystoneApi.getUserApi();
        if(optional.isPresent()){
            final UserApi userApi = optional.get();
            final PaginationOptions paginationOptions = new PaginationOptions();

            final Iterator<User> iterator = userApi.list(paginationOptions).iterator();
            while (iterator.hasNext()){
                final User next = iterator.next();
                System.out.println(next);
            }
        }

        final Optional<? extends ServiceAdminApi> apiServiceAdminApi = keystoneApi.getServiceAdminApi();
        if(apiServiceAdminApi.isPresent()){
            final ServiceAdminApi serviceAdminApi = apiServiceAdminApi.get();
            for (Service endpoints : serviceAdminApi.list().concat()) {
                System.out.println(endpoints);
            }
        }

        for (Tenant tenant : keystoneApi.getServiceApi().listTenants()) {
            System.out.println(tenant);
        }
    }
}
