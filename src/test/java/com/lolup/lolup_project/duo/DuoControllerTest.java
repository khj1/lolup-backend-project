package com.lolup.lolup_project.duo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolup.lolup_project.config.SecurityConfig;
import com.lolup.lolup_project.riotapi.summoner.MostInfo;
import com.lolup.lolup_project.riotapi.summoner.MostInfoDto;
import com.lolup.lolup_project.riotapi.summoner.SummonerPosition;
import com.lolup.lolup_project.riotapi.summoner.SummonerTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(value = DuoController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class DuoControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DuoService duoService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).uris()
                        .withHost("lolup-api.p-e.kr")
                        .withPort(80)
                )
                .build();
    }

    @Test
    void ?????????_??????() throws Exception{
        //given
        DuoForm duoForm = getDuoForm();

        //when
        when(duoService.save(duoForm)).thenReturn(duoForm.getMemberId());

        ResultActions result = mockMvc.perform(
                post("/duo/new")
                        .content(objectMapper.writeValueAsString(duoForm))
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        result.andExpect(status().isOk())
                .andDo(document("duo/create",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("summonerName").description("??? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("memberId").type("Long").description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("position").description("??? ?????????"),
                                fieldWithPath("desc").description("????????? ????????? ?????? ????????? ????????? ????????? ??? ????????????."),
                                fieldWithPath("postDate").type("LocalDateTime").description("????????? ?????? ??????")
                        )
                ));
    }


    private DuoForm getDuoForm() {
        return DuoForm.builder()
                .position(SummonerPosition.MID)
                .summonerName("hideonbush")
                .desc("hi")
                .postDate(LocalDateTime.now())
                .memberId(1L)
                .build();
    }

    @Test
    void ?????????_??????_??????() throws Exception{
        //given
        Map<String, Object> map = getDuoMap();

        //when
        when(duoService.findAll(Position.MID.toString(), SummonerTier.PLATINUM, PageRequest.of(0, 20))).thenReturn(map);

        ResultActions result = mockMvc.perform(
                get("/duo")
                        .param("position", Position.MID.toString())
                        .param("tier", SummonerTier.PLATINUM)
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("duo/readAll",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("position").description("??? ?????????. ?????? ????????? ??????????????? ?????? ???????????????.").optional(),
                                parameterWithName("tier").description("?????? ??????. ?????? ????????? ??????????????? ?????? ???????????????.").optional(),
                                parameterWithName("page").description("?????? ?????????"),
                                parameterWithName("size").description("??? ???????????? ????????? ????????? ??????(???????????? default ???, 20)")
                        ),
                        responseFields(
                                fieldWithPath("totalCount").description("DB??? ????????? ??? ?????? ????????? ???"),
                                fieldWithPath("version").description("?????? ?????? ??????"),
                                subsectionWithPath("data").type("List<DuoDto>").description("????????? ????????? ?????? ?????????"),
                                subsectionWithPath("pageable").description("????????? ????????? ????????? ?????????")
                        ),
                        responseFields(beneathPath("data"),
                                fieldWithPath("duoId").type("Long").description("????????? ???????????? ?????? ??????"),
                                fieldWithPath("memberId").type("Long").description("???????????? ?????? ?????? ??????"),
                                fieldWithPath("iconId").type("int").description("????????? ????????? ??????"),
                                fieldWithPath("summonerName").description("??? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("position").description("??? ?????????"),
                                fieldWithPath("tier").description("?????? ??????"),
                                fieldWithPath("rank").description("?????? ??????"),
                                subsectionWithPath("most3").type("List<MostInfo>").description("?????? 10 ???????????? ?????? ?????? ???????????? ????????????"),
                                fieldWithPath("wins").type("int").description("??? ?????? ??????"),
                                fieldWithPath("losses").type("int").description("??? ?????? ??????"),
                                fieldWithPath("latestWinRate").description("?????? 10 ????????? ??????"),
                                fieldWithPath("desc").description("????????? ????????? ?????? ????????? ????????? ????????? ??? ????????????."),
                                fieldWithPath("postDate").type("LocalDateTime").description("????????? ?????? ??????")
                        )
//                        responseFields(beneathPath("pageable"))
                ));
    }

    private Map<String, Object> getDuoMap() {
        DuoDto duoDto1 = getDuoDto();
        DuoDto duoDto2 = getDuoDto();
        List<DuoDto> data = new ArrayList<>();
        data.add(duoDto1);
        data.add(duoDto2);

        Map<String, Object> map = new HashMap<>();
        map.put("version", "11.16.0");
        map.put("totalCount", 100);
        map.put("data", data);
        map.put("pageable", PageRequest.of(0, 20));

        return map;
    }

    private DuoDto getDuoDto() {
        return DuoDto.builder()
                .iconId(100)
                .duoId(2L)
                .latestWinRate("20%")
                .losses(300)
                .most3(getMost3().stream().map(MostInfoDto::create).collect(Collectors.toList()))
                .rank("IV")
                .tier(SummonerTier.BRONZE)
                .desc("hi")
                .wins(400)
                .memberId(1L)
                .postDate(LocalDateTime.now())
                .summonerName("hideonbush")
                .position(SummonerPosition.MID)
                .build();
    }

    private List<MostInfo> getMost3() {
        List<MostInfo> most3 = new ArrayList<>();

        most3.add(MostInfo.create("Syndra", 4));
        most3.add(MostInfo.create("Lucian", 3));
        most3.add(MostInfo.create("Zed", 2));

        return most3;
    }

    @Test
    void ?????????_??????() throws Exception{
        //given
        Long duoId = 1L;
        Long memberId = 1L;

        //when
        when(duoService.delete(duoId, memberId)).thenReturn(1L);

        ResultActions result = mockMvc.perform(
                delete("/duo/{duoId}", 1L)
                        .param("memberId", String.valueOf(memberId))
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("duo/delete",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("memberId").description("???????????? ????????? memberId??? ???????????????. duoId??? ???????????? memberId??? ??????????????? ?????? ???????????? ????????????.")
                        )
                ));
    }
}