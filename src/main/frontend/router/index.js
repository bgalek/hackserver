import Vue from 'vue'
import Router from 'vue-router'
import Experiments from '../components/Experiments'
import Experiment from '../components/Experiment'

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/experiments', component: Experiments },
    { path: '/experiments/:experimentId', component: Experiment },
    { path: '*', redirect: '/experiments' }
  ]
})
