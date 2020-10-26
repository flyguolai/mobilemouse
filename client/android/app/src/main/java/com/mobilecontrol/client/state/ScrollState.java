package com.mobilecontrol.client.state;

import android.text.method.Touch;
import android.util.Log;

import com.mobilecontrol.client.data.TouchData;

public class ScrollState extends State {
    ScrollState(StateManager stateManager){
        super();
        this.name = "scroll";
        this.stateManager = stateManager;
        this.touchDataType = TouchData.TOUCH_TYPE_SCROLL;
    }

    @Override
    public void nextState() {
        this.stateManager.setState(stateManager.dragState);
    }

    @Override
    public void prevState() {
        this.stateManager.setState(stateManager.noneState);
    }
}
