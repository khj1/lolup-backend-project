package com.lolup.lolup_project.riotapi.summoner;

import lombok.Data;

@Data
public class SummonerRankInfoDto {
    private int iconId;
    private String summonerName;
    private String tier;
    private String rank;
    private int wins;
    private int losses;
}
