package com.lolup.lolup_project.riotapi.summoner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WithMockUser
@WebFluxTest(value = SummonerController.class)
class SummonerControllerTest {

    private WebTestClient webTestClient;

    @MockBean
    SummonerService service;

    @BeforeEach
    public void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation){
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("http://lolup-api.p-e.kr/")
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void ?????????_????????????_?????????() throws Exception {

        SummonerAccountDto dto = SummonerAccountDto.builder()
                .accountId("accountId")
                .profileIconId(100)
                .id("id")
                .puuid("puuid")
                .summonerLevel(100L)
                .name("summonerName")
                .build();


        when(service.getAccountInfo("summonerName")).thenReturn(dto);

        webTestClient.get().uri("/riot/find/{summonerName}", "summonerName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("summoner/success",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("summonerName").description("??? ???????????? ???????????? ????????? ????????? ???????????????.")
                        ),
                        responseFields(
                                fieldWithPath("summonerName").type("String").description("API??? ????????? ???????????? ????????? ????????? ???????????????.")
                        )
                ))
                .jsonPath("$.summonerName").isEqualTo("summonerName");

    }

    @Test
    public void ?????????_?????????_??????_??????() throws Exception {
        //when
        when(service.getAccountInfo("wrongName")).thenThrow(WebClientResponseException.class);

        //then
        webTestClient.get().uri("/riot/find/{summonerName}", "wrongName")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(document("summoner/exception",
                        preprocessResponse(prettyPrint())
                ))
                .jsonPath("$.code").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("?????? ???????????? ???????????? ????????????.");

    }
}