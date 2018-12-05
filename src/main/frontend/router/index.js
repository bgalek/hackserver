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
    {path: '/experiments', component: Experiments, meta: {authRequired: true}},
    {path: '/experiments/create', component: CreateExperimentPage, meta: {authRequired: true}},
    {path: '/experiments/:experimentId', component: Experiment, name: 'experiment', meta: {authRequired: true}},
    {path: '/cookie_baker', component: CookieBaker},
    {path: '/404', component: NotFound},
    {path: '/', redirect: '/experiments'},
    {path: '/*', component: NotFound}
  ]
})
