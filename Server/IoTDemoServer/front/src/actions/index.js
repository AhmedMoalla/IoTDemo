import { SELECT_SENSOR } from './types';

export function selectSensor(sensor) {
    return {
        type: SELECT_SENSOR,
        payload: sensor
    }
}