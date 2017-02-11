import {
	combineReducers
} from 'redux';
import SensorReducer from './CurrentSensorReducer';

const rootReducer = combineReducers({
	currentSensor: SensorReducer
});

export default rootReducer;