package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.ArrayList;
import java.util.Random;

class MyAgentState
{
    public int[][] world = new int[30][30];
    public int initialized = 0;
    final int UNKNOWN 	= 0;
    final int WALL 		= 1;
    final int CLEAR 	= 2;
    final int DIRT		= 3;
    final int HOME		= 4;
    final int ACTION_NONE 			= 0;
    final int ACTION_MOVE_FORWARD 	= 1;
    final int ACTION_TURN_RIGHT 	= 2;
    final int ACTION_TURN_LEFT 		= 3;
    final int ACTION_SUCK	 		= 4;

    public int agent_x_position = 1;
    public int agent_y_position = 1;
    public int agent_last_action = ACTION_NONE;

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public int agent_direction = EAST;

    MyAgentState()
    {
        for (int i=0; i < world.length; i++)
            for (int j=0; j < world[i].length ; j++)
                world[i][j] = UNKNOWN;
        world[1][1] = HOME;
        agent_last_action = ACTION_NONE;
    }
    // Based on the last action and the received percept updates the x & y agent position
    public void updatePosition(DynamicPercept p)
    {
        Boolean bump = (Boolean)p.getAttribute("bump");
        System.out.println("action : " + (agent_last_action==ACTION_MOVE_FORWARD) + " bump : " + bump);
        if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
        {
            System.out.println("MOVE EUH !");
            switch (agent_direction) {
                case MyAgentState.NORTH:
                    agent_y_position--;
                    break;
                case MyAgentState.EAST:
                    agent_x_position++;
                    break;
                case MyAgentState.SOUTH:
                    agent_y_position++;
                    break;
                case MyAgentState.WEST:
                    agent_x_position--;
                    break;
            }
        }

    }

    public void updateWorld(int x_position, int y_position, int info)
    {
        world[x_position][y_position] = info;
    }

    public void printWorldDebug()
    {
        for (int i=0; i < world.length; i++)
        {
            for (int j=0; j < world[i].length ; j++)
            {
                if (world[j][i]==UNKNOWN)
                    System.out.print(" ? ");
                if (world[j][i]==WALL)
                    System.out.print(" # ");
                if (world[j][i]==CLEAR)
                    System.out.print(" 0 ");
                if (world[j][i]==DIRT)
                    System.out.print(" D ");
                if (world[j][i]==HOME)
                    System.out.print(" H ");
            }
            System.out.println("");
        }
    }
}

class MyAgentProgram implements AgentProgram {

    private int initnialRandomActions = 10;
    private Random random_generator = new Random();
    private boolean turnLeft = false,
            removeLast = false,
            addPosition = false,
            homeFound = false,
            isAllFound = false,
            captureHome = false;

    // Here you can define your variables!
    ArrayList<Integer> pos = new ArrayList<Integer>(), posHome = new ArrayList<Integer>();
    int nbTurn = 0, xHome = 1, yHome = 1, maxHeight = 5, maxWidth = 5, indexHome = 0;
    public int iterationCounter = 5000;
    public MyAgentState state = new MyAgentState();

    // moves the Agent to a random start position
    // uses percepts to update the Agent position - only the position, other percepts are ignored
    // returns a random action
    private Action moveToRandomStartPosition(DynamicPercept percept) {
        int action = random_generator.nextInt(6);
        initnialRandomActions--;
        state.updatePosition(percept);
        if(action==0) {
            state.agent_direction = ((state.agent_direction-1) % 4);
            if (state.agent_direction<0)
                state.agent_direction +=4;
            state.agent_last_action = state.ACTION_TURN_LEFT;
            return LIUVacuumEnvironment.ACTION_TURN_LEFT;
        } else if (action==1) {
            state.agent_direction = ((state.agent_direction+1) % 4);
            state.agent_last_action = state.ACTION_TURN_RIGHT;
            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
        }
        state.agent_last_action=state.ACTION_MOVE_FORWARD;
        return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
    }

    public Action turnRight() {
        state.agent_last_action = state.ACTION_TURN_RIGHT;
        state.agent_direction = ((state.agent_direction + 1) % 4);
        return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    }

    public Action turnLeft() {
        state.agent_last_action = state.ACTION_TURN_LEFT;
        state.agent_direction = ((state.agent_direction - 1 + 4) % 4);
        return LIUVacuumEnvironment.ACTION_TURN_LEFT;
    }
    public Action turnaround() {
        state.agent_last_action = state.ACTION_TURN_LEFT;
        state.agent_direction = ((state.agent_direction - 1 + 4) % 4);
        turnLeft = true;
        return LIUVacuumEnvironment.ACTION_TURN_LEFT;
    }

    public boolean checkPos(int x, int y) {
        System.out.println("x : " + x + "    y : " + y);
        if(state.world[x][y] == state.HOME ) System.out.println("Home spotted");
        return state.world[x][y] == state.UNKNOWN || (state.world[x][y] == state.HOME && !homeFound);
    }

    @Override
    public Action execute(Percept percept) {

        // DO NOT REMOVE this if condition!!!
        if (initnialRandomActions>0) {
            return moveToRandomStartPosition((DynamicPercept) percept);
        } else if (initnialRandomActions==0) {
            // process percept for the last step of the initial random actions
            initnialRandomActions--;
            state.updatePosition((DynamicPercept) percept);
            System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
            state.agent_last_action=state.ACTION_SUCK;
            return LIUVacuumEnvironment.ACTION_SUCK;
        }

        // This example agent program will update the internal agent state while only moving forward.
        // START HERE - code below should be modified!

        System.out.println("init " + iterationCounter);
        System.out.println("x=" + state.agent_x_position);
        System.out.println("y=" + state.agent_y_position);
        System.out.println("dir=" + state.agent_direction);


        iterationCounter--;

        if (iterationCounter==0)
            return NoOpAction.NO_OP;

        DynamicPercept p = (DynamicPercept) percept;
        Boolean bump = (Boolean)p.getAttribute("bump");
        Boolean dirt = (Boolean)p.getAttribute("dirt");
        Boolean home = (Boolean)p.getAttribute("home");

        // State update based on the percept value and the last action
        state.updatePosition((DynamicPercept)percept);
        if (bump) {
            switch (state.agent_direction) {
                case MyAgentState.NORTH:
                    state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
                    break;
                case MyAgentState.EAST:
                    state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
                    break;
                case MyAgentState.SOUTH:
                    state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
                    break;
                case MyAgentState.WEST:
                    state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
                    break;
            }
        }
        else {
            if(state.agent_last_action == state.ACTION_MOVE_FORWARD && homeFound && pos.size() != 0) {
                posHome.add(state.agent_direction);
            }
            if(state.agent_last_action == state.ACTION_MOVE_FORWARD && !removeLast && addPosition) {
                System.out.println("ADD : " + state.agent_direction);
                if(!(pos.size() == 0 && homeFound)) pos.add(state.agent_direction);
                if(state.agent_direction == MyAgentState.EAST && state.agent_x_position > maxWidth) maxWidth = state.agent_x_position;
                if(state.agent_direction == MyAgentState.SOUTH && state.agent_y_position > maxHeight) maxHeight = state.agent_y_position;
                if(state.agent_x_position == xHome && state.agent_y_position == yHome && !homeFound) homeFound = true;
            }
            if (dirt)
                state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
            else
                state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
        }

        state.printWorldDebug();


        // Next action selection based on the percept value
        if (dirt)
        {
            System.out.println("DIRT -> choosing SUCK action!");
            state.agent_last_action=state.ACTION_SUCK;
            return LIUVacuumEnvironment.ACTION_SUCK;
        }
        if(turnLeft) {
            state.agent_last_action = state.ACTION_TURN_LEFT;
            state.agent_direction = ((state.agent_direction-1 + 4) % 4);
            turnLeft = false;
            return LIUVacuumEnvironment.ACTION_TURN_LEFT;

        }
        else
        {
            addPosition = false;
            if (bump)
            {
                if(state.agent_direction == MyAgentState.EAST) {
                    if (checkPos(state.agent_x_position, state.agent_y_position + 1)) return turnRight();
                    if (checkPos(state.agent_x_position, state.agent_y_position - 1)) return turnLeft();
                    if (checkPos(state.agent_x_position - 1, state.agent_y_position)) return turnaround();
                    return previous_position();
                }
                if(state.agent_direction == MyAgentState.WEST) {
                    if (checkPos(state.agent_x_position, state.agent_y_position + 1)) return turnLeft();
                    if (checkPos(state.agent_x_position, state.agent_y_position - 1)) return turnRight();
                    if (checkPos(state.agent_x_position + 1, state.agent_y_position)) return turnaround();
                    return previous_position();
                }
                if(state.agent_direction == MyAgentState.NORTH) {
                    if (checkPos(state.agent_x_position - 1, state.agent_y_position)) return turnLeft();
                    if (checkPos(state.agent_x_position + 1, state.agent_y_position)) return turnRight();
                    if (checkPos(state.agent_x_position, state.agent_y_position + 1)) return turnaround();
                    return previous_position();

                }
                else {
                    if (checkPos(state.agent_x_position - 1, state.agent_y_position)) return turnRight();
                    if (checkPos(state.agent_x_position + 1, state.agent_y_position)) return turnLeft();
                    if (checkPos(state.agent_x_position, state.agent_y_position - 1)) return turnaround();
                    return previous_position();
                }
            }
            else    // free space
            {
                if(homeFound && pos.size() == 0) return previous_position();
                if(removeLast) {
                    state.agent_last_action=state.ACTION_MOVE_FORWARD;
                    pos.remove(pos.size() - 1);
                    removeLast = false;
                    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
                }
                else {
                    if(state.agent_direction == MyAgentState.NORTH && !checkPos(state.agent_x_position, state.agent_y_position - 1)) {
                        System.out.println("already visited top");
                        if(checkPos(state.agent_x_position + 1 , state.agent_y_position)) return turnRight();
                        if(checkPos(state.agent_x_position - 1 , state.agent_y_position)) return turnLeft();
                        return previous_position();
                    }
                    if(state.agent_direction == MyAgentState.SOUTH && !checkPos(state.agent_x_position, state.agent_y_position + 1)) {
                        System.out.println("already visited SOUTH");
                        if(checkPos(state.agent_x_position + 1 , state.agent_y_position)) return turnLeft();
                        if(checkPos(state.agent_x_position - 1 , state.agent_y_position)) return turnRight();
                        return previous_position();
                    }
                    if(state.agent_direction == MyAgentState.EAST && !checkPos(state.agent_x_position + 1, state.agent_y_position)) {
                        System.out.println("already visited EAST");
                        if(checkPos(state.agent_x_position, state.agent_y_position + 1)) return turnRight();
                        if(checkPos(state.agent_x_position, state.agent_y_position - 1)) return turnLeft();
                        return previous_position();
                    }
                    if(state.agent_direction == MyAgentState.WEST && !checkPos(state.agent_x_position - 1, state.agent_y_position)) {
                        System.out.println("already visited WEST");
                        if(checkPos(state.agent_x_position, state.agent_y_position + 1)) return turnLeft();
                        if(checkPos(state.agent_x_position, state.agent_y_position - 1)) return turnRight();
                        return previous_position();
                    }
                }
                state.agent_last_action=state.ACTION_MOVE_FORWARD;
                addPosition = true;
                for(int i : pos) System.out.print(" " + i + " ");
                System.out.println();
                return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
            }
        }
    }
    private Action previous_position() {
        for(int i : pos) System.out.print(" " + i + " ");
        System.out.println("previous position size :   " + pos.size() + " state direction : " + state.agent_direction);
        if(pos.size() == 0) {
            if(xHome == state.agent_x_position && yHome == state.agent_y_position) return NoOpAction.NO_OP;
            removeLast = true;
            System.out.println("Size : " + posHome.size());
            for(int i : posHome) System.out.print(" " + i + " ");
            System.out.println();
            int posLook = (posHome.get(posHome.size() - 1) + 2) % 4;
            int nbLeft = (state.agent_direction - posLook + 4) % 4;
            System.out.println("nbLeft : " + nbLeft);
            if(nbLeft == 3) return turnRight();
            if (nbLeft == 2) return turnaround();
            else if(nbLeft == 1) return turnLeft();
            else {
                state.agent_last_action=state.ACTION_MOVE_FORWARD;
                posHome.remove(posHome.size() - 1);
                removeLast = false;
                return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
            }
        }
        removeLast = true;
        int posLook = (pos.get(pos.size() - 1) + 2) % 4;
        int nbLeft = (state.agent_direction - posLook + 4) % 4;
        System.out.println("nbLeft : " + nbLeft);
        if(nbLeft == 3) return turnRight();
        if (nbLeft == 2) return turnaround();
        else if(nbLeft == 1) return turnLeft();
        else {
            state.agent_last_action=state.ACTION_MOVE_FORWARD;
            pos.remove(pos.size() - 1);
            removeLast = false;
            return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
        }
    }
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
        super(new MyAgentProgram());
    }
}
