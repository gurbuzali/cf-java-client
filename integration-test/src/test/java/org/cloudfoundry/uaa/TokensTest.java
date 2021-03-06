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
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.AbstractTokenKey;
import org.cloudfoundry.uaa.tokens.CheckTokenRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordResponse;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.RefreshTokenRequest;
import org.cloudfoundry.uaa.tokens.RefreshTokenResponse;
import org.cloudfoundry.uaa.tokens.TokenFormat;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpException;
import reactor.util.function.Tuple2;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class TokensTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private String clientSecret;

    @Autowired
    private ConnectionContext connectionContext;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void checkTokenNotAuthorized() {
        this.tokenProvider.getToken(this.connectionContext)
            .then(token -> this.uaaClient.tokens()
                .check(CheckTokenRequest.builder()
                    .token(token)
                    .clientId(this.clientId)
                    .clientSecret(this.clientSecret)
                    .scope("password.write")
                    .scope("scim.userids")
                    .build()))
            .subscribe(this.testSubscriber()
                .expectError(HttpException.class, "HTTP request failed with code: 403"));
    }

    @Ignore("TODO: use test authorizationCode")
    @Test
    public void getTokenByAuthorizationCode() {
        this.uaaClient.tokens()
            .getByAuthorizationCode(GetTokenByAuthorizationCodeRequest.builder()
                .authorizationCode("some auth code")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .build())
            .subscribe(this.<GetTokenByAuthorizationCodeResponse>testSubscriber()
                .expectCount(1));
    }

    @Test
    public void getTokenByClientCredentials() {
        this.uaaClient.tokens()
            .getByClientCredentials(GetTokenByClientCredentialsRequest.builder()
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .subscribe(this.<GetTokenByClientCredentialsResponse>testSubscriber()
                .expectThat(response -> assertEquals("bearer", response.getTokenType())));
    }

    @Ignore("TODO: use test one-time passcode")
    @Test
    public void getTokenByOneTimePasscode() {
        this.uaaClient.tokens()
            .getByOneTimePasscode(GetTokenByOneTimePasscodeRequest.builder()
                .passcode("Some passcode")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .subscribe(this.<GetTokenByOneTimePasscodeResponse>testSubscriber()
                .expectThat(response -> assertEquals("bearer", response.getTokenType())));
    }

    @Ignore("TODO: use test openid authorizationCode")
    @Test
    public void getTokenByOpenId() {
        this.uaaClient.tokens()
            .getByOpenId(GetTokenByOpenIdRequest.builder()
                .authorizationCode("Some authorization code")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .subscribe(this.<GetTokenByOpenIdResponse>testSubscriber()
                .expectThat(response -> assertEquals("bearer", response.getTokenType())));
    }

    @Ignore("TODO: use test username and password")
    @Test
    public void getTokenByPassword() {
        this.uaaClient.tokens()
            .getByPassword(GetTokenByPasswordRequest.builder()
                .password("a-password")
                .username("a-username")
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .tokenFormat(TokenFormat.OPAQUE)
                .build())
            .subscribe(this.<GetTokenByPasswordResponse>testSubscriber()
                .expectThat(response -> assertEquals("bearer", response.getTokenType())));
    }

    @Test
    public void getTokenKey() {
        this.uaaClient.tokens()
            .getKey(GetTokenKeyRequest.builder()
                .build())
            .subscribe(this.<GetTokenKeyResponse>testSubscriber()
                .expectThat(response -> {
                    assertEquals("sig", response.getUse());
                    assertNotNull(response.getValue());
                }));
    }

    @Test
    public void listTokenKeys() {
        this.uaaClient.tokens()
            .getKey(GetTokenKeyRequest.builder()
                .build())
            .then(getKey -> Mono
                .when(
                    this.uaaClient.tokens()
                        .listKeys(ListTokenKeysRequest.builder()
                            .build())
                        .flatMap(response -> Flux.fromIterable(response.getKeys()))
                        .filter(tokenKey -> getKey.getValue().equals(tokenKey.getValue()))
                        .single(),
                    Mono.just(getKey)
                ))
            .subscribe(this.<Tuple2<TokenKey, GetTokenKeyResponse>>testSubscriber()
                .expectThat(consumer(TokensTest::assertTokenKeyEquality)));
    }

    @Ignore("TODO: use test refresh token")
    @Test
    public void refreshToken() {
        this.uaaClient.tokens()
            .refresh(RefreshTokenRequest.builder()
                .tokenFormat(TokenFormat.OPAQUE)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .refreshToken("a-refresh-token")
                .build())
            .subscribe(this.<RefreshTokenResponse>testSubscriber()
                .expectCount(1));
    }

    private static void assertTokenKeyEquality(AbstractTokenKey tk1, AbstractTokenKey tk2) {
        assertEquals("algorithm", tk1.getAlgorithm(), tk2.getAlgorithm());
        assertEquals("e", tk1.getE(), tk2.getE());
        assertEquals("keyType", tk1.getKeyType(), tk2.getKeyType());
        assertEquals("id", tk1.getId(), tk2.getId());
        assertEquals("n", tk1.getN(), tk2.getN());
        assertEquals("use", tk1.getUse(), tk2.getUse());
        assertEquals("value", tk1.getValue(), tk2.getValue());
    }
}
