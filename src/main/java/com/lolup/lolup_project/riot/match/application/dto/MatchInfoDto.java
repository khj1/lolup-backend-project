package com.lolup.lolup_project.riot.match.application.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchInfoDto {

	private List<ParticipantDto> participants;

	public MatchInfoDto(final List<ParticipantDto> participants) {
		this.participants = participants;
	}
}