import { SELECT_SENSOR } from '../actions/types';

const INITIAL_STATE = {
    currentSensor: {
        id: -1,
        name: null,
        status: null,
        data: []
    },
    sensors: []
}

export default (state = INITIAL_STATE, action) => {
    switch(action.type) {
        case SELECT_SENSOR: 
            return { ...state, currentSensor: action.payload }
        default:
            return state;
    }
}