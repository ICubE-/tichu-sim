package com.icube.sim.tichu.rooms;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MemberIdRepository {
    private final Set<Long> memberIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void save(Long memberId) {
        memberIds.add(memberId);
    }

    public boolean exists(Long memberId) {
        return memberIds.contains(memberId);
    }

    public void delete(Long memberId) {
        memberIds.remove(memberId);
    }
}
