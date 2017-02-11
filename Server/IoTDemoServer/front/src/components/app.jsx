import React, { Component } from 'react';
import { Link } from 'react-router';

import NavBar from './NavBar';
import NavMenu from './NavMenu';

export default class App extends Component {

	render() {
		const navMenu = [
			{ to: '/', text: "Home", active: true },
			{ to: 'heartbeats', text: 'Heartbeats' },
			{ to: 'location', text: 'Location' }
		]

		return (
			<div>
				<NavBar />
				<div>
					<div className="col-md-3">
						<NavMenu items={navMenu}/>
					</div>
					<div className="col-md-9">
						{this.props.children}
					</div>
				</div>
			</div>
		);
	}
}
