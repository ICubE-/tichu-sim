package com.icube.sim.tichu.game;

import com.icube.sim.tichu.game.cards.Card;
import com.icube.sim.tichu.rooms.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class Player {
    private final Long id;
    private final String name;
    private final Team team;

    @Setter
    private List<Card> hand;

    public Player(Member member, Team team) {
        this.id = member.getId();
        this.name = member.getName();
        this.team = team;
        this.hand = null;
    }

}
