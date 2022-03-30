import React from 'react';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles(theme => ({
    header: { marginTop: theme.spacing(2) },
    paragraph: { marginBottom: theme.spacing(2) }
}));

function About() {
    const styles = useStyles();
    return [
        <Typography key="title" variant="h4" gutterBottom className={styles.header}>
            About
        </Typography>,
        <Typography key="about-text" variant="body1" gutterBottom className={styles.paragraph}>
            Welcome! We&apos;re going to have lots of fun coding and testing!<br/>
        </Typography>,
        <Typography key="title-registration" variant="h5" gutterBottom className={styles.header}>
            Team Registration
        </Typography>,
        <Typography key="registration" variant="body1" gutterBottom>
            To register yourself you need to send <strong>POST</strong>, with <strong>application/json</strong>
            <pre>{`{"name":"Bartosz", "port":"8080"}`}</pre>
            to the <code>/registration</code> endpoint.
        </Typography>,
        <Typography key="registration-secret" variant="body1">
            Upon registration you will receive your <span style={{color: 'red'}}>secret key</span> - don&apos;t
            lose it!
        </Typography>,

        <Typography key="title-health" variant="h5" gutterBottom className={styles.header}>
            Health endpoint
        </Typography>,
        <Typography key="health" variant="body1" gutterBottom>
            To participate in the game you have start a web server on a port provided during registration and
            configure the <code>/status/health</code> endpoint returning <code>200 OK</code> http status.
            After that you will see your team name with a green &quot;healthy&quot; badge at <a href="/#/teams">Teams
            list</a>.
        </Typography>,

        <Typography key="title-tasks" variant="h5" gutterBottom className={styles.header}>
            Tasks
        </Typography>,
        <Typography key="tasks" variant="body1">
            Your task is to write a webserver, which will be queried for some answers. Try to be creative, you can:
            <ul>
                <li style={{margin: 10}}>Try to max out points using your favourite tools - it won&apos;t be easy ;)
                </li>
                <li style={{margin: 10}}>Or use a new programming language/framework</li>
                <ul style={{margin: 10}}>
                    <li>Micronaut - <a href="https://micronaut.io">https://micronaut.io</a></li>
                    <li>Blade - <a href="https://lets-blade.com">https://lets-blade.com</a></li>
                    <li>Vert.x<a href="https://vertx.io/"> - https://vertx.io/</a></li>
                    <li>Axon - <a href="https://axoniq.io/">https://axoniq.io/</a></li>
                    <li>Quarkus - <a href="https://quarkus.io">https://quarkus.io</a></li>
                    <li>Beego - <a href="https://beego.me">https://beego.me</a></li>
                    <li>Gobuffalo - <a href="https://gobuffalo.io">https://gobuffalo.io</a></li>
                    <li>Echo - <a href="https://echo.labstack.com">https://echo.labstack.com</a></li>
                    <li>Iris - <a href="https://iris-go.com">https://iris-go.com</a></li>
                    <li>CherryPy - <a href="https://cherrypy.org/">https://cherrypy.org/</a></li>
                    <li>Bottle - <a href="https://bottlepy.org/docs/dev/#">https://bottlepy.org/docs/dev/#</a></li>
                    <li>Flask - <a href="http://flask.pocoo.org/">http://flask.pocoo.org/</a></li>
                    <li>Express - <a href="https://expressjs.com/">https://expressjs.com/</a></li>
                    <li>Koa - <a href="https://koajs.com/">https://koajs.com/</a></li>
                </ul>
            </ul>
        </Typography>,
    ];
}

About.defaultProps = { teams: [] };

About.propTypes = {};

export default About;
