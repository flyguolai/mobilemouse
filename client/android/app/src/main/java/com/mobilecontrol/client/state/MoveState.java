package com.mobilecontrol.client.state;


import android.util.Log;

import com.mobilecontrol.client.data.TouchData;

public class MoveState extends State{
    MoveState(StateManager stateManager){
        super();
        this.name = "move";
        this.stateManager = stateManager;
        this.touchDataType = TouchData.TOUCH_TYPE_MOVE;

    }

    @Override
    public void nextState() {
        this.stateManager.setState(stateManager.scrollState);
    }

    @Override
    public void prevState() {
        Log.d(this.name,"move state has not got a prev state");
        this.stateManager.setState(this.stateManager.noneState);
    }
}
