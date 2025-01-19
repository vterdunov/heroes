package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    /**
     * Сложность алгоритма: O(n * m):
     * n - количество рядов (строк) в массиве
     * m - максимальное количество юнитов в ряду
     *
     * 1. Внешний цикл выполняется n раз
     * 2. Для каждой итерации внешнего цикла:
     *    - Вызывается getAvailableUnitsInRow со сложностью O(m)
     *    - Операции с HashMap имеют сложность O(1)
     * 3. Финальная операция сбора результатов имеет сложность O(n)
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        // Юниты доступные для атаки по номеру ряда
        Map<Integer, List<Unit>> availableUnitsMap = new HashMap<>();

        // Обрабатываем каждый ряд
        for (int rowNum = 0; rowNum < unitsByRow.size(); rowNum++) {
            List<Unit> currentRow = unitsByRow.get(rowNum);
            List<Unit> availableInRow = getAvailableUnitsInRow(currentRow, isLeftArmyTarget);
            if (!availableInRow.isEmpty()) {
                availableUnitsMap.put(rowNum, availableInRow);
            }
        }

        // Все доступные юниты
        List<Unit> result = new ArrayList<>();
        availableUnitsMap.values().forEach(result::addAll);

        return result;
    }

    // Поиск доступных для атаки юнитов в ряду
    private List<Unit> getAvailableUnitsInRow(List<Unit> currentRow, boolean isLeftArmyTarget) {
        List<Unit> availableUnits = new ArrayList<>();
        for (int pos = 0; pos < currentRow.size(); pos++) {
            Unit currentUnit = currentRow.get(pos);
            // Проверяем существование, жизнь и позицию юнита
            if (currentUnit != null && currentUnit.isAlive() &&
                    (isLeftArmyTarget ? checkRightEdge(currentRow, pos) : checkLeftEdge(currentRow, pos))) {
                availableUnits.add(currentUnit);
            }
        }

        return availableUnits;
    }

    // Проверка правого края
    private boolean checkRightEdge(List<Unit> currentRow, int position) {
        return position == currentRow.size() - 1 ||
                currentRow.subList(position + 1, currentRow.size()).stream().allMatch(Objects::isNull);
    }

    // Проверка левого края
    private boolean checkLeftEdge(List<Unit> currentRow, int position) {
        return position == 0 ||
                currentRow.subList(0, position).stream().allMatch(Objects::isNull);
    }
}