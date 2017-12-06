import Vue from 'vue'
import Router from 'vue-router'
import Experiments from '../components/Experiments'
import Experiment from '../components/Experiment'
import ExperimentDetails from '../components/ExperimentDetails'
import CookieBaker from '../components/CookieBaker'

Vue.use(Router)
Vue.component('experiment-datails', ExperimentDetails)

export default new Router({
  routes: [
    { path: '/experiments', component: Experiments },
    { path: '/experiments/:experimentId', component: Experiment, name: 'experiment' },
    { path: '/cookie_baker', component: CookieBaker },
    { path: '*', redirect: '/experiments' }
  ]
})
