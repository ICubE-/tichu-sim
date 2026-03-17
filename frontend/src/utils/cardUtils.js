export const CardType = {
    STANDARD: 'STANDARD',
    SPARROW: 'SPARROW',
    PHOENIX: 'PHOENIX',
    DRAGON: 'DRAGON',
    DOG: 'DOG',
};

/**
 * Checks if two cards are equal based on their type, suit, and rank.
...
 */
export const areCardsEqual = (cardA, cardB) => {
    if (!cardA || !cardB) return cardA === cardB;
    return (
        cardA.type === cardB.type &&
        cardA.suit === cardB.suit &&
        cardA.rank === cardB.rank
    );
};

/**
 * Checks if a card list contains a specific card using value equality.
...
 */
export const includesCard = (cardList, card) => {
    if (!cardList || !card) return false;
    return cardList.some(c => areCardsEqual(c, card));
};

/**
 * Filters a card list by removing specific cards based on value equality.
...
 */
export const excludeCards = (cardList, cardsToRemove) => {
    if (!cardList) return [];
    const toRemove = Array.isArray(cardsToRemove) ? cardsToRemove : [cardsToRemove];
    return cardList.filter(card => !includesCard(toRemove, card));
};

export const getRank = (card) => {
    if (card.type === CardType.SPARROW) return 1;
    if (card.type === CardType.DRAGON) return 20;
    if (card.type === CardType.STANDARD) return card.rank;
    return 0; // Phoenix, Dog
};

export const isStandard = (card) => card.type === CardType.STANDARD;
export const isPhoenix = (card) => card.type === CardType.PHOENIX;
export const isSparrow = (card) => card.type === CardType.SPARROW;
export const isDog = (card) => card.type === CardType.DOG;
export const isDragon = (card) => card.type === CardType.DRAGON;

export const sortCards = (cards) => {
    return [...cards].sort((a, b) => getRank(a) - getRank(b));
};

export const extractStandardCards = (cards) => cards.filter(isStandard);
export const extractNonPhoenixCards = (cards) => cards.filter(c => !isPhoenix(c));
