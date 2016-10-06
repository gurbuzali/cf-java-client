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

package org.cloudfoundry.reactor.client.v2.serviceinstances;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceToRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceToRouteResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
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
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceInstancesTest {

    public static final class BindToRoute extends AbstractClientApiTest<BindServiceInstanceToRouteRequest, BindServiceInstanceToRouteResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<BindServiceInstanceToRouteResponse> expectations() {
            return ScriptedSubscriber.<BindServiceInstanceToRouteResponse>create()
                .expectValue(BindServiceInstanceToRouteResponse.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-12-22T18:27:58Z")
                        .id("e7e5b08e-c530-4c1c-b420-fa0b09b3770d")
                        .url("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-160")
                        .credential("creds-key-89", "creds-val-89")
                        .servicePlanId("957307f5-6811-4eba-8667-ffee5a704a4a")
                        .spaceId("36b01ada-ef02-4ff5-9f78-cd9e704211d2")
                        .type("managed_service_instance")
                        .tags(Collections.emptyList())
                        .spaceUrl("/v2/spaces/36b01ada-ef02-4ff5-9f78-cd9e704211d2")
                        .servicePlanUrl("/v2/service_plans/957307f5-6811-4eba-8667-ffee5a704a4a")
                        .serviceBindingsUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/service_keys")
                        .routesUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/routes")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/service_instances/test-service-instance-id/routes/route-id")
                    .payload("fixtures/client/v2/service_instances/PUT_{id}_routes_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_instances/PUT_{id}_routes_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<BindServiceInstanceToRouteResponse> invoke(BindServiceInstanceToRouteRequest request) {
            return this.serviceInstances.bindToRoute(request);
        }

        @Override
        protected BindServiceInstanceToRouteRequest validRequest() {
            return BindServiceInstanceToRouteRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .routeId("route-id")
                .parameter("the_service_broker", "wants this object")
                .build();
        }

    }

    public static final class Create extends AbstractClientApiTest<CreateServiceInstanceRequest, CreateServiceInstanceResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateServiceInstanceResponse> expectations() {
            return ScriptedSubscriber.<CreateServiceInstanceResponse>create()
                .expectValue(CreateServiceInstanceResponse.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:08Z")
                        .id("8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                        .url("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("my-service-instance")
                        .credential("creds-key-356", "creds-val-356")
                        .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                        .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                        .type("managed_service_instance")
                        .lastOperation(LastOperation.builder()
                            .createdAt("2015-07-27T22:43:08Z")
                            .updatedAt("2015-07-27T22:43:08Z")
                            .description("")
                            .state("in progress")
                            .type("create")
                            .build())
                        .tag("accounting")
                        .tag("mongodb")
                        .spaceUrl("/v2/spaces/86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                        .servicePlanUrl("/v2/service_plans/2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                        .serviceBindingsUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_keys")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/service_instances?accepts_incomplete=true")
                    .payload("fixtures/client/v2/service_instances/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_instances/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateServiceInstanceResponse> invoke(CreateServiceInstanceRequest request) {
            return this.serviceInstances.create(request);
        }

        @Override
        protected CreateServiceInstanceRequest validRequest() {
            return CreateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .name("my-service-instance")
                .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                .parameter("the_service_broker", "wants this object")
                .tag("accounting")
                .tag("mongodb")
                .build();
        }

    }

    public static final class Delete extends AbstractClientApiTest<DeleteServiceInstanceRequest, DeleteServiceInstanceResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteServiceInstanceResponse> expectations() {
            return ScriptedSubscriber.<DeleteServiceInstanceResponse>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_instances/test-service-instance-id?accepts_incomplete=true&purge=true")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteServiceInstanceResponse> invoke(DeleteServiceInstanceRequest request) {
            return this.serviceInstances.delete(request);
        }

        @Override
        protected DeleteServiceInstanceRequest validRequest() {
            return DeleteServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .acceptsIncomplete(true)
                .purge(true)
                .build();
        }

    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteServiceInstanceRequest, DeleteServiceInstanceResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteServiceInstanceResponse> expectations() {
            return ScriptedSubscriber.<DeleteServiceInstanceResponse>create()
                .expectValue(DeleteServiceInstanceResponse.builder()
                    .metadata(Metadata.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .createdAt("2016-02-02T17:16:31Z")
                        .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .build())
                    .entity(JobEntity.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .status("queued")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_instances/test-service-instance-id?accepts_incomplete=true&async=true&purge=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/service_instances/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteServiceInstanceResponse> invoke(DeleteServiceInstanceRequest request) {
            return this.serviceInstances.delete(request);
        }

        @Override
        protected DeleteServiceInstanceRequest validRequest() {
            return DeleteServiceInstanceRequest.builder()
                .async(true)
                .serviceInstanceId("test-service-instance-id")
                .acceptsIncomplete(true)
                .purge(true)
                .build();
        }

    }

    public static final class Get extends AbstractClientApiTest<GetServiceInstanceRequest, GetServiceInstanceResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetServiceInstanceResponse> expectations() {
            return ScriptedSubscriber.<GetServiceInstanceResponse>create()
                .expectValue(GetServiceInstanceResponse.builder()
                    .metadata(Metadata.builder()
                        .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                        .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                        .createdAt("2015-07-27T22:43:08Z")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-133")
                        .credential("creds-key-72", "creds-val-72")
                        .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                        .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                        .type("managed_service_instance")
                        .lastOperation(LastOperation.builder()
                            .type("create")
                            .state("succeeded")
                            .description("service broker-provided description")
                            .updatedAt("2015-07-27T22:43:08Z")
                            .createdAt("2015-07-27T22:43:08Z")
                            .build())
                        .tag("accounting")
                        .tag("mongodb")
                        .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                        .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                        .serviceBindingsUrl
                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                        .serviceKeysUrl
                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_instances/test-service-instance-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_instances/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetServiceInstanceResponse> invoke(GetServiceInstanceRequest request) {
            return this.serviceInstances.get(request);
        }

        @Override
        protected GetServiceInstanceRequest validRequest() {
            return GetServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build();
        }

    }

    public static final class GetPermissions extends AbstractClientApiTest<GetServiceInstancePermissionsRequest, GetServiceInstancePermissionsResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetServiceInstancePermissionsResponse> expectations() {
            return ScriptedSubscriber.<GetServiceInstancePermissionsResponse>create()
                .expectValue(GetServiceInstancePermissionsResponse.builder()
                    .manage(true)
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_instances/test-service-instance-id/permissions")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_instances/GET_{id}_permissions_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetServiceInstancePermissionsResponse> invoke(GetServiceInstancePermissionsRequest request) {
            return this.serviceInstances.getPermissions(request);
        }

        @Override
        protected GetServiceInstancePermissionsRequest validRequest() {
            return GetServiceInstancePermissionsRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build();
        }

    }

    public static final class List extends AbstractClientApiTest<ListServiceInstancesRequest, ListServiceInstancesResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListServiceInstancesResponse> expectations() {
            return ScriptedSubscriber.<ListServiceInstancesResponse>create()
                .expectValue(ListServiceInstancesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceInstanceResource.builder()
                        .metadata(Metadata.builder()
                            .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                            .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                            .createdAt("2015-07-27T22:43:08Z")
                            .build())
                        .entity(ServiceInstanceEntity.builder()
                            .name("name-133")
                            .credential("creds-key-72", "creds-val-72")
                            .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                            .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                            .type("managed_service_instance")
                            .lastOperation(LastOperation.builder()
                                .type("create")
                                .state("succeeded")
                                .description("service broker-provided description")
                                .updatedAt("2015-07-27T22:43:08Z")
                                .createdAt("2015-07-27T22:43:08Z")
                                .build())
                            .tag("accounting")
                            .tag("mongodb")
                            .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                            .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                            .serviceBindingsUrl
                                ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                            .serviceKeysUrl
                                ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_instances?q=name%20IN%20test-name&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_instances/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListServiceInstancesResponse> invoke(ListServiceInstancesRequest request) {
            return this.serviceInstances.list(request);
        }

        @Override
        protected ListServiceInstancesRequest validRequest() {
            return ListServiceInstancesRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

    }

    public static final class ListServiceBindings extends AbstractClientApiTest<ListServiceInstanceServiceBindingsRequest, ListServiceInstanceServiceBindingsResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListServiceInstanceServiceBindingsResponse> expectations() {
            return ScriptedSubscriber.<ListServiceInstanceServiceBindingsResponse>create()
                .expectValue(ListServiceInstanceServiceBindingsResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceBindingResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2015-07-27T22:43:09Z")
                            .id("05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                            .url("/v2/service_bindings/05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                            .build())
                        .entity(ServiceBindingEntity.builder()
                            .applicationId("8a50163b-a39d-4f44-aece-dc5a956da848")
                            .serviceInstanceId("a5a0567e-edbf-4da9-ae90-dce24af308a1")
                            .bindingOptions(Collections.emptyMap())
                            .credential("creds-key-85", "creds-val-85")
                            .gatewayName("")
                            .applicationUrl("/v2/apps/8a50163b-a39d-4f44-aece-dc5a956da848")
                            .serviceInstanceUrl("/v2/service_instances/a5a0567e-edbf-4da9-ae90-dce24af308a1")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_instances/test-service-instance-id/service_bindings?q=app_guid%20IN%20test-application-id&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_instances/GET_{id}_service_bindings_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListServiceInstanceServiceBindingsResponse> invoke(ListServiceInstanceServiceBindingsRequest request) {
            return this.serviceInstances.listServiceBindings(request);
        }

        @Override
        protected ListServiceInstanceServiceBindingsRequest validRequest() {
            return ListServiceInstanceServiceBindingsRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .applicationId("test-application-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListServiceKeys extends AbstractClientApiTest<ListServiceInstanceServiceKeysRequest, ListServiceInstanceServiceKeysResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListServiceInstanceServiceKeysResponse> expectations() {
            return ScriptedSubscriber.<ListServiceInstanceServiceKeysResponse>create()
                .expectValue(ListServiceInstanceServiceKeysResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceKeyResource.builder()
                        .metadata(Metadata.builder()
                            .id("03ddc0ba-f792-4762-b4e4-dc08b307dc4f")
                            .url("/v2/service_keys/03ddc0ba-f792-4762-b4e4-dc08b307dc4f")
                            .createdAt("2016-05-04T04:49:09Z")
                            .build())
                        .entity(ServiceKeyEntity.builder()
                            .name("a-service-key")
                            .serviceInstanceId("28120eae-4a44-42da-a3db-2a34aea8dcaa")
                            .credential("creds-key-68", "creds-val-68")
                            .serviceInstanceUrl("/v2/service_instances/28120eae-4a44-42da-a3db-2a34aea8dcaa")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_instances/test-service-instance-id/service_keys?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_instances/GET_{id}_service_keys_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListServiceInstanceServiceKeysResponse> invoke(ListServiceInstanceServiceKeysRequest request) {
            return this.serviceInstances.listServiceKeys(request);
        }

        @Override
        protected ListServiceInstanceServiceKeysRequest validRequest() {
            return ListServiceInstanceServiceKeysRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .page(-1)
                .build();
        }

    }

    public static final class Update extends AbstractClientApiTest<UpdateServiceInstanceRequest, UpdateServiceInstanceResponse> {

        private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<UpdateServiceInstanceResponse> expectations() {
            return ScriptedSubscriber.<UpdateServiceInstanceResponse>create()
                .expectValue(UpdateServiceInstanceResponse.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:08Z")
                        .id("2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41")
                        .url("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-139")
                        .credential("creds-key-75", "creds-val-75")
                        .servicePlanId("b07ff29a-78b8-486f-87a8-3f695368b83d")
                        .spaceId("04219ffa-a817-459f-bbd7-c161bdca541b")
                        .type("managed_service_instance")
                        .tags(Collections.emptyList())
                        .lastOperation(LastOperation.builder()
                            .createdAt("2015-07-27T22:43:08Z")
                            .updatedAt("2015-07-27T22:43:08Z")
                            .description("")
                            .state("in progress")
                            .type("update")
                            .build())
                        .spaceUrl("/v2/spaces/04219ffa-a817-459f-bbd7-c161bdca541b")
                        .servicePlanUrl("/v2/service_plans/b07ff29a-78b8-486f-87a8-3f695368b83d")
                        .serviceBindingsUrl("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/2a80a0f7-cb9c-414a-8a6b-7cc3f811ad41/service_keys")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/service_instances/test-service-instance-id?accepts_incomplete=true")
                    .payload("fixtures/client/v2/service_instances/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_instances/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<UpdateServiceInstanceResponse> invoke(UpdateServiceInstanceRequest request) {
            return this.serviceInstances.update(request);
        }

        @Override
        protected UpdateServiceInstanceRequest validRequest() {
            return UpdateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .serviceInstanceId("test-service-instance-id")
                .servicePlanId("5b5e984f-bbf6-477b-9d3a-b6d5df941b50")
                .parameter("the_service_broker", "wants this object")
                .tags(Collections.emptyList())
                .build();
        }

    }

}
