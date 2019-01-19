const socket = new WebSocket('ws://localhost:8080/ws/registrations');
const teams = [];

socket.addEventListener('message', function (event) {
    addTeam(JSON.parse(event.data));
});

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