import React, { useEffect } from "react";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import { CHALLENGES_RESULTS_FETCH } from "../actions";
import { connect } from "react-redux";
import Loader from "./Loader";
import Chip from "@material-ui/core/Chip";
import DoneIcon from "@material-ui/icons/Done";
import ErrorIcon from "@material-ui/icons/Error";
import ReportIcon from "@material-ui/icons/Report";
import red from '@material-ui/core/colors/red';
import green from '@material-ui/core/colors/green';
import orange from '@material-ui/core/colors/orange';
import { bindActionCreators } from "redux";
import PropTypes from "prop-types";
import { Typography } from "@material-ui/core";
import useMediaQuery from '@material-ui/core/useMediaQuery';

function ChallengeResults({ team, fetchChallengesResults, isLoading, challengesResults }) {
    useEffect(() => {
        fetchChallengesResults(team);
    }, [challengesResults.length]);
    const matches = useMediaQuery('(min-width:600px)');

    if (isLoading) return <Loader/>;
    return <Table>
        <TableHead>
            <TableRow>
                <TableCell>challenge</TableCell>
                {matches ? <TableCell>task</TableCell> : undefined}
                <TableCell style={{ width: '1rem' }}>http status</TableCell>
                {matches ? <TableCell style={{ width: '1rem' }}>latency</TableCell> : undefined}
                <TableCell style={{ width: '1rem' }}>score</TableCell>
                <TableCell>answer</TableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {challengesResults.map(entry => <TableRow key={entry.taskName}>
                    <TableCell><Typography variant="caption">{entry.challengeId}</Typography></TableCell>
                    {matches ? <TableCell>{entry.taskName}</TableCell> : undefined}
                    <TableCell>{renderHttpStatus(entry.responseHttpStatus)}</TableCell>
                    {matches ? <TableCell>{entry.latencyMillis} ms</TableCell> : undefined}
                    <TableCell>{entry.score}</TableCell>
                    <TableCell>{renderScore(entry.score, entry.responseBody)}</TableCell>
                </TableRow>
            )}
        </TableBody>
    </Table>;
}

function renderHttpStatus(httpStatus) {
    const states = {
        200: { color: green.A700, icon: <DoneIcon style={{ color: "white" }}/> },
        404: { color: orange.A400, icon: <ReportIcon style={{ color: "white" }}/> },
        error: { color: red.A200, icon: <ErrorIcon style={{ color: "white" }}/> }
    };
    const state = states[httpStatus] || states.error;
    return <Chip style={{ color: "white", backgroundColor: state.color }} label={httpStatus} icon={state.icon}/>;
}

function renderScore(score, value) {
    if(score){
        return <Chip style={{ color: "white", backgroundColor: green.A700, maxWidth: "150px" }} label={value} icon={<DoneIcon style={{ color: "white" }}/>}/>;
    }
    return <Chip style={{ color: "white", backgroundColor: red.A200, maxWidth: "150px" }} label={value || 'error'} icon={<ErrorIcon style={{ color: "white" }}/>}/>;
}

ChallengeResults.propTypes = {
    team: PropTypes.string.isRequired,
    fetchChallengesResults: PropTypes.func.isRequired,
    isLoading: PropTypes.bool.isRequired,
    challengesResults: PropTypes.array.isRequired
};

const mapStateToProps = (state) => ({
    challengesResults: state.results.data,
    isLoading: state.results.isLoading
});

const mapDispatchToProps = dispatch => bindActionCreators({ fetchChallengesResults: CHALLENGES_RESULTS_FETCH }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(ChallengeResults);
