package Progmeth_project2.model.powerup;

import Progmeth_project2.model.GameState;
import Progmeth_project2.model.card.BaseCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Power-up that highlights one unmatched matching pair.
 *
 * <p>When activated, searches for a face-down pair with the same symbol key and
 * stores the two card IDs in the game state. The view layer reads these IDs and
 * applies a highlight style for {@value #HINT_DURATION_MS} ms.</p>
 */
public class HintPowerUp extends BasePowerUp {

    /** Duration (ms) the hint highlight is shown. */
    public static final int HINT_DURATION_MS = 2000;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Creates a new Hint power-up.
     */
    public HintPowerUp() {
        super("Hint", "Highlights one matching pair for 2 seconds", "💡");
    }

    // ── BasePowerUp contract ─────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Finds an unmatched face-down pair and records their card IDs in the
     * game state so the view can highlight them.
     */
    @Override
    public void activate(GameState gameState) {
        List<BaseCard> faceDownCards = gameState.getCards().stream()
                .filter(c -> !c.isMatched() && !c.isFaceUp())
                .collect(Collectors.toList());

        // Group by symbolKey to find a pair
        Map<String, List<BaseCard>> groups = faceDownCards.stream()
                .collect(Collectors.groupingBy(BaseCard::getSymbolKey));

        List<String> pairKeys = groups.entrySet().stream()
                .filter(e -> e.getValue().size() == 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (pairKeys.isEmpty()) return;

        // Pick a random pair from the available ones
        Collections.shuffle(pairKeys);
        List<BaseCard> pair = groups.get(pairKeys.get(0));

        List<String> hintIds = new ArrayList<>();
        hintIds.add(pair.get(0).getCardId());
        hintIds.add(pair.get(1).getCardId());
        gameState.setHintCardIds(hintIds);
    }
}
