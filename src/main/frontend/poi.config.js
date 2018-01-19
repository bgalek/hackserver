const appState = require('./fake-api').appState
const FAKE_API = false

module.exports = (options, req) => ({
  devServer: {
    proxy: "http://localhost:8080/api",

    before(app) {
      if (FAKE_API) {
        app.get('/api/statistics/:experimentId', (req, res) => {
          let device = req.query.device ? req.query.device: 'all'
          let toDate = req.query.toDate ? new Date(req.query.toDate): appState.getExperiment(req.params.experimentId).toDate
          res.end(JSON.stringify(appState.getStatistics(req.params.experimentId, device, toDate)))
        })

        app.get('/api/admin/experiments', (req, res) => {
          res.end(JSON.stringify(appState.getExperiments()))
        })

        app.get('/api/admin/experiments/:experimentId', (req, res) => {
          res.end(JSON.stringify(appState.getExperiment(req.params.experimentId)))
        })
      }
    },

  },
  entry: './index.js',
  templateCompiler: true,
  html: {
    template: "index.html"
  },
  dist: "../../../public",
  presets: [
    "eslint",
    "transform-test-files"
  ],
})
