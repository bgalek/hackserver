import React, { Component } from "react";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import { CHALLENGES_RESULTS_FETCH } from "../actions";
import { connect } from "react-redux";
import { withStyles } from "@material-ui/core";
import Loader from "./Loader";
import Chip from "@material-ui/core/Chip";
import DoneIcon from "@material-ui/icons/Done";
import ErrorIcon from "@material-ui/icons/Error";
import red from '@material-ui/core/colors/red';
import green from '@material-ui/core/colors/green';

const styles = theme => ({});

class ChallengeResults extends Component {

    async componentDidMount() {
        this.props.fetchChallengesResults(this.props.team);
        this.interval = setInterval(() => {
            this.props.fetchChallengesResults(this.props.team)
        }, 3000);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    render() {
        const { isLoading, challengesResults } = this.props;
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
                        <TableCell component="th" scope="row">{entry.taskName}</TableCell>
                        <TableCell>{renderHttpStatus(entry.responseHttpStatus)}</TableCell>
                        <TableCell>{entry.latencyMillis} ms</TableCell>
                        <TableCell>{entry.responseBody}</TableCell>
                        <TableCell>{entry.score}</TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>;
    }
}

function renderHttpStatus(httpStatus) {
    const icon = httpStatus === 200 ? <DoneIcon/> : <ErrorIcon/>;
    const color = httpStatus === 200 ? green.A100 : red.A100;
    return <Chip style={{ backgroundColor: color }} label={httpStatus} icon={icon}/>;
}

const mapStateToProps = (state) => ({
    challengesResults: state.results.data,
    isLoading: state.results.isLoading
});

const mapDispatchToProps = (dispatch) => ({
    fetchChallengesResults: (team) => dispatch(CHALLENGES_RESULTS_FETCH(team))
});

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(ChallengeResults));