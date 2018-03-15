import Vue from 'vue'
import Router from 'vue-router'
import Experiments from '../components/Experiments'
import NotFound from '../components/NotFound'
import Experiment from '../components/experiment/Experiment'
import CookieBaker from '../components/CookieBaker'
import CreateExperimentPage from '../components/CreateExperimentPage'

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/experiments', component: Experiments },
    { path: '/experiments/create', component: CreateExperimentPage },
    { path: '/experiments/:experimentId', component: Experiment, name: 'experiment' },
    { path: '/cookie_baker', component: CookieBaker },
    { path: '/404', component: NotFound },
    { path: '/', redirect: '/experiments' },
    { path: '*', redirect: '/404' }
  ]
})
