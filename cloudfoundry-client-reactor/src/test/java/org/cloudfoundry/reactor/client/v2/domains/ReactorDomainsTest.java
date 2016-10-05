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

package org.cloudfoundry.reactor.client.v2.domains;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DeleteDomainResponse;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesResponse;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;
import reactor.test.ScriptedSubscriber;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDomainsTest {

    public static final class Create extends AbstractClientApiTest<CreateDomainRequest, CreateDomainResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateDomainResponse> expectations() {
            return CreateDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("abb8338f-eaea-4149-85c0-61888bac0737")
                    .url("/v2/domains/abb8338f-eaea-4149-85c0-61888bac0737")
                    .createdAt("2015-07-27T22:43:33Z")
                    .build())
                .entity(DomainEntity.builder()
                    .name("exmaple.com")
                    .owningOrganizationId("09e0d56f-4e50-4bff-af83-9bd87a7d7f00")
                    .owningOrganizationUrl("/v2/organizations/09e0d56f-4e50-4bff-af83-9bd87a7d7f00")
                    .sharedOrganizations(Collections.emptyList())
                    .spacesUrl("/v2/domains/abb8338f-eaea-4149-85c0-61888bac0737/spaces")
                    .build())
                .build();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/domains")
                    .payload("fixtures/client/v2/domains/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/domains/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateDomainResponse> invoke(CreateDomainRequest request) {
            return this.domains.create(request);
        }

        @Override
        protected CreateDomainRequest validRequest() {
            return CreateDomainRequest.builder()
                .name("exmaple.com")
                .owningOrganizationId("09e0d56f-4e50-4bff-af83-9bd87a7d7f00")
                .wildcard(true)
                .build();
        }
    }

    public static final class Delete extends AbstractClientApiTest<DeleteDomainRequest, DeleteDomainResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteDomainResponse> expectations() {
            return null;
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/domains/test-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteDomainResponse> invoke(DeleteDomainRequest request) {
            return this.domains.delete(request);
        }

        @Override
        protected DeleteDomainRequest validRequest() {
            return DeleteDomainRequest.builder()
                .domainId("test-domain-id")
                .build();
        }
    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteDomainRequest, DeleteDomainResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteDomainResponse> expectations() {
            return DeleteDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/domains/test-domain-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/domains/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteDomainResponse> invoke(DeleteDomainRequest request) {
            return this.domains.delete(request);
        }

        @Override
        protected DeleteDomainRequest validRequest() {
            return DeleteDomainRequest.builder()
                .async(true)
                .domainId("test-domain-id")
                .build();
        }
    }

    public static final class Get extends AbstractClientApiTest<GetDomainRequest, GetDomainResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetDomainResponse> expectations() {
            return GetDomainResponse.builder()
                .metadata(Metadata.builder()
                    .id("7cd249aa-197c-425c-8831-57cbc24e8e26")
                    .url("/v2/domains/7cd249aa-197c-425c-8831-57cbc24e8e26")
                    .createdAt("2015-07-27T22:43:33Z")
                    .build())
                .entity(DomainEntity.builder()
                    .name("domain-63.example.com")
                    .build())
                .build();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/domains/test-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/domains/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetDomainResponse> invoke(GetDomainRequest request) {
            return this.domains.get(request);
        }

        @Override
        protected GetDomainRequest validRequest() {
            return GetDomainRequest.builder()
                .domainId("test-domain-id")
                .build();
        }

    }

    public static final class ListDomains extends AbstractClientApiTest<ListDomainsRequest, ListDomainsResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListDomainsResponse> expectations() {
            return ListDomainsResponse.builder()
                .totalResults(4)
                .totalPages(1)
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("c8e670ff-2473-4e21-8047-afc6e0c62ce7")
                        .url("/v2/domains/c8e670ff-2473-4e21-8047-afc6e0c62ce7")
                        .createdAt("2015-07-27T22:43:31Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain1.com")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("2b63d3fa-52e9-4f12-87d1-a96af5bd3cd4")
                        .url("/v2/domains/2b63d3fa-52e9-4f12-87d1-a96af5bd3cd4")
                        .createdAt("2015-07-27T22:43:31Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain2.com")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da")
                        .url("/v2/domains/2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da")
                        .createdAt("2015-07-27T22:43:31Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("vcap.me")
                        .owningOrganizationId("f93d5a41-5d35-4e21-ac32-421dfd545d3c")
                        .owningOrganizationUrl("/v2/organizations/f93d5a41-5d35-4e21-ac32-421dfd545d3c")
                        .spacesUrl("/v2/domains/2c60a78c-0f6e-4ef8-81db-f3a6cb5e31da/spaces")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("b37aab98-5882-420a-a91f-65539e36e860")
                        .url("/v2/domains/b37aab98-5882-420a-a91f-65539e36e860")
                        .createdAt("2015-07-27T22:43:33Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("domain-62.example.com")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/domains?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/domains/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListDomainsResponse> invoke(ListDomainsRequest request) {
            return this.domains.list(request);
        }

        @Override
        protected ListDomainsRequest validRequest() {
            return ListDomainsRequest.builder()
                .page(-1)
                .build();
        }
    }

    public static final class ListSpaces extends AbstractClientApiTest<ListDomainSpacesRequest, ListDomainSpacesResponse> {

        private final ReactorDomains domains = new ReactorDomains(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListDomainSpacesResponse> expectations() {
            return ListDomainSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                    .metadata(Metadata.builder()
                        .id("d1686ef7-59dc-4ada-8900-85e89d749046")
                        .url("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046")
                        .createdAt("2015-07-27T22:43:33Z")
                        .build())
                    .entity(SpaceEntity.builder()
                        .name("name-2311")
                        .organizationId("836b112a-30bc-4d55-b8e4-7323849759d1")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/836b112a-30bc-4d55-b8e4-7323849759d1")
                        .developersUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/developers")
                        .managersUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/managers")
                        .auditorsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/auditors")
                        .applicationsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/apps")
                        .routesUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/routes")
                        .domainsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/domains")
                        .serviceInstancesUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/service_instances")
                        .applicationEventsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/app_events")
                        .eventsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/events")
                        .securityGroupsUrl("/v2/spaces/d1686ef7-59dc-4ada-8900-85e89d749046/security_groups")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/domains/test-domain-id/spaces?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/domains/GET_{id}_spaces_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListDomainSpacesResponse> invoke(ListDomainSpacesRequest request) {
            return this.domains.listSpaces(request);
        }

        @Override
        protected ListDomainSpacesRequest validRequest() {
            return ListDomainSpacesRequest.builder()
                .domainId("test-domain-id")
                .page(-1)
                .build();
        }

    }

}
