import React, { Component } from 'react';
import { Sparklines, SparklinesLine, SparklinesSpots } from 'react-sparklines';
import RemoteSensor from './RemoteSensor';
import { HEARTBEAT_TOPIC } from './topics'

class Heartbeats extends Component {

    constructor(props) {
        super(props);

        this.state = { data: [], currentValue: 0 }

        this.sensor = new RemoteSensor(HEARTBEAT_TOPIC);
        this.sensor.onDataReceived((data) => {
            console.log('[data]', data.toString());
            if (data.toString() != 0)
                this.setState({ data: this.state.data.concat([data.toString()]), currentValue: data.toString() });
            else {
                console.log('[data:error] data is 0. Check the position of your finger on the sensor !');
            }
        })

        this.sensor.onErrorReceived((err) => {
            console.log('[error]', err.toString());
        })
    }
    renderChart() {
        if (this.state.data.length == 0) {
            const styles = {
                height: '270px',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                fontSize: '50px'
            }
            return (
                <div style={styles}>No data received</div>
            );
        }


        return (
            <Sparklines data={this.state.data}>
                <SparklinesLine color="#1c8cdc" />
                <SparklinesSpots />
            </Sparklines>
        );
    }

    render() {
        return (
            <ul className="list-group">
                <li className="list-group-item">Heartbeats : {this.state.currentValue}</li>
                <li className="list-group-item"><button onClick={() => this.sensor.activate()}>Click me</button></li>
                <li className="list-group-item">
                    {this.renderChart()}
                </li>
            </ul>
        );
    }
}

export default Heartbeats;