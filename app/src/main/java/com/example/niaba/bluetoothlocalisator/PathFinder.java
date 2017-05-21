package com.example.niaba.bluetoothlocalisator;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by niaba on 20/05/17.
 */

public class PathFinder {

    private final int size_tab_width = 1000;
   private final int size_tab_height = 1000;

    /**
     * nodes to be evaluate
     */
    ArrayList<Cell> freeNodes;
    /**
     * evaluated nodes
     */
    ArrayList<Cell> closedNodes;
    /**
     * Start Cell
     */
    Cell start;

    /**
     * Goal Cell
     */
    Cell goal;


    Entrepot ent;



    public PathFinder() {
        this.ent = new Entrepot(size_tab_width, size_tab_height,0.25,this);
        reCompute();
    }



    ArrayList<Cell> algoAstar(Cell _start, Cell _goal){

        start = _start;
        goal = _goal;
        // list of visited nodes
        closedNodes = new ArrayList<Cell>();
        // list of nodes to evaluate
        freeNodes = new ArrayList<Cell>();
        freeNodes.add(start);
        // no cost to go from start to start
        // TODO: g(start) <- 0
        start.setG(0);
        // TODO: h(start) <- evaluation(start)
        start.setH(evaluation(start));
        // TODO: f(start) <- h(start)
        start.setF(start.getH());
        // while there is still a node to evaluate
        while (!freeNodes.isEmpty()) {
            // choose the node having a F minimal
            Cell n = chooseBestNode();
            // stop if the node is the goal
            if (isGoal(n)) return rebuildPath(n);
            // TODO: freeNodes <- freeNodes - {n}
            freeNodes.remove(n);
            // TODO: closedNodes <- closedNodes U {n}
            closedNodes.add(n);
            // construct the list of neighbourgs
            ArrayList<Cell> nextDoorNeighbours = neighbours4(n);
            for (Cell ndn : nextDoorNeighbours) {
                // if the neighbour has been visited, do not reevaluate it
                if (closedNodes.contains(ndn))
                    continue;
                // cost to reach the neighbour is the cost to reach n + cost from n to the neighbourg
                //TODO: int cost = ...
                int cost = costBetween(n, ndn);
                boolean bestCost = false;
                // if the neighbour has not been evaluated
                if (!freeNodes.contains(ndn)) {
                    // TODO: freeNodes <- freeNodes U {ndn}
                    freeNodes.add(ndn);
                    // TODO: h(ndn) -> evaluation(ndn)
                    ndn.setH(evaluation(ndn));
                    bestCost = true;
                } else
                    // if the neighbour has been evaluated to a more important cost, change its evaluation
                    if (cost < ndn.getG())
                        bestCost = true;
                if (bestCost) {
                    ndn.setParent(n);
                    //TODO : g(ndn) <- cost
                    ndn.setG(cost);
                    //TODO : f(ndn) <- g(ndn) + h(ndn)
                    ndn.setF(ndn.getG() + ndn.getH());
                }
            }
        }

        return null;
    }



    ArrayList<Cell> rebuildPath(Cell n) {
        if (n.getParent() != null) {
            ArrayList<Cell> p = rebuildPath(n.getParent());
            n.setVisited(true);
            p.add(n);
            return p;
        } else
            return (new ArrayList<Cell>());
    }

    /**
     * return the estimation of the distance from c to the goal
     */
    int evaluation(Cell c) {
        return manhattan_Dist(c);
    }


    public void reCompute() {
        ArrayList<Cell> solution = algoAstar(ent.getStart(), ent.getGoal());
        ent.setSolution(solution);
        if (solution == null)
            System.out.println("solution IMPOSSIBLE");

    }

    public int manhattan_Dist(Cell a) {
        return Math.abs(goal.getX() - a.getX()) + Math.abs(goal.getY() - a.getY());
    }

    /**
     * return the free node having the minimal f
     */
    Cell chooseBestNode() {
        Cell best = freeNodes.get(0);
        for (Cell c : freeNodes) {
            if (c.getF() < best.getF()) {
                best = c;
            }
        }
        return best;
    }

    /**
     * return weither n is a goal or not
     */
    boolean isGoal(Cell n) {
        return (n.getX() == goal.getX() && n.getY() == goal.getY());
    }


    /**
     * return the neighbouring of a node n without permission to go in diagonal
     */
    ArrayList<Cell> neighbours4(Cell n) {
        // TODO: (en reponse au 2e cas)
        ArrayList<Cell> voisin_non_verifer = new ArrayList<>();
        if (!ent.getCell(n.getX(), n.getY() + 1).isContainer())
            voisin_non_verifer.add(ent.getCell(n.getX(), n.getY() + 1));//(x,y+1) pas diag

        if (!ent.getCell(n.getX() + 1, n.getY()).isContainer())
            voisin_non_verifer.add(ent.getCell(n.getX() + 1, n.getY()));//(x+1,y)pas diag

        if (!ent.getCell(n.getX(), n.getY() - 1).isContainer())
            voisin_non_verifer.add(ent.getCell(n.getX(), n.getY() - 1));//(x,y-1)pas diag

        if (!ent.getCell(n.getX() - 1, n.getY()).isContainer())
            voisin_non_verifer.add(ent.getCell(n.getX() - 1, n.getY()));//(x-1,y)pas diag

        return voisin_non_verifer;
    }

    /**
     * return the cost from n to c : 10 for a longitudinal move, 14 (squareroot(2)*10) for a diagonal move
     */
    int costBetween(Cell n, Cell c) {
        //TODO : sur terre, deplacement horizontal ou vertical = 10; en diagonale = 14
        if (n.getX() == c.getX() || n.getY() == c.getY()) {
            return 10;
        } else {
            return (int) Math.sqrt(2.0) * 10;
        }
    }

}
