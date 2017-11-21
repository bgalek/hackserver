import Vue from 'vue'
import Router from 'vue-router'
import Experiments from '../components/Experiments'
import CookieBaker from '../components/CookieBaker'

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/experiments', component: Experiments },
    { path: '/cookie_baker', component: CookieBaker },
    { path: '*', redirect: '/experiments' }
  ]
})
