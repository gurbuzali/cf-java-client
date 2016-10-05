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

package org.cloudfoundry.reactor.client.v3.tasks;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.GetTaskRequest;
import org.cloudfoundry.client.v3.tasks.GetTaskResponse;
import org.cloudfoundry.client.v3.tasks.ListTasksRequest;
import org.cloudfoundry.client.v3.tasks.ListTasksResponse;
import org.cloudfoundry.client.v3.tasks.Result;
import org.cloudfoundry.client.v3.tasks.State;
import org.cloudfoundry.client.v3.tasks.TaskResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;
import reactor.test.ScriptedSubscriber;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorTasksTest {

    public static final class Cancel extends AbstractClientApiTest<CancelTaskRequest, CancelTaskResponse> {

        private final ReactorTasks tasks = new ReactorTasks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CancelTaskResponse> expectations() {
            return ScriptedSubscriber.<CancelTaskResponse>create()
                .expectValue(CancelTaskResponse.builder()
                    .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .name("migrate")
                    .command("rake db:migrate")
                    .state(State.CANCELING_STATE)
                    .memoryInMb(512)
                    .environmentVariables(Collections.emptyMap())
                    .result(Result.builder()
                        .build())
                    .link("self", Link.builder()
                        .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("droplet", Link.builder()
                        .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/tasks/test-id/cancel")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v3/tasks/PUT_{id}_cancel_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CancelTaskResponse> invoke(CancelTaskRequest request) {
            return this.tasks.cancel(request);
        }

        @Override
        protected CancelTaskRequest validRequest() {
            return CancelTaskRequest.builder()
                .taskId("test-id")
                .build();
        }

    }

    public static final class Create extends AbstractClientApiTest<CreateTaskRequest, CreateTaskResponse> {

        private final ReactorTasks tasks = new ReactorTasks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateTaskResponse> expectations() {
            return ScriptedSubscriber.<CreateTaskResponse>create()
                .expectValue(CreateTaskResponse.builder()
                    .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .name("migrate")
                    .command("rake db:migrate")
                    .state(State.RUNNING_STATE)
                    .memoryInMb(256)
                    .environmentVariables(Collections.emptyMap())
                    .result(Result.builder()
                        .build())
                    .link("self", Link.builder()
                        .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("droplet", Link.builder()
                        .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v3/apps/test-application-id/tasks")
                    .payload("fixtures/client/v3/tasks/POST_apps_{id}_tasks_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v3/tasks/POST_apps_{id}_tasks_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateTaskResponse> invoke(CreateTaskRequest request) {
            return this.tasks.create(request);
        }

        @Override
        protected CreateTaskRequest validRequest() {
            return CreateTaskRequest.builder()
                .applicationId("test-application-id")
                .command("echo 'hello world'")
                .memoryInMb(512)
                .name("my-task")
                .build();
        }

    }

    public static final class Get extends AbstractClientApiTest<GetTaskRequest, GetTaskResponse> {

        private final ReactorTasks tasks = new ReactorTasks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetTaskResponse> expectations() {
            return ScriptedSubscriber.<GetTaskResponse>create()
                .expectValue(GetTaskResponse.builder()
                    .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .name("migrate")
                    .command("rake db:migrate")
                    .state(State.RUNNING_STATE)
                    .memoryInMb(256)
                    .environmentVariables(Collections.emptyMap())
                    .result(Result.builder()
                        .build())
                    .link("self", Link.builder()
                        .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("droplet", Link.builder()
                        .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/tasks/test-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/tasks/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetTaskResponse> invoke(GetTaskRequest request) {
            return this.tasks.get(request);
        }

        @Override
        protected GetTaskRequest validRequest() {
            return GetTaskRequest.builder()
                .taskId("test-id")
                .build();
        }

    }

    public static final class List extends AbstractClientApiTest<ListTasksRequest, ListTasksResponse> {

        private final ReactorTasks tasks = new ReactorTasks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListTasksResponse> expectations() {
            return ScriptedSubscriber.<ListTasksResponse>create()
                .expectValue(ListTasksResponse.builder()
                    .pagination(Pagination.builder()
                        .totalResults(3)
                        .first(Link.builder()
                            .href("/v3/tasks?page=1&per_page=2")
                            .build())
                        .last(Link.builder()
                            .href("/v3/tasks?page=2&per_page=2")
                            .build())
                        .next(Link.builder()
                            .href("/v3/tasks?page=2&per_page=2")
                            .build())
                        .build())
                    .resource(TaskResource.builder()
                        .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .name("hello")
                        .command("echo \"hello world\"")
                        .state(State.SUCCEEDED_STATE)
                        .memoryInMb(256)
                        .environmentVariables(Collections.emptyMap())
                        .result(Result.builder()
                            .build())
                        .link("self", Link.builder()
                            .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                            .build())
                        .link("app", Link.builder()
                            .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                            .build())
                        .link("droplet", Link.builder()
                            .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                            .build())
                        .build())
                    .resource(TaskResource.builder()
                        .id("63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                        .name("migrate")
                        .command("rake db:migrate")
                        .state(State.FAILED_STATE)
                        .memoryInMb(256)
                        .environmentVariables(Collections.emptyMap())
                        .result(Result.builder()
                            .failureReason("Exited with status 1")
                            .build())
                        .link("self", Link.builder()
                            .href("/v3/tasks/63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                            .build())
                        .link("app", Link.builder()
                            .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                            .build())
                        .link("droplet", Link.builder()
                            .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/tasks?page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/tasks/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListTasksResponse> invoke(ListTasksRequest request) {
            return this.tasks.list(request);
        }

        @Override
        protected ListTasksRequest validRequest() {
            return ListTasksRequest.builder()
                .page(1)
                .build();
        }

    }

}
