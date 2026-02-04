package com.icube.sim.tichu.game.mappers;

import com.icube.sim.tichu.game.cards.*;
import com.icube.sim.tichu.game.dtos.CardDto;
import com.icube.sim.tichu.game.dtos.CardType;

import java.util.List;

public class CardMapper {
    public CardDto toDto(Card card) {
        if (card instanceof StandardCard standardCard) {
            return new CardDto(CardType.STANDARD, standardCard.suit(), standardCard.rank());
        } else if (card instanceof SparrowCard) {
            return new CardDto(CardType.SPARROW);
        } else if (card instanceof PhoenixCard) {
            return new CardDto(CardType.PHOENIX);
        } else if (card instanceof DragonCard) {
            return new CardDto(CardType.DRAGON);
        } else if (card instanceof DogCard) {
            return new CardDto(CardType.DOG);
        } else {
            throw new IllegalArgumentException("Invalid card type.");
        }
    }

    public Card toCard(CardDto cardDto) {
        return switch (cardDto.getType()) {
            case STANDARD -> new StandardCard(cardDto.getSuit(), cardDto.getRank());
            case SPARROW -> new SparrowCard();
            case PHOENIX -> new PhoenixCard();
            case DRAGON -> new DragonCard();
            case DOG -> new DogCard();
        };
    }

    public List<CardDto> toDtos(List<Card> cards) {
        return cards.stream().map(this::toDto).toList();
    }

    public List<Card> toCards(List<CardDto> cardDtos) {
        return cardDtos.stream().map(this::toCard).toList();
    }
}

