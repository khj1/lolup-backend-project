package com.lolup.lolup_project.member;

import com.lolup.lolup_project.base.BaseTimeEntity;
import com.lolup.lolup_project.message.Message;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;
    private String email;
    private String picture;
    private String summonerName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Message> messages;

    public Member(String name, String email, Role role, String picture) {
        this(null, name, email, role, picture, null);
    }

    @Builder
    public Member(Long id, String name, String email, Role role, String picture, String summonerName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.summonerName = summonerName;
        this.role = role;
    }

    public Member update(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;

        return this;
    }

    public static UserProfile toUserProfile(Member member) {
        return new UserProfile(
                member.getName(),
                member.getEmail(),
                member.getPicture()
        );
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void changeSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }
}
