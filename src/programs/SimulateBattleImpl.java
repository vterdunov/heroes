package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    /**
     * Сложность: O(n * m)
     * n - количество юнитов в армии игрока
     * m - количество юнитов в армии компьютера
     *
     * 1. Внешний цикл while выполняется пока есть живые юниты в обеих армиях
     * 2. В каждой итерации:
     *    - Проверяем каждого юнита армии игрока: O(n)
     *    - Проверяем каждого юнита армии компьютера: O(m)
     * 3. В худшем случае каждый юнит может атаковать только один раз за итерацию
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        Set<Unit> playerForces = new HashSet<>(playerArmy.getUnits());
        Set<Unit> computerForces = new HashSet<>(computerArmy.getUnits());

        while (areBothArmiesActive(playerForces, computerForces)) {
            processRound(playerForces, computerForces);
            processRound(computerForces, playerForces);
        }
    }

    private boolean areBothArmiesActive(Set<Unit> firstArmy, Set<Unit> secondArmy) {
        return !firstArmy.isEmpty() && !secondArmy.isEmpty();
    }

    private void processRound(Set<Unit> attackingForces, Set<Unit> defendingForces)
            throws InterruptedException {
        Iterator<Unit> unitIterator = attackingForces.iterator();

        while (unitIterator.hasNext()) {
            Unit attacker = unitIterator.next();

            if (!attacker.isAlive()) {
                unitIterator.remove();
                continue;
            }

            processAttack(attacker);
        }
    }

    private void processAttack(Unit attacker) throws InterruptedException {
        Unit defender = attacker.getProgram().attack();

        if (defender != null) {
            printBattleLog.printBattleLog(attacker, defender);
        }
    }
}