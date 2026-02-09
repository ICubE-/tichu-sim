package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.common.ImmutableGameRuleException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TichuGameRule {
    private boolean isMutable = true;
    private Map<Long, Team> teamAssignment = new HashMap<>();
    private int timeLimit = 30;

    public void set(TichuGameRule other) {
        if (!this.isMutable) {
            throw new ImmutableGameRuleException();
        }

        this.teamAssignment = other.teamAssignment;
        this.timeLimit = other.timeLimit;
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
