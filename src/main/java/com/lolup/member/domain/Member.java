package com.lolup.member.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lolup.common.BaseTimeEntity;
import com.lolup.member.exception.InvalidEmailException;
import com.lolup.member.exception.InvalidMemberNameException;
import com.lolup.member.exception.InvalidSummonerNameException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	public static final int MIN_NAME_LENGTH = 1;
	public static final int MAX_NAME_LENGTH = 16;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String picture;

	private String summonerName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	public Member(final String name, final String email, final String picture, final SocialType socialType) {
		this(name, email, picture, null, socialType);
	}

	@Builder
	public Member(final String name, final String email, final String picture, final String summonerName,
				  final SocialType socialType) {
		validateName(name);
		validateEmail(email);

		this.name = name;
		this.email = email;
		this.picture = picture;
		this.summonerName = summonerName;
		this.socialType = socialType;
	}

	private void validateName(final String name) {
		if (name.isBlank() || MAX_NAME_LENGTH < name.length()) {
			throw new InvalidMemberNameException();
		}
	}

	private void validateEmail(final String email) {
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		if (!matcher.matches()) {
			throw new InvalidEmailException();
		}
	}

	public void update(final String name, final String email, final String picture) {
		validateName(name);
		validateEmail(email);

		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public void changeSummonerName(final String summonerName) {
		validateSummonerName(summonerName);

		this.summonerName = summonerName;
	}

	private void validateSummonerName(final String summonerName) {
		if (summonerName.isBlank() || MAX_NAME_LENGTH < summonerName.length()) {
			throw new InvalidSummonerNameException();
		}
	}

	public boolean isSameMember(final Long memberId) {
		return this.id.equals(memberId);
	}
}
