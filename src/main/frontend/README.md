# chi-admin

The user interface for experiments management.

* PROD - http://chi.allegrogroup.com/
* TEST - http://chi-test.allegrogroup.com/
* DEV - http://chi-dev.allegrogroup.com/

## Development

### Prerequisites

You need to have [Node.js](https://nodejs.org/) and [yarn](https://yarnpkg.com/) installed to start the development.

Local **chi-server** app nned to be running at http://localhost:8080 for interactions with its **/api** endpoints.

### Workflow

```bash
# install dependencies
npm install yarn

# serve with hot reload at localhost:4000
node_modules/.bin/poi

# build for production with minification
node_modules/.bin/poi build
```

### Used tools

* https://vuejs.org/ - JavaScript framework
* https://vuetifyjs.com/ - material component framework
* [vuex-rest-api](https://github.com/christianmalek/vuex-rest-api) - REST APIs with Vuex
* https://yarnpkg.com/ - dependency management
* https://poi.js.org/ - config-free build and live reloading