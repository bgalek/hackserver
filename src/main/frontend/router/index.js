import Vue from 'vue'
import Router from 'vue-router'
import Experiments from '../components/Experiments'
import NotFound from '../components/NotFound'
import LoginFailed from '../components/LoginFailed'
import Experiment from '../components/experiment/Experiment'
import CookieBaker from '../components/CookieBaker'
import CreateExperimentPage from '../components/CreateExperimentPage'

Vue.use(Router)

export default function createRouter (store) {
  const router = new Router({
    routes: [
      {path: '/experiments', component: Experiments, meta: {authRequired: true}},
      {path: '/experiments/create', component: CreateExperimentPage, meta: {authRequired: true}},
      {path: '/experiments/:experimentId', component: Experiment, name: 'experiment', meta: {authRequired: true}},
      {path: '/cookie_baker', component: CookieBaker},
      {path: '/404', component: NotFound},
      {path: '/loginFailed', component: LoginFailed},
      {path: '/', redirect: '/experiments'},
      {path: '*', redirect: '/404'}
    ]
  })

  router.beforeEach((to, from, next) => {
    const authRequired = to.matched.some(record => record.meta && record.meta.authRequired)
    const isAuthenticated = store.getters.isAuthenticated()
    console.log('requested route: ' + to.fullPath + ', authRequired: ' + authRequired + ', isAuthenticated: ' + isAuthenticated)

    if (authRequired && !isAuthenticated) {
      store.dispatch('login').then(() => {
        if (store.getters.isAuthenticated()) {
          next()
        } else {
          next({
            path: '/loginFailed'
          })
        }
      })
    } else {
      next()
    }
  })

  return router
}
