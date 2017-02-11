import React from 'react';
import { Route, IndexRoute } from 'react-router';
import WelcomeContent from './components/WelcomeContent';
import Heartbeats from './components/Heartbeats';
import Location from './components/Location';

import App from './components/app';

// Nested route is to render component as children of the component of the parent route
// IndexRoute render its component only if the route matches the parent route
const route = (
    <Route path="/" component={App} >
        <IndexRoute component={WelcomeContent} />
        <Route path="heartbeats" component={Heartbeats} />
        <Route path="location" component={Location} />
    </Route>
)
/*
<Route path="posts/new" component={PostCreate} />
<Route path="posts/:id" component={PostShow} />*/ 

export default route;