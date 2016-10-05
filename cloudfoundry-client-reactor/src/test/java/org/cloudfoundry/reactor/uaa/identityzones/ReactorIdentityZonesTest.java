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

package org.cloudfoundry.reactor.uaa.identityzones;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.IdentityZone;
import org.cloudfoundry.uaa.identityzones.IdentityZoneConfiguration;
import org.cloudfoundry.uaa.identityzones.Links;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.identityzones.LogoutLink;
import org.cloudfoundry.uaa.identityzones.Prompt;
import org.cloudfoundry.uaa.identityzones.SamlConfiguration;
import org.cloudfoundry.uaa.identityzones.SelfServiceLink;
import org.cloudfoundry.uaa.identityzones.TokenPolicy;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneResponse;
import reactor.core.publisher.Mono;
import reactor.test.ScriptedSubscriber;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorIdentityZonesTest {

    public static final class Create extends AbstractUaaApiTest<CreateIdentityZoneRequest, CreateIdentityZoneResponse> {

        private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateIdentityZoneResponse> expectations() {
            return ScriptedSubscriber.<CreateIdentityZoneResponse>create()
                .expectValue(CreateIdentityZoneResponse.builder()
                    .createdAt(1463595920184L)
                    .description("Like the Twilight Zone but tastier.")
                    .id("twiglet-create")
                    .lastModified(1463595920184L)
                    .name("The Twiglet Zone")
                    .subdomain("twiglet-create")
                    .version(0)
                    .configuration(IdentityZoneConfiguration.builder()
                        .tokenPolicy(TokenPolicy.builder()
                            .accessTokenValidity(-1)
                            .jwtRevocable(false)
                            .refreshTokenValidity(-1)
                            .key("exampleKeyId", Collections.singletonMap("signingKey", "s1gNiNg.K3y/t3XT"))
                            .build())
                        .samlConfiguration(SamlConfiguration.builder()
                            .assertionSigned(true)
                            .requestSigned(true)
                            .wantAssertionSigned(false)
                            .wantPartnerAuthenticationRequestSigned(false)
                            .assertionTimeToLive(600)
                            .build())
                        .links(Links.builder()
                            .logout(LogoutLink.builder()
                                .redirectUrl("/login")
                                .redirectParameterName("redirect")
                                .disableRedirectParameter(true)
                                .build())
                            .selfService(SelfServiceLink.builder()
                                .selfServiceLinksEnabled(true)
                                .signupLink("/create_account")
                                .resetPasswordLink("/forgot_password")
                                .build())
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("username")
                            .text("Email")
                            .fieldType("text")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("password")
                            .text("Password")
                            .fieldType("password")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("passcode")
                            .text("One Time Code (Get on at /passcode)")
                            .fieldType("password")
                            .build())
                        .ldapDiscoveryEnabled(false)
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/identity-zones")
                    .payload("fixtures/uaa/identity-zones/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/identity-zones/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateIdentityZoneResponse> invoke(CreateIdentityZoneRequest request) {
            return this.identityZones.create(request);
        }

        @Override
        protected CreateIdentityZoneRequest validRequest() {
            return CreateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but tastier.")
                .identityZoneId("twiglet-create")
                .name("The Twiglet Zone")
                .subdomain("twiglet-create")
                .version(0)
                .configuration(IdentityZoneConfiguration.builder()
                    .tokenPolicy(TokenPolicy.builder()
                        .accessTokenValidity(-1)
                        .jwtRevocable(false)
                        .refreshTokenValidity(-1)
                        .key("exampleKeyId", Collections.singletonMap("signingKey", "s1gNiNg.K3y/t3XT"))
                        .build())
                    .samlConfiguration(SamlConfiguration.builder()
                        .assertionSigned(true)
                        .requestSigned(true)
                        .wantAssertionSigned(false)
                        .wantPartnerAuthenticationRequestSigned(false)
                        .assertionTimeToLive(600)
                        .build())
                    .links(Links.builder()
                        .logout(LogoutLink.builder()
                            .redirectUrl("/login")
                            .redirectParameterName("redirect")
                            .disableRedirectParameter(true)
                            .build())
                        .selfService(SelfServiceLink.builder()
                            .selfServiceLinksEnabled(true)
                            .signupLink("/create_account")
                            .resetPasswordLink("/forgot_password")
                            .build())
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("username")
                        .fieldType("text")
                        .text("Email")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("password")
                        .fieldType("password")
                        .text("Password")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("passcode")
                        .fieldType("password")
                        .text("One Time Code (Get on at /passcode)")
                        .build())
                    .ldapDiscoveryEnabled(false)
                    .build())
                .build();
        }
    }

    public static final class Delete extends AbstractUaaApiTest<DeleteIdentityZoneRequest, DeleteIdentityZoneResponse> {

        private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteIdentityZoneResponse> expectations() {
            return ScriptedSubscriber.<DeleteIdentityZoneResponse>create()
                .expectValue(DeleteIdentityZoneResponse.builder()
                    .createdAt(1463595919906L)
                    .description("Like the Twilight Zone but tastier.")
                    .id("twiglet-delete")
                    .lastModified(1463595919906L)
                    .name("The Twiglet Zone")
                    .subdomain("twiglet-delete")
                    .version(0)
                    .configuration(IdentityZoneConfiguration.builder()
                        .tokenPolicy(TokenPolicy.builder()
                            .accessTokenValidity(-1)
                            .jwtRevocable(false)
                            .keys(Collections.emptyMap())
                            .refreshTokenValidity(-1)
                            .build())
                        .samlConfiguration(SamlConfiguration.builder()
                            .assertionSigned(true)
                            .requestSigned(true)
                            .wantAssertionSigned(false)
                            .wantPartnerAuthenticationRequestSigned(false)
                            .assertionTimeToLive(600)
                            .build())
                        .links(Links.builder()
                            .logout(LogoutLink.builder()
                                .redirectUrl("/login")
                                .redirectParameterName("redirect")
                                .disableRedirectParameter(true)
                                .build())
                            .selfService(SelfServiceLink.builder()
                                .selfServiceLinksEnabled(true)
                                .signupLink("/create_account")
                                .resetPasswordLink("/forgot_password")
                                .build())
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("username")
                            .text("Email")
                            .fieldType("text")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("password")
                            .text("Password")
                            .fieldType("password")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("passcode")
                            .text("One Time Code (Get on at /passcode)")
                            .fieldType("password")
                            .build())
                        .ldapDiscoveryEnabled(false)
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/identity-zones/twiglet-delete")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/DELETE_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteIdentityZoneResponse> invoke(DeleteIdentityZoneRequest request) {
            return this.identityZones.delete(request);
        }

        @Override
        protected DeleteIdentityZoneRequest validRequest() {
            return DeleteIdentityZoneRequest.builder()
                .identityZoneId("twiglet-delete")
                .build();
        }
    }

    public static final class Get extends AbstractUaaApiTest<GetIdentityZoneRequest, GetIdentityZoneResponse> {

        private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetIdentityZoneResponse> expectations() {
            return ScriptedSubscriber.<GetIdentityZoneResponse>create()
                .expectValue(GetIdentityZoneResponse.builder()
                    .createdAt(1463595920104L)
                    .id("twiglet-get")
                    .lastModified(1463595920104L)
                    .name("The Twiglet Zone")
                    .subdomain("twiglet-get")
                    .version(0)
                    .configuration(IdentityZoneConfiguration.builder()
                        .tokenPolicy(TokenPolicy.builder()
                            .accessTokenValidity(-1)
                            .jwtRevocable(false)
                            .keys(Collections.emptyMap())
                            .refreshTokenValidity(-1)
                            .build())
                        .samlConfiguration(SamlConfiguration.builder()
                            .assertionSigned(true)
                            .requestSigned(true)
                            .wantAssertionSigned(false)
                            .wantPartnerAuthenticationRequestSigned(false)
                            .assertionTimeToLive(600)
                            .build())
                        .links(Links.builder()
                            .logout(LogoutLink.builder()
                                .redirectUrl("/login")
                                .redirectParameterName("redirect")
                                .disableRedirectParameter(true)
                                .build())
                            .selfService(SelfServiceLink.builder()
                                .selfServiceLinksEnabled(true)
                                .signupLink("/create_account")
                                .resetPasswordLink("/forgot_password")
                                .build())
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("username")
                            .text("Email")
                            .fieldType("text")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("password")
                            .text("Password")
                            .fieldType("password")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("passcode")
                            .text("One Time Code (Get on at /passcode)")
                            .fieldType("password")
                            .build())
                        .ldapDiscoveryEnabled(false)
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/identity-zones/twiglet-get")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetIdentityZoneResponse> invoke(GetIdentityZoneRequest request) {
            return this.identityZones.get(request);
        }

        @Override
        protected GetIdentityZoneRequest validRequest() {
            return GetIdentityZoneRequest.builder()
                .identityZoneId("twiglet-get")
                .build();
        }
    }

    public static final class List extends AbstractUaaApiTest<ListIdentityZonesRequest, ListIdentityZonesResponse> {

        private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListIdentityZonesResponse> expectations() {
            return ScriptedSubscriber.<ListIdentityZonesResponse>create()
                .expectValue(ListIdentityZonesResponse.builder()
                    .identityZone(IdentityZone.builder()
                        .createdAt(1463595916851L)
                        .id("twiglet-list-1")
                        .description("Like the Twilight Zone but tastier.")
                        .lastModified(1463595916851L)
                        .name("The Twiglet Zone")
                        .subdomain("twiglet-list-1")
                        .version(0)
                        .configuration(IdentityZoneConfiguration.builder()
                            .tokenPolicy(TokenPolicy.builder()
                                .accessTokenValidity(-1)
                                .jwtRevocable(false)
                                .keys(Collections.emptyMap())
                                .refreshTokenValidity(-1)
                                .build())
                            .samlConfiguration(SamlConfiguration.builder()
                                .assertionSigned(true)
                                .requestSigned(true)
                                .wantAssertionSigned(false)
                                .wantPartnerAuthenticationRequestSigned(false)
                                .assertionTimeToLive(600)
                                .build())
                            .links(Links.builder()
                                .logout(LogoutLink.builder()
                                    .redirectUrl("/login")
                                    .redirectParameterName("redirect")
                                    .disableRedirectParameter(true)
                                    .build())
                                .selfService(SelfServiceLink.builder()
                                    .selfServiceLinksEnabled(true)
                                    .signupLink("/create_account")
                                    .resetPasswordLink("/forgot_password")
                                    .build())
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("username")
                                .text("Email")
                                .fieldType("text")
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("password")
                                .text("Password")
                                .fieldType("password")
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("passcode")
                                .text("One Time Code (Get on at /passcode)")
                                .fieldType("password")
                                .build())
                            .ldapDiscoveryEnabled(false)
                            .build())
                        .build())
                    .identityZone(IdentityZone.builder()
                        .createdAt(1463595918196L)
                        .description("Like the Twilight Zone but tastier.")
                        .id("twiglet-list-2")
                        .lastModified(1463595918196L)
                        .name("The Twiglet Zone")
                        .subdomain("twiglet-list-2")
                        .version(0)
                        .configuration(IdentityZoneConfiguration.builder()
                            .tokenPolicy(TokenPolicy.builder()
                                .accessTokenValidity(-1)
                                .jwtRevocable(false)
                                .keys(Collections.emptyMap())
                                .refreshTokenValidity(-1)
                                .build())
                            .samlConfiguration(SamlConfiguration.builder()
                                .assertionSigned(true)
                                .requestSigned(true)
                                .wantAssertionSigned(false)
                                .wantPartnerAuthenticationRequestSigned(false)
                                .assertionTimeToLive(600)
                                .build())
                            .links(Links.builder()
                                .logout(LogoutLink.builder()
                                    .redirectUrl("/login")
                                    .redirectParameterName("redirect")
                                    .disableRedirectParameter(true)
                                    .build())
                                .selfService(SelfServiceLink.builder()
                                    .selfServiceLinksEnabled(true)
                                    .signupLink("/create_account")
                                    .resetPasswordLink("/forgot_password")
                                    .build())
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("username")
                                .text("Email")
                                .fieldType("text")
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("password")
                                .text("Password")
                                .fieldType("password")
                                .build())
                            .prompt(Prompt.builder()
                                .fieldName("passcode")
                                .text("One Time Code (Get on at /passcode)")
                                .fieldType("password")
                                .build())
                            .ldapDiscoveryEnabled(false)
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/identity-zones")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListIdentityZonesResponse> invoke(ListIdentityZonesRequest request) {
            return this.identityZones.list(request);
        }

        @Override
        protected ListIdentityZonesRequest validRequest() {
            return ListIdentityZonesRequest.builder().build();
        }
    }

    public static final class Update extends AbstractUaaApiTest<UpdateIdentityZoneRequest, UpdateIdentityZoneResponse> {

        private final ReactorIdentityZones identityZones = new ReactorIdentityZones(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<UpdateIdentityZoneResponse> expectations() {
            return ScriptedSubscriber.<UpdateIdentityZoneResponse>create()
                .expectValue(UpdateIdentityZoneResponse.builder()
                    .createdAt(1463595920023L)
                    .description("Like the Twilight Zone but not tastier.")
                    .id("twiglet-update")
                    .lastModified(1463595920023L)
                    .name("The Updated Twiglet Zone")
                    .subdomain("twiglet-update")
                    .version(1)
                    .configuration(IdentityZoneConfiguration.builder()
                        .tokenPolicy(TokenPolicy.builder()
                            .accessTokenValidity(-1)
                            .jwtRevocable(false)
                            .refreshTokenValidity(-1)
                            .key("exampleKeyId", Collections.singletonMap("signingKey", "upD4t3d.s1gNiNg.K3y/t3XT"))
                            .build())
                        .samlConfiguration(SamlConfiguration.builder()
                            .assertionSigned(true)
                            .requestSigned(true)
                            .wantAssertionSigned(false)
                            .wantPartnerAuthenticationRequestSigned(false)
                            .assertionTimeToLive(600)
                            .build())
                        .links(Links.builder()
                            .logout(LogoutLink.builder()
                                .redirectUrl("/login")
                                .redirectParameterName("redirect")
                                .disableRedirectParameter(true)
                                .build())
                            .selfService(SelfServiceLink.builder()
                                .selfServiceLinksEnabled(true)
                                .signupLink("/create_account")
                                .resetPasswordLink("/forgot_password")
                                .build())
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("username")
                            .fieldType("text")
                            .text("Email")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("password")
                            .fieldType("password")
                            .text("Password")
                            .build())
                        .prompt(Prompt.builder()
                            .fieldName("passcode")
                            .fieldType("password")
                            .text("One Time Code (Get on at /passcode)")
                            .build())
                        .ldapDiscoveryEnabled(false)
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/identity-zones/twiglet-update")
                    .payload("fixtures/uaa/identity-zones/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/identity-zones/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<UpdateIdentityZoneResponse> invoke(UpdateIdentityZoneRequest request) {
            return this.identityZones.update(request);
        }

        @Override
        protected UpdateIdentityZoneRequest validRequest() {
            return UpdateIdentityZoneRequest.builder()
                .description("Like the Twilight Zone but not tastier.")
                .identityZoneId("twiglet-update")
                .name("The Updated Twiglet Zone")
                .subdomain("twiglet-update")
                .version(1)
                .configuration(IdentityZoneConfiguration.builder()
                    .tokenPolicy(TokenPolicy.builder()
                        .accessTokenValidity(-1)
                        .jwtRevocable(false)
                        .refreshTokenValidity(-1)
                        .key("exampleKeyId", Collections.singletonMap("signingKey", "upD4t3d.s1gNiNg.K3y/t3XT"))
                        .build())
                    .samlConfiguration(SamlConfiguration.builder()
                        .assertionSigned(true)
                        .requestSigned(true)
                        .wantAssertionSigned(false)
                        .wantPartnerAuthenticationRequestSigned(false)
                        .assertionTimeToLive(600)
                        .build())
                    .links(Links.builder()
                        .logout(LogoutLink.builder()
                            .redirectUrl("/login")
                            .redirectParameterName("redirect")
                            .disableRedirectParameter(true)
                            .build())
                        .selfService(SelfServiceLink.builder()
                            .selfServiceLinksEnabled(true)
                            .signupLink("/create_account")
                            .resetPasswordLink("/forgot_password")
                            .build())
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("username")
                        .fieldType("text")
                        .text("Email")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("password")
                        .fieldType("password")
                        .text("Password")
                        .build())
                    .prompt(Prompt.builder()
                        .fieldName("passcode")
                        .fieldType("password")
                        .text("One Time Code (Get on at /passcode)")
                        .build())
                    .ldapDiscoveryEnabled(false)
                    .build())
                .build();
        }
    }

}
