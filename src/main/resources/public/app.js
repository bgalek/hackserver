const socket = new SockJS('/gs-guide-websocket');
const stompClient = Stomp.over(socket);
const teams = [];

stompClient.connect({}, frame => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/registration', teams => {
        addTeams(JSON.parse(teams.body));
    });
    stompClient.subscribe('/topic/registration/new', team => {
        addTeam(JSON.parse(team.body));
    });
    stompClient.send("/app/hello");
});

function addTeams(teams) {
    teams.forEach(team => addTeam(team));
}

function addTeam(team) {
    if (teams.length) {
        teams.push(team);
    } else {
        teams.push(Object.assign(team, { active: true }));
    }
}

const vue = new Vue({
    data: { teams },
    watch: {},
    computed: {},
    filters: {},
    methods: {
        selectTeam: function (team) {
            this.$data.teams = this.$data.teams.map(el => Object.assign(el, { active: el.name === team.name }));
        }
    },
    directives: {}
});

vue.$mount(".app");