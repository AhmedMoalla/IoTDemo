import React from 'react';
import { Link } from 'react-router';

const NavBar = (props) => {
    return (
        <nav className="navbar navbar-default navbar-fixed-top">
            <div className="container-fluid">
                <div className="navbar-header">
                    <Link to="/" className="navbar-brand">IoTDemo</Link>
                </div>
                <div className="collapse navbar-collapse">
                    <ul className="nav navbar-nav navbar-right">
                        <li><a>Current sensor : None</a></li>
                        <li><a>Status : Inactive</a></li>
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default NavBar;