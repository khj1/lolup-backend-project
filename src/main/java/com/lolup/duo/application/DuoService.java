package com.lolup.duo.application;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolup.duo.application.dto.DuoDto;
import com.lolup.duo.application.dto.DuoResponse;
import com.lolup.duo.application.dto.DuoSaveRequest;
import com.lolup.duo.application.dto.SummonerDto;
import com.lolup.duo.domain.Duo;
import com.lolup.duo.domain.DuoRepository;
import com.lolup.duo.domain.SummonerPosition;
import com.lolup.duo.domain.SummonerStat;
import com.lolup.duo.domain.SummonerTier;
import com.lolup.duo.exception.DuoCreationLimitException;
import com.lolup.duo.exception.DuoDeleteFailureException;
import com.lolup.duo.exception.DuoUpdateFailureException;
import com.lolup.duo.exception.NoSuchDuoException;
import com.lolup.member.domain.Member;
import com.lolup.member.domain.MemberRepository;
import com.lolup.member.exception.NoSuchMemberException;
import com.lolup.riot.match.application.MatchService;
import com.lolup.riot.match.application.dto.RecentMatchStatsDto;
import com.lolup.riot.riotstatic.RiotStaticService;
import com.lolup.riot.summoner.application.SummonerService;
import com.lolup.riot.summoner.application.dto.SummonerAccountDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DuoService {

	public static final int CREATION_LIMIT_MINUTES = 10;

	private final MatchService matchService;
	private final SummonerService summonerService;
	private final RiotStaticService riotStaticService;
	private final DuoRepository duoRepository;
	private final MemberRepository memberRepository;

	public DuoResponse findAll(final SummonerPosition position, final SummonerTier tier, final Pageable pageable) {
		Page<DuoDto> data = duoRepository.findAll(position, tier, pageable);

		return new DuoResponse(data, riotStaticService.getLatestGameVersion());
	}

	@Transactional
	public void save(final Long memberId, final DuoSaveRequest request, final LocalDateTime currentDate) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(NoSuchMemberException::new);

		LocalDateTime latestCreatedDate = duoRepository.findFirstCreatedDateByMemberId(memberId)
				.orElse(LocalDateTime.MIN);
		validateCreationLimit(latestCreatedDate, currentDate);

		SummonerDto summonerDto = requestSummonerDto(request.getSummonerName());

		Duo duo = Duo.create(
				member,
				summonerDto.getSummonerStat(),
				summonerDto.getChampionStats(),
				summonerDto.getLatestWinRate(),
				summonerDto.getProfileIconId(),
				request.getPosition(),
				request.getDesc()
		);
		duoRepository.save(duo);
	}

	private void validateCreationLimit(final LocalDateTime createdDate, final LocalDateTime currentDate) {
		if (isBeforeCreationLimit(createdDate, currentDate)) {
			throw new DuoCreationLimitException();
		}
	}

	private boolean isBeforeCreationLimit(final LocalDateTime createdDate, final LocalDateTime currentDate) {
		return currentDate.minusMinutes(CREATION_LIMIT_MINUTES).isBefore(createdDate);
	}

	private SummonerDto requestSummonerDto(final String summonerName) {
		SummonerAccountDto accountDto = summonerService.requestAccountInfo(summonerName);
		SummonerStat summonerStat = summonerService.requestSummonerStat(accountDto.getId(), accountDto.getName());
		RecentMatchStatsDto recentMatchStats = matchService.requestRecentMatchStats(accountDto.getPuuid());

		return new SummonerDto(
				accountDto.getProfileIconId(),
				recentMatchStats.getLatestWinRate(),
				summonerStat,
				recentMatchStats.getChampionStats());
	}

	@Transactional
	public void update(final Long memberId, final Long duoId, final SummonerPosition position, final String desc) {
		Duo duo = duoRepository.findById(duoId)
				.orElseThrow(NoSuchDuoException::new);
		validateAuthor(memberId, duo);
		duo.update(position, desc);
	}

	private void validateAuthor(final Long memberId, final Duo duo) {
		if (!duo.isAuthor(memberId)) {
			throw new DuoUpdateFailureException();
		}
	}

	@Transactional
	public void delete(final Long duoId, final Long memberId) {
		duoRepository.findByIdAndMemberId(duoId, memberId)
				.orElseThrow(DuoDeleteFailureException::new);

		duoRepository.deleteById(duoId);
	}
}
