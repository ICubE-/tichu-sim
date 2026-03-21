export const CardType = {
    STANDARD: 'STANDARD',
    SPARROW: 'SPARROW',
    PHOENIX: 'PHOENIX',
    DRAGON: 'DRAGON',
    DOG: 'DOG',
};

const TypePriority = {
    [CardType.SPARROW]: 0,
    [CardType.STANDARD]: 1,
    [CardType.PHOENIX]: 2,
    [CardType.DRAGON]: 3,
    [CardType.DOG]: 4,
};

const SuitPriority = {
    'SPADE': 0,
    'CLUB': 1,
    'HEART': 2,
    'DIAMOND': 3,
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
    if (card.effectiveRank !== undefined) return card.effectiveRank;
    if (card.type === CardType.SPARROW) return 1;
    if (card.type === CardType.DRAGON) return 20;
    if (card.type === CardType.STANDARD) return card.rank;
    return 0; // Phoenix, Dog (default)
};

export const isStandard = (card) => card.type === CardType.STANDARD;
export const isPhoenix = (card) => card.type === CardType.PHOENIX;
export const isSparrow = (card) => card.type === CardType.SPARROW;
export const isDog = (card) => card.type === CardType.DOG;
export const isDragon = (card) => card.type === CardType.DRAGON;

export const sortCards = (cards) => {
    return [...cards].sort((a, b) => {
        // 1. Type Priority (Sparrow < Standard < Phoenix/Dragon/Dog)
        // Note: If card has effectiveRank, it should be treated as STANDARD for sorting purposes in tricks
        const typeA = a.effectiveRank !== undefined ? CardType.STANDARD : a.type;
        const typeB = b.effectiveRank !== undefined ? CardType.STANDARD : b.type;

        if (TypePriority[typeA] !== TypePriority[typeB]) {
            return TypePriority[typeA] - TypePriority[typeB];
        }

        // 2. Rank Priority
        const rankA = getRank(a);
        const rankB = getRank(b);
        if (rankA !== rankB) {
            return rankA - rankB;
        }

        // 3. Suit Priority (Spade < Club < Heart < Diamond)
        const suitA = SuitPriority[a.suit] ?? 99;
        const suitB = SuitPriority[b.suit] ?? 99;
        return suitA - suitB;
    });
};

export const extractStandardCards = (cards) => cards.filter(isStandard);
export const extractNonPhoenixCards = (cards) => cards.filter(c => !isPhoenix(c));
