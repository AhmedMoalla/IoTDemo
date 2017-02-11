import React, { Component, PropTypes } from 'react';
import { Link } from 'react-router';

class NavMenu extends Component {

    constructor(props) {
        super(props);
        
        this.state = { selected: this.initiallySelectedItem() };
    }

    initiallySelectedItem() {
        const activeItems = this.props.items.filter((i) => i.active);
        return activeItems[0].to || '/';
    }

    onItemClicked(route) {
        this.setState({ selected: route });
    }

    renderItems() {
        return this.props.items.map((i) => {
            return (
                <li key={i.to} className={this.state.selected == i.to ? 'active' : ''} onClick={() => this.onItemClicked(i.to)}>
                    <Link to={i.to}>{i.text}</Link>
                </li>
            )
        })
    }

    render() {
        return (
            <ul className="nav nav-pills nav-stacked">
                {this.renderItems()}
            </ul>
        );
    }
};

export default NavMenu;