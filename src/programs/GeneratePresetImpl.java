package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GeneratePresetImpl implements GeneratePreset {
    private static final int MAX_SAME_UNITS = 11;
    private static final int FIELD_WIDTH = 3;
    private static final int FIELD_HEIGHT = 21;

    /**
     * Сложность алгоритма: O(n log n), где n - размер входного списка unitList
     * - Сортировка юнитов по эффективности: O(n log n)
     * - Цикл выбора юнитов: O(n)
     * - Распределение координат: O(n)
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Создаем армию
        Army resultArmy = new Army();
        List<Unit> chosenUnits = new ArrayList<>();

        // Сортируем по силе
        orderByPower(unitList);

        int totalCost = 0;

        for (Unit unit : unitList) {
            int count = calculateUnitCount(unit, maxPoints, totalCost);
            addUnits(unit, count, chosenUnits);
            totalCost += count * unit.getCost();
        }

        distributePositions(chosenUnits);
        resultArmy.setUnits(chosenUnits);
        resultArmy.setPoints(totalCost);
        return resultArmy;
    }

    private void orderByPower(List<Unit> units) {
        units.sort(Comparator.comparingDouble(unit ->
                -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())));
    }

    private int calculateUnitCount(Unit unit, int maxPoints, int totalCost) {
        return Math.min(MAX_SAME_UNITS, (maxPoints - totalCost) / unit.getCost());
    }

    private void addUnits(Unit unit, int count, List<Unit> chosenUnits) {
        for (int i = 0; i < count; i++) {
            Unit copy = cloneUnit(unit, i);
            chosenUnits.add(copy);
        }
    }

    private Unit cloneUnit(Unit unit, int index) {
        Unit copy = new Unit(unit.getName(), unit.getUnitType(), unit.getHealth(),
                unit.getBaseAttack(), unit.getCost(), unit.getAttackType(),
                unit.getAttackBonuses(), unit.getDefenceBonuses(), -1, -1);
        copy.setName(unit.getUnitType() + " " + index);
        return copy;
    }

    private void distributePositions(List<Unit> units) {
        Set<String> takenPositions = new HashSet<>();
        Random rand = new Random();

        for (Unit unit : units) {
            assignPosition(unit, takenPositions, rand);
        }
    }

    private void assignPosition(Unit unit, Set<String> takenPositions, Random rand) {
        int x, y;
        do {
            x = rand.nextInt(FIELD_WIDTH);
            y = rand.nextInt(FIELD_HEIGHT);
        } while (takenPositions.contains(x + "," + y));
        takenPositions.add(x + "," + y);
        unit.setxCoordinate(x);
        unit.setyCoordinate(y);
    }
}