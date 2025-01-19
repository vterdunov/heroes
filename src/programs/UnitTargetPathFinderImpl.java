package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int FIELD_WIDTH = 27;
    private static final int FIELD_HEIGHT = 21;
    private static final int[][] MOVES = {
            {-1, 0},  // влево
            {1, 0},   // вправо
            {0, -1},  // вверх
            {0, 1}    // вниз
    };

    /**
     * Сложность: O(n * log(n))
     * n = FIELD_WIDTH * FIELD_HEIGHT
     * - Основной цикл: O(n * log(n))
     * - Обработка соседей: O(1) для каждой клетки
     */
    @Override
    public List<Edge> getTargetPath(Unit attacker, Unit target, List<Unit> allUnits) {
        int[][] distances = createDistanceMatrix();
        boolean[][] visited = new boolean[FIELD_WIDTH][FIELD_HEIGHT];
        Edge[][] predecessors = new Edge[FIELD_WIDTH][FIELD_HEIGHT];
        Set<String> blockedCells = findBlockedCells(allUnits, attacker, target);

        PriorityQueue<EdgeDistance> frontier = new PriorityQueue<>(
                Comparator.comparingInt(EdgeDistance::getDistance)
        );

        initializeSearch(attacker, distances, frontier);

        while (!frontier.isEmpty()) {
            EdgeDistance current = frontier.poll();
            if (visited[current.getX()][current.getY()]) continue;
            visited[current.getX()][current.getY()] = true;

            if (isDestinationReached(current, target)) {
                break;
            }

            processMoves(current, blockedCells, distances, predecessors, frontier);
        }

        return reconstructPath(predecessors, attacker, target);
    }

    private int[][] createDistanceMatrix() {
        int[][] distances = new int[FIELD_WIDTH][FIELD_HEIGHT];
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distances;
    }

    private Set<String> findBlockedCells(List<Unit> allUnits, Unit attacker, Unit target) {
        Set<String> blockedCells = new HashSet<>();
        for (Unit unit : allUnits) {
            if (unit.isAlive() && unit != attacker && unit != target) {
                blockedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return blockedCells;
    }

    private void initializeSearch(Unit attacker, int[][] distances, PriorityQueue<EdgeDistance> frontier) {
        int startX = attacker.getxCoordinate();
        int startY = attacker.getyCoordinate();
        distances[startX][startY] = 0;
        frontier.add(new EdgeDistance(startX, startY, 0));
    }

    private boolean isDestinationReached(EdgeDistance current, Unit target) {
        return current.getX() == target.getxCoordinate() &&
                current.getY() == target.getyCoordinate();
    }

    private void processMoves(
            EdgeDistance current,
            Set<String> blockedCells,
            int[][] distances,
            Edge[][] predecessors,
            PriorityQueue<EdgeDistance> frontier
    ) {
        for (int[] move : MOVES) {
            int nextX = current.getX() + move[0];
            int nextY = current.getY() + move[1];

            if (isCellValid(nextX, nextY, blockedCells)) {
                int newDistance = distances[current.getX()][current.getY()] + 1;
                if (newDistance < distances[nextX][nextY]) {
                    distances[nextX][nextY] = newDistance;
                    predecessors[nextX][nextY] = new Edge(current.getX(), current.getY());
                    frontier.add(new EdgeDistance(nextX, nextY, newDistance));
                }
            }
        }
    }

    private boolean isCellValid(int x, int y, Set<String> blockedCells) {
        return x >= 0 && x < FIELD_WIDTH &&
                y >= 0 && y < FIELD_HEIGHT &&
                !blockedCells.contains(x + "," + y);
    }

    private List<Edge> reconstructPath(Edge[][] predecessors, Unit attacker, Unit target) {
        List<Edge> path = new ArrayList<>();
        int currentX = target.getxCoordinate();
        int currentY = target.getyCoordinate();

        while (currentX != attacker.getxCoordinate() || currentY != attacker.getyCoordinate()) {
            path.add(new Edge(currentX, currentY));
            Edge prev = predecessors[currentX][currentY];
            if (prev == null) {
                return Collections.emptyList();
            }
            currentX = prev.getX();
            currentY = prev.getY();
        }

        path.add(new Edge(attacker.getxCoordinate(), attacker.getyCoordinate()));
        Collections.reverse(path);
        return path;
    }
}