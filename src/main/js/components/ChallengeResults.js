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
import red from '@material-ui/core/colors/red';
import green from '@material-ui/core/colors/green';
import { bindActionCreators } from "redux";
import PropTypes from "prop-types";

function ChallengeResults({ team, fetchChallengesResults, isLoading, challengesResults }) {
    useEffect(() => {
        fetchChallengesResults(team);
    }, [challengesResults.length]);

    if (isLoading) return <Loader/>;
    return <Table>
        <TableHead>
            <TableRow>
                <TableCell>task</TableCell>
                <TableCell>http status</TableCell>
                <TableCell>latency</TableCell>
                <TableCell>answer</TableCell>
                <TableCell>score</TableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {challengesResults.map((entry, i) => <TableRow key={`log-${i}`}>
                    <TableCell>{entry.taskName}</TableCell>
                    <TableCell>{renderHttpStatus(entry.responseHttpStatus)}</TableCell>
                    <TableCell>{entry.latencyMillis} ms</TableCell>
                    <TableCell>{entry.responseBody}</TableCell>
                    <TableCell>{entry.score}</TableCell>
                </TableRow>
            )}
        </TableBody>
    </Table>;
}

function renderHttpStatus(httpStatus) {
    const icon = httpStatus === 200 ? <DoneIcon/> : <ErrorIcon/>;
    const color = httpStatus === 200 ? green.A100 : red.A100;
    return <Chip style={{ backgroundColor: color }} label={httpStatus} icon={icon}/>;
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