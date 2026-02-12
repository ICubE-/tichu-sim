package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.common.domain.GameName;
import com.icube.sim.tichu.games.common.domain.GameRule;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TichuRule implements GameRule {
    private final Map<Long, Team> teamAssignment = new HashMap<>();
    private final int timeLimit = 30;

    @Override
    public GameName getGameName() {
        return GameName.TICHU;
    }

    public Map<Long, Team> getDeterminedTeams(List<Long> userIds) {
        assert userIds.size() == 4;

        Collections.shuffle(userIds);

        int red = 0, blue = 0;
        for (Long userId : userIds) {
            var team = teamAssignment.getOrDefault(userId, Team.NONE);
            if (team.equals(Team.RED)) {
                red++;
            }
            if (team.equals(Team.BLUE)) {
                blue++;
            }
        }

        if (red > 2 || blue > 2) {
            throw new InvalidTeamAssignmentException();
        }

        var determinedTeams = new HashMap<Long, Team>();
        for (Long userId : userIds) {
            var team = teamAssignment.getOrDefault(userId, Team.NONE);
            if (!team.equals(Team.NONE)) {
                determinedTeams.put(userId, team);
                continue;
            }
            if (red < 2) {
                determinedTeams.put(userId, Team.RED);
                red++;
            } else if (blue < 2) {
                determinedTeams.put(userId, Team.BLUE);
                blue++;
            }
        }

        assert red == 2 && blue == 2;
        assert determinedTeams.size() == 4;
        return determinedTeams;
    }
}
