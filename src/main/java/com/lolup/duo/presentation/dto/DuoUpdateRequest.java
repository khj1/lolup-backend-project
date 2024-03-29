package com.lolup.duo.presentation.dto;

import com.lolup.duo.domain.SummonerPosition;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuoUpdateRequest {

	@NotNull(message = "포지션은 공백일 수 없습니다.")
	private SummonerPosition position;
	private String desc;

	public DuoUpdateRequest(final SummonerPosition position, final String desc) {
		this.position = position;
		this.desc = desc;
	}
}
