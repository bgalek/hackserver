import React from "react";
import FlightTakeoffIcon from '@material-ui/icons/FlightTakeoff';
import CodeIcon from '@material-ui/icons/Code';
import ScoreIcon from '@material-ui/icons/Score';
import PeopleIcon from '@material-ui/icons/People';
import Teams from "../views/Teams";
import Challenges from "../views/Challenges";
import About from "../views/About";
import Leaderboard from "../views/Leaderboard";

export const ADDITIONAL_MENU = 'ADDITIONAL';
export const MAIN_MENU = 'MAIN';

const TEAMS = {
    path: '/teams',
    label: 'Teams',
    icon: <PeopleIcon/>,
    view: Teams,
    home: true,
    menu: MAIN_MENU
};

const CHALLENGES = {
    path: '/challenges',
    label: 'Challenges',
    icon: <FlightTakeoffIcon/>,
    view: Challenges,
    menu: MAIN_MENU
};

const SCORES = {
    path: '/leaderboard',
    label: 'Leaderboard',
    icon: <ScoreIcon/>,
    view: Leaderboard,
    menu: MAIN_MENU
};

const ABOUT = {
    path: '/about',
    label: 'About',
    icon: <CodeIcon/>,
    view: About,
    menu: ADDITIONAL_MENU
};

const routes = [
    TEAMS,
    CHALLENGES,
    SCORES,
    ABOUT
];

export default routes;