package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.rooms.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Player {
    @Getter
    private final Long id;
    @Getter
    private final String name;
    @Getter
    private final Team team;
    private List<Card> hand;

    public Player(Member member, Team team) {
        this.id = member.getId();
        this.name = member.getName();
        this.team = team;
        this.hand = null;
    }

    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    public boolean hasCard(Card card) {
        return hand.contains(card);
    }

    public void initFirstDraws(List<Card> cards) {
        assert cards.size() == 8;
        hand = new ArrayList<>(cards);
    }

    public void addSecondDraws(List<Card> cards) {
        assert cards.size() == 6;
        hand.addAll(cards);
    }

    public void exchange(List<Card> cardsGave, List<Card> cardsReceived) {
        hand.removeAll(cardsGave);
        hand.addAll(cardsReceived);
    }
}
