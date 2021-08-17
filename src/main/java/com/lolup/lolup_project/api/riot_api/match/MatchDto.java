package com.lolup.lolup_project.api.riot_api.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchDto {
    List<TeamStatsDto> teams;
    List<ParticipantDto> participants;
}