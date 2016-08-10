/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@Ignore("TODO: Figure out what is causing UAA failures in CI")
public final class IdentityZonesTest extends AbstractIntegrationTest {

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void create() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String subdomainName = this.nameFactory.getDomainName();

        this.uaaClient.identityZones()
            .create(CreateIdentityZoneRequest.builder()
                .subdomain(subdomainName)
                .name(identityZoneName)
                .build())
            .then(requestListIdentityZones(this.uaaClient))
            .flatMapIterable(ListIdentityZonesResponse::getIdentityZones)
            .filter(resource -> identityZoneName.equals(resource.getName()))
            .subscribe(this.testSubscriber()
                .expectCount(1));
    }

    @Test
    public void delete() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String subdomainName = this.nameFactory.getDomainName();

        getIdentityZoneId(this.uaaClient, identityZoneName, subdomainName)
            .then(identityZoneId -> this.uaaClient.identityZones()
                .delete(DeleteIdentityZoneRequest.builder()
                    .identityZoneId(identityZoneId)
                    .build()))
            .then(requestListIdentityZones(this.uaaClient))
            .flatMapIterable(ListIdentityZonesResponse::getIdentityZones)
            .filter(resource -> identityZoneName.equals(resource.getName()))
            .subscribe(this.testSubscriber()
                .expectCount(0));
    }

    @Test
    public void get() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String subdomainName = this.nameFactory.getDomainName();

        getIdentityZoneId(this.uaaClient, identityZoneName, subdomainName)
            .then(identityZoneId -> this.uaaClient.identityZones()
                .get(GetIdentityZoneRequest.builder()
                    .identityZoneId(identityZoneId)
                    .build()))
            .map(GetIdentityZoneResponse::getName)
            .subscribe(this.testSubscriber()
                .expectEquals(identityZoneName));
    }

    @Test
    public void list() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String subdomainName = this.nameFactory.getDomainName();

        requestCreateIdentityZone(this.uaaClient, identityZoneName, subdomainName)
            .then(this.uaaClient.identityZones()
                .list(ListIdentityZonesRequest.builder()
                    .build()))
            .flatMapIterable(ListIdentityZonesResponse::getIdentityZones)
            .filter(resource -> identityZoneName.equals(resource.getName()))
            .subscribe(this.testSubscriber()
                .expectCount(1));
    }

    @Test
    public void update() {
        String identityZoneName = this.nameFactory.getIdentityZoneName();
        String baseSubdomainName = this.nameFactory.getDomainName();
        String newSubdomainName = this.nameFactory.getDomainName();

        getIdentityZoneId(this.uaaClient, identityZoneName, baseSubdomainName)
            .then(identityZoneId -> this.uaaClient.identityZones()
                .update(UpdateIdentityZoneRequest.builder()
                    .identityZoneId(identityZoneId)
                    .name(identityZoneName)
                    .subdomain(newSubdomainName)
                    .build()))
            .then(requestListIdentityZones(this.uaaClient))
            .flatMapIterable(ListIdentityZonesResponse::getIdentityZones)
            .filter(resource -> newSubdomainName.equals(resource.getSubdomain()))
            .subscribe(this.testSubscriber()
                .expectCount(1));
    }

    private static Mono<String> getIdentityZoneId(UaaClient uaaClient, String identityZoneName, String subdomainName) {
        return requestCreateIdentityZone(uaaClient, identityZoneName, subdomainName)
            .map(CreateIdentityZoneResponse::getId);
    }

    private static Mono<CreateIdentityZoneResponse> requestCreateIdentityZone(UaaClient uaaClient, String identityZoneName, String subdomainName) {
        return uaaClient.identityZones()
            .create(CreateIdentityZoneRequest.builder()
                .subdomain(subdomainName)
                .name(identityZoneName)
                .build());
    }

    private static Mono<ListIdentityZonesResponse> requestListIdentityZones(UaaClient uaaClient) {
        return uaaClient.identityZones()
            .list(ListIdentityZonesRequest.builder()
                .build());
    }

}