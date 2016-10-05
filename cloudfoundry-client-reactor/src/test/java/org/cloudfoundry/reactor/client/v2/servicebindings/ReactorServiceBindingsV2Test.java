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

package org.cloudfoundry.reactor.client.v2.servicebindings;


import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
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
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceBindingsV2Test {

    public static final class Create extends AbstractClientApiTest<CreateServiceBindingRequest, CreateServiceBindingResponse> {

        private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/service_bindings")
                    .payload("fixtures/client/v2/service_bindings/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_bindings/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected ScriptedSubscriber<CreateServiceBindingResponse> expectations() {
            return CreateServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:20Z")
                    .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                    .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                    .build())
                .entity(ServiceBindingEntity.builder()
                    .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                    .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                    .bindingOptions(Collections.emptyMap())
                    .credential("creds-key-356", "creds-val-356")
                    .gatewayName("")
                    .applicationUrl("/v2/apps/26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                    .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                    .build())
                .build();
        }

        @Override
        protected CreateServiceBindingRequest validRequest() {
            return CreateServiceBindingRequest.builder()
                .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                .parameters(Collections.singletonMap("the_service_broker", (Object) "wants this object"))
                .build();
        }

        @Override
        protected Mono<CreateServiceBindingResponse> invoke(CreateServiceBindingRequest request) {
            return this.serviceBindings.create(request);
        }

    }

    public static final class Delete extends AbstractClientApiTest<DeleteServiceBindingRequest, DeleteServiceBindingResponse> {

        private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_bindings/test-service-binding-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected ScriptedSubscriber<DeleteServiceBindingResponse> expectations() {
            return null;
        }

        @Override
        protected DeleteServiceBindingRequest validRequest() {
            return DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<DeleteServiceBindingResponse> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteServiceBindingRequest, DeleteServiceBindingResponse> {

        private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_bindings/test-service-binding-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/service_bindings/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected ScriptedSubscriber<DeleteServiceBindingResponse> expectations() {
            return DeleteServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .createdAt("2016-03-14T22:30:51Z")
                    .url("/v2/jobs/c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .build())
                .entity(JobEntity.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteServiceBindingRequest validRequest() {
            return DeleteServiceBindingRequest.builder()
                .async(true)
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<DeleteServiceBindingResponse> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }

    public static final class Get extends AbstractClientApiTest<GetServiceBindingRequest, GetServiceBindingResponse> {

        private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_bindings/test-service-binding-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_bindings/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected ScriptedSubscriber<GetServiceBindingResponse> expectations() {
            return GetServiceBindingResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-11-03T00:53:50Z")
                    .id("925d8848-4808-47cf-a3e8-049aa0163328")
                    .updatedAt("2015-11-04T12:54:50Z")
                    .url("/v2/service_bindings/925d8848-4808-47cf-a3e8-049aa0163328")
                    .build())
                .entity(ServiceBindingEntity.builder()
                    .applicationId("56ae4265-4c1c-43a9-9069-2c1fee7fd42f")
                    .serviceInstanceId("f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                    .bindingOptions(Collections.emptyMap())
                    .credential("creds-key-108", "creds-val-108")
                    .gatewayName("")
                    .applicationUrl("/v2/apps/56ae4265-4c1c-43a9-9069-2c1fee7fd42f")
                    .serviceInstanceUrl("/v2/service_instances/f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                    .build())
                .build();
        }

        @Override
        protected GetServiceBindingRequest validRequest() {
            return GetServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<GetServiceBindingResponse> invoke(GetServiceBindingRequest request) {
            return this.serviceBindings.get(request);
        }

    }

    public static final class List extends AbstractClientApiTest<ListServiceBindingsRequest, ListServiceBindingsResponse> {

        private final ReactorServiceBindingsV2 serviceBindings = new ReactorServiceBindingsV2(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_bindings?q=app_guid%20IN%20dd44fd4f-5e20-4c52-b66d-7af6e201f01e&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_bindings/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ScriptedSubscriber<ListServiceBindingsResponse> expectations() {
            return ListServiceBindingsResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:06Z")
                        .id("d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .url("/v2/service_bindings/d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-3", "creds-val-3")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-11-03T00:53:50Z")
                        .id("925d8848-4808-47cf-a3e8-049aa0163328")
                        .updatedAt("2015-11-04T12:54:50Z")
                        .url("/v2/service_bindings/925d8848-4808-47cf-a3e8-049aa0163328")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-108", "creds-val-108")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:20Z")
                        .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-356", "creds-val-356")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListServiceBindingsRequest validRequest() {
            return ListServiceBindingsRequest.builder()
                .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListServiceBindingsResponse> invoke(ListServiceBindingsRequest request) {
            return this.serviceBindings.list(request);
        }

    }

}
