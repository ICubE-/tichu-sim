import {
  getRank,
  isStandard,
  isPhoenix,
  isSparrow,
  isDog,
  isDragon,
  sortCards,
  extractStandardCards, CardType,
} from './cardUtils.js';

export const TrickType = {
  SINGLE: 'SINGLE',
  PAIR: 'PAIR',
  CONSECUTIVE_PAIRS: 'CONSECUTIVE_PAIRS',
  THREE_OF_A_KIND: 'THREE_OF_A_KIND',
  FULL_HOUSE: 'FULL_HOUSE',
  STRAIGHT: 'STRAIGHT',
  DOG: 'DOG',
  FOUR_OF_A_KIND: 'FOUR_OF_A_KIND',
  STRAIGHT_FLUSH: 'STRAIGHT_FLUSH',
};

export const isBomb = (trickType) => {
  return trickType === TrickType.FOUR_OF_A_KIND || trickType === TrickType.STRAIGHT_FLUSH;
};

export const identifyTrick = (cards, lastTrick) => {
  if (!cards || cards.length === 0) return null;

  const size = cards.length;
  const standardCards = sortCards(extractStandardCards(cards));
  const hasPhoenix = cards.some(isPhoenix);
  const hasSparrow = cards.some(isSparrow);

  // SINGLE
  if (size === 1) {
    const card = cards[0];
    if (isDog(card)) return { type: TrickType.DOG, label: 'Dog' };
    let rank = getRank(card);
    let label = `Single ${rank}`;
    if (isDragon(card)) label = 'Dragon';
    if (isSparrow(card)) label = 'Sparrow (1)';
    if (isPhoenix(card)) {
      if (lastTrick === null) {
        rank = 1.5;
        label = 'Phoenix';
      } else if (lastTrick.type !== TrickType.SINGLE) {
        return null;
      } else {
        rank = Math.min(lastTrick.rank + 0.5, 15);
        label = 'Phoenix';
      }
    }
    return { type: TrickType.SINGLE, rank, label };
  }

  // PAIR
  if (size === 2) {
    if (hasPhoenix) {
      if (standardCards.length === 1) {
        const rank = standardCards[0].rank;
        return { type: TrickType.PAIR, rank, label: `Pair of ${rank}s (with Phoenix)` };
      }
    } else {
      if (standardCards.length === 2 && standardCards[0].rank === standardCards[1].rank) {
        const rank = standardCards[0].rank;
        return { type: TrickType.PAIR, rank, label: `Pair of ${rank}s` };
      }
    }
  }

  // THREE OF A KIND
  if (size === 3) {
    if (hasPhoenix) {
      if (standardCards.length === 2 && standardCards[0].rank === standardCards[1].rank) {
        const rank = standardCards[0].rank;
        return { type: TrickType.THREE_OF_A_KIND, rank, label: `Three of a Kind ${rank}s (with Phoenix)` };
      }
    } else {
      if (standardCards.length === 3 && standardCards[0].rank === standardCards[1].rank && standardCards[1].rank === standardCards[2].rank) {
        const rank = standardCards[0].rank;
        return { type: TrickType.THREE_OF_A_KIND, rank, label: `Three of a Kind ${rank}s` };
      }
    }
  }

  // FOUR OF A KIND (BOMB)
  if (size === 4 && !hasPhoenix) {
    if (standardCards.length === 4 &&
      standardCards[0].rank === standardCards[1].rank &&
      standardCards[1].rank === standardCards[2].rank &&
      standardCards[2].rank === standardCards[3].rank) {
      const rank = standardCards[0].rank;
      return { type: TrickType.FOUR_OF_A_KIND, rank, label: `Bomb! Four of a Kind ${rank}s`, isBomb: true };
    }
  }

  // FULL HOUSE
  if (size === 5) {
    // No Phoenix
    if (!hasPhoenix && standardCards.length === 5) {
      if (standardCards[0].rank === standardCards[1].rank && standardCards[3].rank === standardCards[4].rank) {
        if (standardCards[1].rank === standardCards[2].rank) {
          return {
            type: TrickType.FULL_HOUSE,
            rank: standardCards[0].rank,
            label: `Full House ${standardCards[0].rank} over ${standardCards[4].rank}`
          };
        } else if (standardCards[2].rank === standardCards[3].rank) {
          return {
            type: TrickType.FULL_HOUSE,
            rank: standardCards[4].rank,
            label: `Full House ${standardCards[4].rank} over ${standardCards[0].rank}`
          };
        }
      }
    }
    // With Phoenix
    if (hasPhoenix) {
      if (standardCards.length === 4) {
        // Reject 2222P (that's 4 of a kind bomb + phoenix, not allowed as FH in this logic)
        if (standardCards[0].rank !== standardCards[3].rank) {
          if (standardCards[0].rank === standardCards[2].rank) {
            return {
              type: TrickType.FULL_HOUSE,
              rank: standardCards[0].rank,
              label: `Full House ${standardCards[0].rank}s (with Phoenix)`
            };
          } else if (standardCards[1].rank === standardCards[3].rank) {
            return {
              type: TrickType.FULL_HOUSE,
              rank: standardCards[3].rank,
              label: `Full House ${standardCards[3].rank}s (with Phoenix)`
            };
          } else if (standardCards[0].rank === standardCards[1].rank && standardCards[2].rank === standardCards[3].rank) {
            // 22 33 P -> rank 3 (highest of the two pairs becomes the triple)
            return {
              type: TrickType.FULL_HOUSE,
              rank: standardCards[3].rank,
              label: `Full House ${standardCards[3].rank}s over ${standardCards[0].rank} (with Phoenix)`
            };
          }
        }
      }
    }
  }

  // CONSECUTIVE PAIRS
  if (size >= 4 && size % 2 === 0) {
    if (!hasPhoenix) {
      if (standardCards.length === size) {
        let valid = true;
        let startRank = standardCards[0].rank;
        for (let i = 0; i < size; i += 2) {
          if (standardCards[i].rank !== startRank + i / 2 || standardCards[i + 1].rank !== startRank + i / 2) {
            valid = false;
            break;
          }
        }
        if (valid) return {
          type: TrickType.CONSECUTIVE_PAIRS,
          minRank: startRank,
          maxRank: startRank + size / 2 - 1,
          length: size / 2,
          label: `Consecutive Pairs ${startRank}-${startRank + size / 2 - 1}`
        };
      }
    } else {
      // Phoenix used in consecutive pairs
      if (standardCards.length === size - 1) {
        let expectedRank = standardCards[0].rank;
        let count = 0;
        let usedPhoenix = false;
        let valid = true;
        for (const card of standardCards) {
          if (count === 0) {
            if (card.rank !== expectedRank) {
              valid = false;
              break;
            }
            count = 1;
          } else {
            if (card.rank === expectedRank) {
              expectedRank++;
              count = 0;
            } else {
              if (usedPhoenix || card.rank !== expectedRank + 1) {
                valid = false;
                break;
              }
              expectedRank++;
              usedPhoenix = true;
              // This card is actually the FIRST of the next pair
              count = 1;
            }
          }
        }
        if (valid) return {
          type: TrickType.CONSECUTIVE_PAIRS,
          minRank: standardCards[0].rank,
          maxRank: standardCards[standardCards.length - 1].rank,
          length: size / 2,
          label: `Consecutive Pairs ${standardCards[0].rank}-${standardCards[standardCards.length - 1].rank} (with Phoenix)`
        };
      }
    }
  }

  // STRAIGHT / STRAIGHT FLUSH
  if (size >= 5) {
    const isFlush = !hasPhoenix && standardCards.length === size && standardCards.every(c => c.suit === standardCards[0].suit);

    if (!hasPhoenix) {
      if (!hasSparrow) {
        if (standardCards.length === size) {
          let valid = true;
          let startRank = standardCards[0].rank;
          for (let i = 0; i < size; i++) {
            if (standardCards[i].rank !== startRank + i) {
              valid = false;
              break;
            }
          }
          if (valid) {
            if (isFlush) return {
              type: TrickType.STRAIGHT_FLUSH,
              minRank: startRank,
              maxRank: startRank + size - 1,
              length: size,
              label: `Bomb! Straight Flush ${startRank}-${startRank + size - 1}`,
              isBomb: true
            };
            return {
              type: TrickType.STRAIGHT,
              minRank: startRank,
              maxRank: startRank + size - 1,
              length: size,
              label: `Straight ${startRank}-${startRank + size - 1}`
            };
          }
        }
      } else if (standardCards.length === size - 1) {
        // 1 2 3 4 5
        let expectedRank = 2;
        let valid = true;
        for (const card of standardCards) {
          if (card.rank !== expectedRank) {
            valid = false;
            break;
          }
          expectedRank++;
        }
        if (valid) return {
          type: TrickType.STRAIGHT,
          minRank: 1,
          maxRank: size,
          length: size,
          label: `Straight 1-${size}`
        };
      }
    } else {
      // Straight with Phoenix
      if (!hasSparrow) {
        if (standardCards.length === size - 1) {
          let expectedRank = standardCards[0].rank;
          let usedPhoenix = false;
          let valid = true;
          for (const card of standardCards) {
            if (card.rank === expectedRank) {
              expectedRank++;
            } else {
              if (usedPhoenix || card.rank !== expectedRank + 1) {
                valid = false;
                break;
              }
              expectedRank += 2;
              usedPhoenix = true;
            }
          }
          if (valid) {
            let min = standardCards[0].rank;
            let max = expectedRank - 1;
            if (!usedPhoenix) {
              if (standardCards[standardCards.length - 1].rank === 14) {
                min--;
              } else {
                max++;
              }
            }
            return {
              type: TrickType.STRAIGHT,
              minRank: min,
              maxRank: max,
              length: size,
              label: `Straight ${min}-${max} (with Phoenix)`
            };
          }
        }
      } else if (standardCards.length === size - 2) {
        // P 1 2 4 5 etc
        let expectedRank = 2;
        let usedPhoenix = false;
        let valid = true;
        for (const card of standardCards) {
          if (card.rank === expectedRank) {
            expectedRank++;
          } else {
            if (usedPhoenix || card.rank !== expectedRank + 1) {
              valid = false;
              break;
            }
            expectedRank += 2;
            usedPhoenix = true;
          }
        }
        if (valid) {
          let max = usedPhoenix ? standardCards[standardCards.length - 1].rank : standardCards[standardCards.length - 1].rank + 1;
          return {
            type: TrickType.STRAIGHT,
            minRank: 1,
            maxRank: max,
            length: size,
            label: `Straight 1-${max} (with Phoenix)`
          };
        }
      }
    }
  }

  return null;
};

export const appendTrickInfo = (trick, prevTrick) => {
  const standardCards = sortCards(extractStandardCards(trick.cards));
  let resultCards = [...trick.cards];

  const updatePhoenixRank = (rank) => {
    resultCards = resultCards.map(c => isPhoenix(c) ? { ...c, effectiveRank: rank } : c);
  };

  switch (trick.type) {
    case TrickType.SINGLE:
      const card = trick.cards[0];
      switch (card.type) {
        case CardType.STANDARD:
          return { ...trick, rank: card.rank };
        case CardType.SPARROW:
          return { ...trick, rank: 1 };
        case CardType.PHOENIX:
          let pRank;
          if (prevTrick === null) {
            pRank = 1.5;
          } else {
            pRank = appendTrickInfo(prevTrick).rank + 0.5;
          }
          updatePhoenixRank(pRank);
          return { ...trick, rank: pRank, cards: resultCards };
        case CardType.DRAGON:
          return { ...trick, rank: 20 };
        case CardType.DOG:
        default:
          return null;
      }
    case TrickType.PAIR:
      const pairRank = standardCards[0].rank;
      updatePhoenixRank(pairRank);
      return { ...trick, rank: pairRank, cards: resultCards };
    case TrickType.CONSECUTIVE_PAIRS: {
      const minRank = standardCards[0].rank;
      const maxRank = standardCards[standardCards.length - 1].rank;
      // Assign effective rank to phoenix if it completes a pair
      if (trick.cards.some(isPhoenix)) {
        let expectedRank = minRank;
        let count = 0;
        let pRank = null;
        for (const c of standardCards) {
          if (c.rank === expectedRank) {
            count++;
            if (count === 2) {
              expectedRank++;
              count = 0;
            }
          } else {
            pRank = expectedRank;
            break;
          }
        }
        if (pRank === null) pRank = maxRank; // Should not happen if identifyTrick is correct
        updatePhoenixRank(pRank);
      }
      return { ...trick, minRank, maxRank, length: maxRank - minRank + 1, cards: resultCards };
    }
    case TrickType.THREE_OF_A_KIND:
      const threeRank = standardCards[0].rank;
      updatePhoenixRank(threeRank);
      return { ...trick, rank: threeRank, cards: resultCards };
    case TrickType.FULL_HOUSE:
      let tripleRank;
      if (trick.cards.some(isPhoenix)) {
        if (standardCards[0].rank === standardCards[2].rank) {
          tripleRank = standardCards[0].rank;
        } else if (standardCards[1].rank === standardCards[3].rank) {
          tripleRank = standardCards[1].rank;
        } else {
          // 22 33 P -> triple is highest pair rank
          tripleRank = standardCards[3].rank;
        }
        updatePhoenixRank(tripleRank);
      } else {
        tripleRank = standardCards[2].rank;
      }
      return { ...trick, rank: tripleRank, cards: resultCards };
    case TrickType.STRAIGHT:
      if (trick.cards.some(isPhoenix)) {
        if (trick.cards.some(isSparrow)) {
          let expectedRank = 2;
          let pRank = null;
          for (const c of standardCards) {
            if (c.rank !== expectedRank) {
              pRank = expectedRank;
              break;
            }
            expectedRank++;
          }
          const maxRank = pRank !== null ? standardCards[standardCards.length - 1].rank : standardCards[standardCards.length - 1].rank + 1;
          if (pRank === null) pRank = maxRank;
          updatePhoenixRank(pRank);
          return { ...trick, minRank: 1, maxRank, length: maxRank, cards: resultCards };
        } else {
          let expectedRank = standardCards[0].rank;
          let pRank = null;
          for (const c of standardCards) {
            if (c.rank !== expectedRank) {
              pRank = expectedRank;
              break;
            }
            expectedRank++;
          }
          let minRank = standardCards[0].rank;
          let maxRank = standardCards[standardCards.length - 1].rank;
          if (pRank === null) {
            if (maxRank === 14) {
              minRank--;
              pRank = minRank;
            } else {
              maxRank++;
              pRank = maxRank;
            }
          }
          updatePhoenixRank(pRank);
          return { ...trick, minRank, maxRank, length: maxRank - minRank + 1, cards: resultCards };
        }
      } else {
        if (trick.cards.some(isSparrow)) {
          const maxRank = standardCards[standardCards.length - 1].rank;
          return { ...trick, minRank: 1, maxRank, length: maxRank };
        } else {
          const minRank = standardCards[0].rank;
          const maxRank = standardCards[standardCards.length - 1].rank;
          return { ...trick, minRank, maxRank, length: maxRank - minRank + 1 };
        }
      }
    case TrickType.DOG:
      return trick;
    case TrickType.FOUR_OF_A_KIND:
      return { ...trick, rank: standardCards[0].rank };
    case TrickType.STRAIGHT_FLUSH:
      const sfMinRank = standardCards[0].rank;
      const sfMaxRank = standardCards[standardCards.length - 1].rank;
      return { ...trick, minRank: sfMinRank, maxRank: sfMaxRank, length: sfMaxRank - sfMinRank + 1 };
    default:
      return null;
  }
};

export const canCoverUp = (myTrick, lastTrick) => {
  if (!myTrick) return false;
  if (!lastTrick) return true;
  if (lastTrick.type === TrickType.DOG) return false;

  switch (myTrick.type) {
    case TrickType.SINGLE:
      return lastTrick.type === TrickType.SINGLE && myTrick.rank > lastTrick.rank;
    case TrickType.PAIR:
      return lastTrick.type === TrickType.PAIR && myTrick.rank > lastTrick.rank;
    case TrickType.THREE_OF_A_KIND:
      return lastTrick.type === TrickType.THREE_OF_A_KIND && myTrick.rank > lastTrick.rank;
    case TrickType.FULL_HOUSE:
      return lastTrick.type === TrickType.FULL_HOUSE && myTrick.rank > lastTrick.rank;
    case TrickType.CONSECUTIVE_PAIRS:
      return lastTrick.type === TrickType.CONSECUTIVE_PAIRS && myTrick.length === lastTrick.length && myTrick.maxRank > lastTrick.maxRank;
    case TrickType.STRAIGHT:
      return lastTrick.type === TrickType.STRAIGHT && myTrick.length === lastTrick.length && myTrick.maxRank > lastTrick.maxRank;
    case TrickType.DOG:
      return false;
    case TrickType.FOUR_OF_A_KIND:
      if (lastTrick.type === TrickType.STRAIGHT_FLUSH) {
        return false;
      } else if (lastTrick.type === TrickType.FOUR_OF_A_KIND) {
        return myTrick.rank > lastTrick.rank;
      } else {
        return true;
      }
    case TrickType.STRAIGHT_FLUSH:
      if (lastTrick.type === TrickType.STRAIGHT_FLUSH) {
        return myTrick.length > lastTrick.length || myTrick.length === lastTrick.length && myTrick.maxRank > lastTrick.maxRank;
      } else {
        return true;
      }
    default:
      return false;
  }
};

export const canSatisfyWish = (hand, wish, lastTrick) => {
  if (!wish) return false;

  const standardCards = extractStandardCards(hand);
  const wishCards = standardCards.filter(c => c.rank === wish);
  if (wishCards.length === 0) return false;

  const hasPhoenix = hand.some(isPhoenix);
  const cardCounts = standardCards.reduce((acc, card) => {
    acc[card.rank] = (acc[card.rank] || 0) + 1;
    return acc;
  }, {});

  // Any trick with the wish card can start a phase
  if (!lastTrick) return true;

  // 1. Can satisfy with a bomb?
  // 4 of a Kind
  if (cardCounts[wish] === 4) {
    const bombTrick = { type: TrickType.FOUR_OF_A_KIND, rank: wish };
    if (canCoverUp(bombTrick, lastTrick)) return true;
  }

  // Straight Flush
  const standardBySuit = standardCards.reduce((acc, card) => {
    acc[card.suit] = (acc[card.suit] || []);
    acc[card.suit].push(card.rank);
    return acc;
  }, {});

  for (const [suit, ranks] of Object.entries(standardBySuit)) {
    if (!ranks.includes(wish)) continue;
    const sortedRanks = [...new Set(ranks)].sort((a, b) => a - b);

    // Find segments containing wish
    let currentSeg = [];
    for (let i = 0; i < sortedRanks.length; i++) {
      if (currentSeg.length === 0 || sortedRanks[i] === currentSeg[currentSeg.length - 1] + 1) {
        currentSeg.push(sortedRanks[i]);
      } else {
        if (currentSeg.includes(wish) && currentSeg.length >= 5) {
          // Check all possible lengths within this segment that include wish
          for (let len = 5; len <= currentSeg.length; len++) {
            for (let startIdx = 0; startIdx <= currentSeg.length - len; startIdx++) {
              const sub = currentSeg.slice(startIdx, startIdx + len);
              if (sub.includes(wish)) {
                const sf = { type: TrickType.STRAIGHT_FLUSH, length: len, maxRank: sub[sub.length - 1] };
                if (canCoverUp(sf, lastTrick)) return true;
              }
            }
          }
        }
        currentSeg = [sortedRanks[i]];
      }
    }
    if (currentSeg.includes(wish) && currentSeg.length >= 5) {
      for (let len = 5; len <= currentSeg.length; len++) {
        for (let startIdx = 0; startIdx <= currentSeg.length - len; startIdx++) {
          const sub = currentSeg.slice(startIdx, startIdx + len);
          if (sub.includes(wish)) {
            const sf = { type: TrickType.STRAIGHT_FLUSH, length: len, maxRank: sub[sub.length - 1] };
            if (canCoverUp(sf, lastTrick)) return true;
          }
        }
      }
    }
  }

  // If lastTrick is a bomb, only bombs can cover it (already checked)
  if (isBomb(lastTrick.type)) return false;

  // 2. Can satisfy with a normal trick of the same type?
  switch (lastTrick.type) {
    case TrickType.SINGLE:
      return wish > lastTrick.rank;

    case TrickType.PAIR:
      if (wish <= lastTrick.rank) return false;
      return cardCounts[wish] >= 2 || (hasPhoenix && cardCounts[wish] >= 1);

    case TrickType.THREE_OF_A_KIND:
      if (wish <= lastTrick.rank) return false;
      return cardCounts[wish] >= 3 || (hasPhoenix && cardCounts[wish] >= 2);

    case TrickType.FULL_HOUSE:
      // Case A: Wish is the triple
      if (wish > lastTrick.rank) {
        const hasNaturalTriple = cardCounts[wish] >= 3;
        const hasPhoenixTripleForce = !hasNaturalTriple && hasPhoenix && cardCounts[wish] >= 2;

        if (hasNaturalTriple || hasPhoenixTripleForce) {
          const usedPhoenixInTriple = hasPhoenixTripleForce;
          for (let r = 2; r <= 14; r++) {
            if (r === wish) continue;
            const count = cardCounts[r] || 0;
            if (count >= 2 || (!usedPhoenixInTriple && hasPhoenix && count >= 1)) return true;
          }
        }
      }
      // Case B: Wish is the pair, another rank > lastTrick.rank is the triple
      for (let r = 2; r <= 14; r++) {
        if (r === wish || r <= lastTrick.rank) continue;
        const hasNaturalTriple = cardCounts[r] >= 3;
        const hasPhoenixTripleForce = !hasNaturalTriple && hasPhoenix && cardCounts[r] >= 2;

        if (hasNaturalTriple || hasPhoenixTripleForce) {
          const usedPhoenixInTriple = hasPhoenixTripleForce;
          if (cardCounts[wish] >= 2 || (!usedPhoenixInTriple && hasPhoenix && cardCounts[wish] >= 1)) return true;
        }
      }
      return false;

    case TrickType.STRAIGHT:
      const len = lastTrick.length;
      const allRanks = Object.keys(cardCounts).map(Number).sort((a, b) => a - b);
      const rankSet = new Set(allRanks);

      // Look for any straight starting from min+1
      for (let start = lastTrick.minRank + 1; start <= 15 - len; start++) {
        const end = start + len - 1;
        if (wish < start || wish > end) continue;

        let missing = 0;
        for (let r = start; r <= end; r++) {
          if (!rankSet.has(r)) missing++;
        }
        if (missing === 0 || (hasPhoenix && missing === 1)) return true;
      }
      return false;

    case TrickType.CONSECUTIVE_PAIRS:
      const cLen = lastTrick.length;
      for (let start = lastTrick.minRank + 1; start <= 15 - cLen; start++) {
        const end = start + cLen - 1;
        if (wish < start || wish > end) continue;

        let missingCount = 0;
        for (let r = start; r <= end; r++) {
          const count = cardCounts[r] || 0;
          if (count < 2) missingCount += (2 - count);
        }
        if (missingCount === 0 || (hasPhoenix && missingCount === 1)) return true;
      }
      return false;

    default:
      return false;
  }
};
