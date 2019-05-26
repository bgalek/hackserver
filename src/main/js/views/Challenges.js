import React, { useEffect, useState } from 'react';
import Typography from '@material-ui/core/Typography';
import { CHALLENGES_FETCH } from "../actions";
import { connect } from "react-redux";
import Loader from "./Teams";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import AccessAlarmsIcon from '@material-ui/icons/AccessAlarms';
import Dialog from "@material-ui/core/Dialog";
import ChallengeDetail from "./ChallengeDetail";
import { bindActionCreators } from "redux";
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles(() => ({
    dialog: {
        backgroundColor: '#fafafa',
    }
}));

function Challenges({ fetchChallenges, challenges, isLoading }) {
    const classes = useStyles();
    const [modal, setModal] = useState({ open: false, selected: null });
    useEffect(() => {
        fetchChallenges();
    }, [challenges.length]);
    if (isLoading) return <Loader/>;
    return [
        <Typography key="title" variant="h4" gutterBottom>
            Challenges
        </Typography>,
        <List key="challenge-list">
            {challenges.filter(it => it.active).map((challenge, index) =>
                <ListItem button key={challenge.name + index} onClick={() => {
                    setModal(({ open: true, selected: challenge }));
                }}>
                    <ListItemAvatar>
                        <Avatar>
                            <AccessAlarmsIcon/>
                        </Avatar>
                    </ListItemAvatar>
                    <ListItemText primary={challenge.name}/>
                </ListItem>
            )}
        </List>,
        <Dialog key="challenge-details-modal" fullScreen open={modal.open}
                onClose={() => setModal((prevState) => ({ ...prevState, open: false }))}
                className={classes.dialog}>
            <ChallengeDetail onClose={() => setModal((prevState) => ({ ...prevState, open: false }))}
                             challenge={modal.selected}/>
        </Dialog>
    ];
}

const mapStateToProps = (state) => ({
    challenges: state.challenges.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({ fetchChallenges: CHALLENGES_FETCH }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(Challenges);
