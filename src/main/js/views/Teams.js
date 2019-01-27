import React, { Component } from 'react';
import { withStyles } from "@material-ui/core";
import PropTypes from "prop-types";
import Typography from '@material-ui/core/Typography';
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import FolderIcon from '@material-ui/icons/Folder';
import ListItemText from "@material-ui/core/ListItemText";

const styles = theme => ({});

class Teams extends Component {

    constructor(props) {
        super(props);
        this.state = {
            teams: [],
            isLoading: false
        };
    }

    async componentDidMount() {
        this.setState({ isLoading: true });
        const response = await fetch('/registration');
        const data = await response.json();
        this.setState({ teams: data, isLoading: false });
        const socket = new WebSocket('ws://localhost:8080/ws/events');
        socket.addEventListener('message', async (event) => {
            const team = JSON.parse(event.data);
            this.state.teams.push(team);
            this.setState({ teams: this.state.teams });
        });
    }

    render() {
        const { teams } = this.state;
        return [
            <Typography key="title" variant="h4" gutterBottom>
                Teams
            </Typography>,
            <List key="teams-list">
                {teams.map((team, index) =>
                    <ListItem key={team.name + index}>
                        <ListItemAvatar>
                            <Avatar>
                                <FolderIcon/>
                            </Avatar>
                        </ListItemAvatar>
                        <ListItemText primary={team.name}/>
                    </ListItem>
                )}
            </List>
        ];
    }
}

Teams.defaultProps = { teams: [] };

Teams.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Teams);
