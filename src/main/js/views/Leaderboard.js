import React, { useEffect } from 'react';
import Typography from '@material-ui/core/Typography';
import { connect } from "react-redux";
import { SCORES_FETCH } from "../actions";
import { bindActionCreators } from "redux";
import { makeStyles } from "@material-ui/core";
import distanceInWordsToNow from "date-fns/distance_in_words_to_now";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";

const useStyles = makeStyles(() => ({
    title: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    }
}));

function Leaderboard({ scores, fetchScores }) {
    const classes = useStyles();
    useEffect(() => {
        fetchScores();
    }, [scores.length]);
    return [
        <div key="title" className={classes.title}>
            <Typography variant="h4" gutterBottom>
                Leaderboard
            </Typography>
            <Typography>
                {distanceInWordsToNow(new Date(scores.updatedAt), { addSuffix: true })}
            </Typography>
        </div>,
        <Table key="team-scores">
            <TableHead>
                <TableRow>
                    <TableCell style={{ padding: 0, textAlign: 'center' }}>#</TableCell>
                    <TableCell>Team</TableCell>
                    <TableCell>Score</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {scores.scores.map((score, i) =>
                    <TableRow key={score.teamId}>
                        <TableCell style={{ padding: 0, textAlign: 'center' }}>{++i}</TableCell>
                        <TableCell component="th" scope="row">
                            {i === 1 ? <Typography variant="h4">{score.teamId}</Typography> : ""}
                            {i === 2 ? <Typography variant="h5">{score.teamId}</Typography> : ""}
                            {i === 3 ? <Typography variant="h6">{score.teamId}</Typography> : ""}
                            {i > 3 ? <Typography variant="body1">{score.teamId}</Typography> : ""}
                        </TableCell>
                        <TableCell>
                            {i === 1 ? <Typography variant="h4">{score.score}</Typography> : ""}
                            {i === 2 ? <Typography variant="h5">{score.score}</Typography> : ""}
                            {i === 3 ? <Typography variant="h6">{score.score}</Typography> : ""}
                            {i > 3 ? <Typography variant="body1">{score.score}</Typography> : ""}
                        </TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>
    ];
}

Leaderboard.defaultProps = { scores: { scores: [] } };

Leaderboard.propTypes = {};

const mapStateToProps = (state) => ({
    scores: state.scores.data,
    isLoading: state.teams.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({ fetchScores: SCORES_FETCH }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(Leaderboard);
