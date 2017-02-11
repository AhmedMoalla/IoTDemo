import React, { Component } from 'react';
import { Sparklines, SparklinesLine, SparklinesSpots } from 'react-sparklines';
import { GoogleMapLoader, GoogleMap, Marker } from 'react-google-maps';
import RemoteSensor from './RemoteSensor';
import { LOCATION_TOPIC } from './topics';

class Location extends Component {
    constructor(props) {
        super(props);

        this.state = {
            currentValue: {
                position: {
                    lat: 25.0112183,
                    lng: 121.52067570000001,
                },
                key: `Your position`,
                defaultAnimation: 2,
            }
        }

        this.sensor = new RemoteSensor(LOCATION_TOPIC);
        this.sensor.onDataReceived((data) => {
            console.log('[data]', data.toString());
            if (data.toString() != 0) {
                this.setState({
                    currentValue: {
                        ...this.state.currentValue,
                        position: JSON.parse(data.toString()),
                    }
                });
                console.log(this.state.currentValue);
            } else {
                console.log('[data:error] data is 0.');
            }
        })

        this.sensor.onErrorReceived((err) => {
            console.log('[error]', err.toString());
        })
    }
    renderMap() {
        return (
            <GoogleMapLoader
                containerElement={
                    <div style={{ height: '100%' }} />
                }
                googleMapElement={
                    <GoogleMap defaultZoom={8}
                        defaultCenter={
                            { lat: 36.5, lng: 10 }
                        }>
                        <Marker {...this.state.currentValue} />
                    </GoogleMap>
                } />
        );
    }

    render() {
        return (
            <ul className="list-group">
                <li className="list-group-item">Position : {this.state.currentValue.position.lat}, {this.state.currentValue.position.lng}</li>
                <li className="list-group-item"><button onClick={() => this.sensor.activate()}>Click me</button></li>
                <li className="list-group-item" style={{ height: '500px' }}>
                    {this.renderMap()}
                </li>
            </ul>
        );
    }
}

export default Location;